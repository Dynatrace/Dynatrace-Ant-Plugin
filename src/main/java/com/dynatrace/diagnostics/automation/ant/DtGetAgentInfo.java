package com.dynatrace.diagnostics.automation.ant;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.Agent;

public class DtGetAgentInfo extends DtServerBase {

	private String agentCountProperty;
	private String agentNameProperty;
	private String agentHostNameProperty;
	private String agentProcessIdProperty;
	private int infoForAgentByIndex = -1;
	private String infoForAgentByName;
	
	public void execute() throws BuildException {
		System.out.println("Execute with " + agentCountProperty + " " + getUsername() + " " + getPassword() + " " + getServerUrl()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		ArrayList<Agent> agents = getEndpoint().getAgents();
		if(agentCountProperty != null && agentCountProperty.length() > 0) {
			System.out.println("Set AgentCount to " + String.valueOf(agents.size())); //$NON-NLS-1$
			this.getProject().setProperty(agentCountProperty, String.valueOf(agents.size()));
		}

		Agent agentForInfo = null;
		if(infoForAgentByIndex >= 0 && infoForAgentByIndex < agents.size()) {
			agentForInfo = agents.get(infoForAgentByIndex);
		}
		if(infoForAgentByName != null) {
			for(Agent agent : agents) {
				if(agent.getName().equalsIgnoreCase(infoForAgentByName))
					agentForInfo = agent;				
			}
		}

		if(agentForInfo != null) {
			Project project = this.getProject();
			project.setProperty(getAgentNameProperty(), agentForInfo.getName());
			project.setProperty(getAgentHostNameProperty(), agentForInfo.getHost());
			project.setProperty(getAgentProcessIdProperty(), String.valueOf(agentForInfo.getProcessId()));
		}
	}

	public void setAgentCountProperty(String agentCountProperty) {
		this.agentCountProperty = agentCountProperty;
	}

	public String getAgentCountProperty() {
		return agentCountProperty;
	}

	public void setInfoForAgentByIndex(int infoForAgentByIndex) {
		this.infoForAgentByIndex = infoForAgentByIndex;
	}

	public int getInfoForAgentByIndex() {
		return infoForAgentByIndex;
	}

	public void setInfoForAgentByName(String infoForAgentByName) {
		this.infoForAgentByName = infoForAgentByName;
	}

	public String getInfoForAgentByName() {
		return infoForAgentByName;
	}

	public void setAgentNameProperty(String agentNameProperty) {
		this.agentNameProperty = agentNameProperty;
	}

	public String getAgentNameProperty() {
		return agentNameProperty;
	}

	public void setAgentHostNameProperty(String agentHostNameProperty) {
		this.agentHostNameProperty = agentHostNameProperty;
	}

	public String getAgentHostNameProperty() {
		return agentHostNameProperty;
	}

	public void setAgentProcessIdProperty(String agentProcessIdProperty) {
		this.agentProcessIdProperty = agentProcessIdProperty;
	}

	public String getAgentProcessIdProperty() {
		return agentProcessIdProperty;
	}
}
