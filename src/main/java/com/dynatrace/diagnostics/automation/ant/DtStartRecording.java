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
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StartRecordingRequest;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to start session recording via the server REST-interface
 */
public class DtStartRecording extends DtServerProfileBase {

    private String sessionName;
    private String sessionDescription;
    private String recordingOption;
    private String sessionUriProperty;
    private boolean sessionLocked;
    private boolean appendTimestamp;

    /**
     * Executes ant task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @Override
    public void execute() throws BuildException {
        Sessions sessions = new Sessions(this.getDynatraceClient());

        StartRecordingRequest startRecordingRequest = new StartRecordingRequest(this.getProfileName());
        startRecordingRequest.setPresentableName(this.getSessionName());
        startRecordingRequest.setDescription(this.getSessionDescription());
        startRecordingRequest.setSessionLocked(this.isSessionLocked());
        startRecordingRequest.setTimestampAllowed(this.isAppendTimestamp());

        if (this.getRecordingOption() != null) {
            startRecordingRequest.setRecordingOption(RecordingOption.fromInternal(this.getRecordingOption()));
        }

        try {
            String sessionUri = sessions.startRecording(startRecordingRequest);
            this.log(String.format("Started recording on %s system profile with session URI %s", this.getProfileName(), sessionUri));

            if (!DtUtil.isEmpty(this.getSessionUriProperty())) {
                this.getProject().setProperty(this.getSessionUriProperty(), sessionUri);
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(String.format("Error while trying to start recording in '%s' system profile: %s", this.getProfileName(), e.getMessage()), e);
        }
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionDescription() {
        return sessionDescription;
    }

    public void setSessionDescription(String sessionDescription) {
        this.sessionDescription = sessionDescription;
    }

    public String getRecordingOption() {
        return recordingOption;
    }

    public void setRecordingOption(String recordingOption) {
        this.recordingOption = recordingOption;
    }

    public boolean isSessionLocked() {
        return sessionLocked;
    }

    public void setSessionLocked(boolean sessionLocked) {
        this.sessionLocked = sessionLocked;
    }

    public boolean isAppendTimestamp() {
        return appendTimestamp;
    }

    public void setAppendTimestamp(boolean appendTimestamp) {
        this.appendTimestamp = appendTimestamp;
    }

    /**
     * @return the name of the session the recording is started
     */
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
}
