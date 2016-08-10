package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class DtStopRecording extends DtServerProfileBase {

	private String sessionNameProperty;
	private boolean doReanalyzeSession = false;
	
	private int reanalyzeSessionTimeout = 60000;
	private int reanalyzeSessionPollingInterval = 5000;
	private int stopDelay=0;
	private String reanalyzeStatusProperty;
	private boolean failOnError=true;

	@Override
	public void execute() throws BuildException {
		try {
			Thread.sleep(stopDelay);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		String sessionName=null;
		try {
			sessionName = getEndpoint().stopRecording(getProfileName());

			log(String.format("Stopped recording on %1$s with SessionName %2$s", getProfileName(), sessionName)); //$NON-NLS-1$ 

			if (getSessionNameProperty() != null && getSessionNameProperty().length() > 0)
				this.getProject().setProperty(getSessionNameProperty(), sessionName);

			if (doReanalyzeSession) {
				boolean reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(sessionName);
				if (getEndpoint().reanalyzeSession(sessionName)) {
					int timeout = reanalyzeSessionTimeout;
					while (!reanalyzeFinished && (timeout > 0)) {
						try {
							java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
							timeout -= getReanalyzeSessionPollingInterval();
						} catch (InterruptedException e) {
						}

						reanalyzeFinished = getEndpoint().reanalyzeSessionStatus(sessionName);
					}
				}

				if (getReanalyzeStatusProperty() != null && getReanalyzeStatusProperty().length() > 0)
					this.getProject().setProperty(getReanalyzeStatusProperty(), String.valueOf(reanalyzeFinished));
			}
		} catch (RuntimeException e) {
			if (isFailOnError()) {
				throw e;
			}
			log(String.format(
					"Caught exception while Stopping session recording of session %1$s on profile %2$s. Since failOnError==true ignoring this exception.\n\tException message: %3$s", sessionName,getProfileName(),e.getMessage()), e, Project.MSG_WARN); //$NON-NLS-1$
		}
	}

	public void setSessionNameProperty(String sessionNameProperty) {
		this.sessionNameProperty = sessionNameProperty;
	}

	public String getSessionNameProperty() {
		if(sessionNameProperty == null) {
			if(sessionNameProperty == null) {
				String dtSessionNameProperty = this.getProject().getProperty("dtSessionNameProperty"); //$NON-NLS-1$
				if(dtSessionNameProperty != null && dtSessionNameProperty.length() > 0)
					sessionNameProperty = dtSessionNameProperty;
			}			
		}		
		return sessionNameProperty;
	}

	public void setDoReanalyzeSession(boolean doReanalyzeSession) {
		this.doReanalyzeSession = doReanalyzeSession;
	}

	public boolean isDoReanalyzeSession() {
		return doReanalyzeSession;
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

	public void setReanalyzeSessionPollingInterval(
			int reanalyzeSessionPollingInterval) {
		this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
	}

	public int getReanalyzeSessionPollingInterval() {
		return reanalyzeSessionPollingInterval;
	}

	public int getStopDelay() {
		return stopDelay;
	}

	public void setStopDelay(int stopDelay) {
		this.stopDelay = stopDelay;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}
}
