package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StartRecordingRequest;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to start session recording via the server REST-interface
 *
 * @author andreas.grabner
 * @author cwat-ruttenth
 */
public class DtStartRecording extends DtServerProfileBase {

    private String sessionName;
    private String sessionDescription;
    private String recordingOption;
    private String sessionNameProperty;
    private boolean sessionLocked;
    private boolean appendTimestamp;

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
            String sessionName = sessions.startRecording(startRecordingRequest);

            log("Started recording on " + getProfileName() + " with SessionName " + sessionName); //$NON-NLS-1$ //$NON-NLS-2$

            if (sessionNameProperty != null && sessionNameProperty.length() > 0) {
                this.getProject().setProperty(sessionNameProperty, sessionName);
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
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
    public String getSessionNameProperty() {
        if (sessionNameProperty == null) {
            String dtSessionNameProperty = this.getProject().getProperty("dtSessionNameProperty"); //$NON-NLS-1$
            if (dtSessionNameProperty != null && dtSessionNameProperty.length() > 0)
                sessionNameProperty = dtSessionNameProperty;
        }
        return sessionNameProperty;
    }

    public void setSessionNameProperty(String sessionNameProperty) {
        this.sessionNameProperty = sessionNameProperty;
    }
}
