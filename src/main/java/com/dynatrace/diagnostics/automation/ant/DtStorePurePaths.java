package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;


public class DtStorePurePaths extends DtServerProfileBase{
	private String recordingOption;
	private boolean sessionLocked;
	private boolean appendTimestamp;

	@Override
	public void execute() throws BuildException {
		getEndpoint().storePurePaths(getProfileName(), getRecordingOption(), isSessionLocked(), isAppendTimestamp());
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
}
