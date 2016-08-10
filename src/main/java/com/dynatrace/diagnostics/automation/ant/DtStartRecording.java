package com.dynatrace.diagnostics.automation.ant;

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
		String sessionName = getEndpoint().startRecording(getProfileName(), getSessionName(), getSessionDescription(), getRecordingOption(), isSessionLocked(), !isAppendTimestamp());

		log("Started recording on " + getProfileName() + " with SessionName " + sessionName); //$NON-NLS-1$ //$NON-NLS-2$

		if(sessionNameProperty != null && sessionNameProperty.length() > 0)
			this.getProject().setProperty(sessionNameProperty, sessionName);
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionDescription(String sessionDescription) {
		this.sessionDescription = sessionDescription;
	}

	public String getSessionDescription() {
		return sessionDescription;
	}

	public void setRecordingOption(String recordingOption) {
		this.recordingOption = recordingOption;
	}

	public String getRecordingOption() {
		return recordingOption;
	}

	public void setSessionLocked(boolean sessionLocked) {
		this.sessionLocked = sessionLocked;
	}

	public boolean isSessionLocked() {
		return sessionLocked;
	}

	public void setAppendTimestamp(boolean appendTimestamp) {
		this.appendTimestamp = appendTimestamp;
	}

	public boolean isAppendTimestamp() {
		return appendTimestamp;
	}

	public void setSessionNameProperty(String sessionNameProperty) {
		this.sessionNameProperty = sessionNameProperty;
	}

	/**
	 *
	 * @return the name of the session the recording is started
	 */
	public String getSessionNameProperty() {
		if(sessionNameProperty == null) {
				String dtSessionNameProperty = this.getProject().getProperty("dtSessionNameProperty"); //$NON-NLS-1$
				if(dtSessionNameProperty != null && dtSessionNameProperty.length() > 0)
					sessionNameProperty = dtSessionNameProperty;
		}
		return sessionNameProperty;
	}
}
