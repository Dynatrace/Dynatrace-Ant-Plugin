package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtActivateConfiguration extends DtServerProfileBase {

	private String configuration;
	
	public void execute() throws BuildException {
		getEndpoint().activateConfiguration(getProfileName(), getConfiguration());
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return configuration;
	}
}
