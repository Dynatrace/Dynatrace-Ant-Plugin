package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtRestartCollector extends DtServerBase {
	private boolean restart = true;
	private String collector;
	
	public void execute() throws BuildException {
		if(restart)
			getEndpoint().restartCollector(getCollector());
		else
			getEndpoint().shutdownCollector(getCollector());
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	public void setCollector(String collector) {
		this.collector = collector;
	}

	public String getCollector() {
		return collector;
	}
}
