package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtClearSession extends DtServerProfileBase {

	public void execute() throws BuildException {
		getEndpoint().clearSession(getProfileName());
	}	
}
