package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildException;

import com.dynatrace.diagnostics.automation.rest.sdk.RESTEndpoint;


public class DtSensorPlacement extends DtServerBase{
	private int agentId;

	@Override
	public void execute() throws BuildException {
		RESTEndpoint endpoint=getEndpoint();
		endpoint.hotSensorPlacement(agentId);
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
}
