package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtRestartServer extends DtServerBase {
	private boolean restart = true;
	
	public void execute() throws BuildException {
		if(restart)
			getEndpoint().restartServer();
		else
			getEndpoint().shutdownServer();
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}
}
