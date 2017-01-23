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

    private String sessionUriProperty;
    private int stopDelay = 0;
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

            if (!DtUtil.isEmpty(this.getSessionUriProperty())) {
                this.getProject().setProperty(this.getSessionUriProperty(), sessionName);
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

    public String getSessionUriProperty() {
        if (this.sessionUriProperty == null) {
            String sessionUriPropertyFromProperty = this.getProject().getProperty("dtSessionUriProperty");

            if (!DtUtil.isEmpty(sessionUriPropertyFromProperty)) {
                this.sessionUriProperty = sessionUriPropertyFromProperty;
            }
        }

        return this.sessionUriProperty;
    }

    public void setSessionUriProperty(String sessionUriProperty) {
        this.sessionUriProperty = sessionUriProperty;
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
