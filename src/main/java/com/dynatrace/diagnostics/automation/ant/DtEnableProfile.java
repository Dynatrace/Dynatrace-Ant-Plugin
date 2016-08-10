package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtEnableProfile extends DtServerProfileBase {

	private boolean enable;
	
	public void execute() throws BuildException {
		getEndpoint().enableProfile(getProfileName(), isEnable());
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}
}
