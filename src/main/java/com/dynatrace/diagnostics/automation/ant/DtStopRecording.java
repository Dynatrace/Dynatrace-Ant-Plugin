/*
 * Dynatrace Ant Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Ant task to stop recording session
 */
public class DtStopRecording extends DtServerProfileBase {

    private String sessionNameProperty;
    private boolean doReanalyzeSession = false;

    private int reanalyzeSessionTimeout = 60000;
    private int reanalyzeSessionPollingInterval = 5000;
    private int stopDelay = 0;
    private String reanalyzeStatusProperty;
    private boolean failOnError = true;

    /**
     * Executes ant task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @Override
    public void execute() throws BuildException {
        try {
            Thread.sleep(this.stopDelay);
        } catch (InterruptedException e) {
            /* don't break execution */
        }

        String sessionName = null;

        try {
            Sessions sessions = new Sessions(this.getDynatraceClient());
            sessionName = sessions.stopRecording(this.getProfileName());

            this.log(String.format("Stopped recording on %1$s with SessionName %2$s", getProfileName(), sessionName));

            if (!DtUtil.isEmpty(this.getSessionNameProperty())) {
                this.getProject().setProperty(this.getSessionNameProperty(), sessionName);
            }

            if (this.doReanalyzeSession) {
                boolean reanalyzeFinished = sessions.getReanalysisStatus(sessionName);

                if (sessions.reanalyze(sessionName)) {
                    int timeout = this.reanalyzeSessionTimeout;

                    while (!reanalyzeFinished && (timeout > 0)) {
                        try {
                            Thread.sleep(this.reanalyzeSessionPollingInterval);
                            timeout -= this.reanalyzeSessionPollingInterval;
                        } catch (InterruptedException e) {
                            /* don't break execution */
                        }

                        reanalyzeFinished = sessions.getReanalysisStatus(sessionName);
                    }
                }

                if (!DtUtil.isEmpty(this.reanalyzeStatusProperty)) {
                    this.getProject().setProperty(this.reanalyzeStatusProperty, String.valueOf(reanalyzeFinished));
                }
            }
        } catch (RuntimeException e) {
            if (this.failOnError) {
                throw e;
            }

            this.log(String.format("Caught exception while Stopping session recording of session %1$s on profile %2$s. Since failOnError==true ignoring this exception.\n\tException message: %3$s", sessionName, getProfileName(), e.getMessage()), e, Project.MSG_WARN);
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public String getSessionNameProperty() {
        if (this.sessionNameProperty == null) {
            String sessionNamePropertyFromProperty = this.getProject().getProperty("dtSessionNameProperty");

            if (!DtUtil.isEmpty(sessionNamePropertyFromProperty)) {
                this.sessionNameProperty = sessionNamePropertyFromProperty;
            }
        }

        return this.sessionNameProperty;
    }

    public void setSessionNameProperty(String sessionNameProperty) {
        this.sessionNameProperty = sessionNameProperty;
    }

    public boolean isDoReanalyzeSession() {
        return doReanalyzeSession;
    }

    public void setDoReanalyzeSession(boolean doReanalyzeSession) {
        this.doReanalyzeSession = doReanalyzeSession;
    }

    public int getReanalyzeSessionTimeout() {
        return reanalyzeSessionTimeout;
    }

    public void setReanalyzeSessionTimeout(int reanalyzeSessionTimeout) {
        this.reanalyzeSessionTimeout = reanalyzeSessionTimeout;
    }

    public String getReanalyzeStatusProperty() {
        return reanalyzeStatusProperty;
    }

    public void setReanalyzeStatusProperty(String reanalyzeStatusProperty) {
        this.reanalyzeStatusProperty = reanalyzeStatusProperty;
    }

    public int getReanalyzeSessionPollingInterval() {
        return reanalyzeSessionPollingInterval;
    }

    public void setReanalyzeSessionPollingInterval(
            int reanalyzeSessionPollingInterval) {
        this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
    }

    public int getStopDelay() {
        return stopDelay;
    }

    public void setStopDelay(int stopDelay) {
        this.stopDelay = stopDelay;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }
}
