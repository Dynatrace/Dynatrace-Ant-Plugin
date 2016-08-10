package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtReanalyzeSession extends DtServerBase {

	private String sessionName;
	private int reanalyzeSessionTimeout = 60000;
	private int reanalyzeSessionPollingInterval = 5000;
	private String reanalyzeStatusProperty;

	public void execute() throws BuildException {
		boolean reanalyzeFinished = false;
		
		if(getEndpoint().reanalyzeSession(getSessionName())) {		
			int timeout = reanalyzeSessionTimeout;
			reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(getSessionName());
			while(!reanalyzeFinished && (timeout > 0)) {
				try {
					java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
					timeout -= getReanalyzeSessionPollingInterval();
				} catch (InterruptedException e) {
				}
				
				reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(getSessionName());
			}
		}
		
		if(getReanalyzeStatusProperty() != null && getReanalyzeStatusProperty().length() > 0)
			this.getProject().setProperty(getReanalyzeStatusProperty(), String.valueOf(reanalyzeFinished));
	}

	public void setReanalyzeSessionTimeout(int reanalyzeSessionTimeout) {
		this.reanalyzeSessionTimeout = reanalyzeSessionTimeout;
	}

	public int getReanalyzeSessionTimeout() {
		return reanalyzeSessionTimeout;
	}

	public void setReanalyzeStatusProperty(String reanalyzeStatusProperty) {
		this.reanalyzeStatusProperty = reanalyzeStatusProperty;
	}

	public String getReanalyzeStatusProperty() {
		return reanalyzeStatusProperty;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setReanalyzeSessionPollingInterval(
			int reanalyzeSessionPollingInterval) {
		this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
	}

	public int getReanalyzeSessionPollingInterval() {
		return reanalyzeSessionPollingInterval;
	}	
}
