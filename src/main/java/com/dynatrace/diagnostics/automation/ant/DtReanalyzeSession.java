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

/**
 * Ant task for reanalyzing session
 */
public class DtReanalyzeSession extends DtServerBase {

    private String sessionName;
    private int reanalyzeSessionTimeout = 60000;
    private int reanalyzeSessionPollingInterval = 5000;
    private String reanalyzeStatusProperty;

    /**
     * Executes ant task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @Override
    public void execute() throws BuildException {
        this.log(String.format("Reanalyzing '%s' session", this.sessionName));

        Sessions sessions = new Sessions(this.getDynatraceClient());

        try {
            boolean reanalyzeFinished = false;

            if (sessions.reanalyze(this.getSessionName())) {
                reanalyzeFinished = sessions.getReanalysisStatus(this.sessionName);

                int timeout = this.reanalyzeSessionTimeout;

                while (!reanalyzeFinished && (timeout > 0)) {
                    try {
                        Thread.sleep(this.reanalyzeSessionPollingInterval);
                        timeout -= this.reanalyzeSessionPollingInterval;
                    } catch (InterruptedException e) {
                        /* don't break execution */
                    }

                    reanalyzeFinished = sessions.getReanalysisStatus(this.sessionName);
                }
            }

            if (!DtUtil.isEmpty(this.reanalyzeStatusProperty)) {
                this.getProject().setProperty(this.reanalyzeStatusProperty, String.valueOf(reanalyzeFinished));
            }

            this.log(String.format("Reanalyzing '%s' session finished %s success ", this.sessionName, (reanalyzeFinished) ? "with" : "without"));
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(String.format("Error while trying to reanalyze '%s' session: %s", this.sessionName, e.getMessage()), e);
        }
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

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getReanalyzeSessionPollingInterval() {
        return reanalyzeSessionPollingInterval;
    }

    public void setReanalyzeSessionPollingInterval(
            int reanalyzeSessionPollingInterval) {
        this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
    }
}
