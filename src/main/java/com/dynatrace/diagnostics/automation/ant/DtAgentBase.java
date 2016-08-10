package com.dynatrace.diagnostics.automation.ant;

public abstract class DtAgentBase extends DtServerProfileBase {

	private String hostName;
	private String agentName;
	private int processId;
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public int getProcessId() {
		return processId;
	}
}
