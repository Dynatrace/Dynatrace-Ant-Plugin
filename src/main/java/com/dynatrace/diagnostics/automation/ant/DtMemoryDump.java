package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

public class DtMemoryDump extends DtAgentBase {

	private String dumpType;
	private boolean sessionLocked;
	private String memoryDumpNameProperty;

	private int waitForDumpTimeout = 60000;
	private int waitForDumpPollingInterval = 5000;
	private String dumpStatusProperty;
	private boolean doGc;
	private boolean autoPostProcess;
	private boolean capturePrimitives;
	private boolean captureStrings;

	@Override
	public void execute() throws BuildException {
		System.out.println("Creating Memory Dump for " + getProfileName() + "-" + getAgentName() + "-" + getHostName() + "-" + getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		String memoryDump = getEndpoint().memoryDump(getProfileName(), getAgentName(), getHostName(), getProcessId(), getDumpType(), isSessionLocked(), getCaptureStrings(), getCapturePrimitives(), getAutoPostProcess(), getDoGc());
		if(memoryDumpNameProperty != null && memoryDumpNameProperty.length() > 0)
			this.getProject().setProperty(memoryDumpNameProperty, memoryDump);

		if(memoryDump == null || memoryDump.length() == 0) {
			throw new BuildException("Memory Dump wasnt taken"); //$NON-NLS-1$
		}
		
		int timeout = waitForDumpTimeout;
		boolean dumpFinished = getEndpoint().memoryDumpStatus(getProfileName(), memoryDump).isResultValueTrue();
		while(!dumpFinished && (timeout > 0)) {
			try {
				java.lang.Thread.sleep(waitForDumpPollingInterval);
				timeout -= waitForDumpPollingInterval;
			} catch (InterruptedException e) {
			}
			
			dumpFinished = getEndpoint().memoryDumpStatus(getProfileName(), memoryDump).isResultValueTrue();
		}
		
		if(dumpStatusProperty != null && dumpStatusProperty.length() > 0)
			this.getProject().setProperty(dumpStatusProperty, String.valueOf(dumpFinished));
	}

	private boolean getCaptureStrings() {
		return captureStrings;
	}

	private boolean getCapturePrimitives() {
		return capturePrimitives;
	}

	private boolean getAutoPostProcess() {
		return autoPostProcess;
	}

	private boolean getDoGc() {
		return doGc;
	}

	public void setDoGc(boolean doGc) {
		this.doGc = doGc;
	}

	public void setAutoPostProcess(boolean autoPostProcess) {
		this.autoPostProcess = autoPostProcess;
	}

	public void setCapturePrimitives(boolean capturePrimitives) {
		this.capturePrimitives = capturePrimitives;
	}

	public void setCaptureStrings(boolean captureStrings) {
		this.captureStrings = captureStrings;
	}

	public void setDumpType(String dumpType) {
		this.dumpType = dumpType;
	}

	public String getDumpType() {
		return dumpType;
	}

	public void setSessionLocked(boolean sessionLocked) {
		this.sessionLocked = sessionLocked;
	}

	public boolean isSessionLocked() {
		return sessionLocked;
	}

	public void setMemoryDumpNameProperty(String memoryDumpNameProperty) {
		this.memoryDumpNameProperty = memoryDumpNameProperty;
	}

	public String getMemoryDumpNameProperty() {
		return memoryDumpNameProperty;
	}

	public void setWaitForDumpTimeout(int waitForDumpTimeout) {
		this.waitForDumpTimeout = waitForDumpTimeout;
	}

	public int getWaitForDumpTimeout() {
		return waitForDumpTimeout;
	}

	public void setWaitForDumpPollingInterval(int waitForDumpPollingInterval) {
		this.waitForDumpPollingInterval = waitForDumpPollingInterval;
	}

	public int getWaitForDumpPollingInterval() {
		return waitForDumpPollingInterval;
	}

	public void setDumpStatusProperty(String dumpStatusProperty) {
		this.dumpStatusProperty = dumpStatusProperty;
	}

	public String getDumpStatusProperty() {
		return dumpStatusProperty;
	}
}
