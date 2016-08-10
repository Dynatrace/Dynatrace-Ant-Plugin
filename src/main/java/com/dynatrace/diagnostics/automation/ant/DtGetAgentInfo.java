package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.agentsandcollectors.models.AgentInformation;
import com.dynatrace.sdk.server.agentsandcollectors.models.Agents;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.List;

public class DtGetAgentInfo extends DtServerBase {

    private String agentCountProperty;
    private String agentNameProperty;
    private String agentHostNameProperty;
    private String agentProcessIdProperty;
    private int infoForAgentByIndex = -1;
    private String infoForAgentByName;

    public void execute() throws BuildException {
        System.out.println("Execute with " + agentCountProperty + " " + getUsername() + " " + getPassword() + " " + getServerUrl()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        try {
            AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());
            Agents agentsContainer = agentsAndCollectors.fetchAgents();
            List<AgentInformation> agents = agentsContainer.getAgents();

            if (agentCountProperty != null && agentCountProperty.length() > 0) {
                System.out.println("Set AgentCount to " + String.valueOf(agents.size())); //$NON-NLS-1$
                this.getProject().setProperty(agentCountProperty, String.valueOf(agents.size()));
            }

            AgentInformation agentForInfo = null;
            if (infoForAgentByIndex >= 0 && infoForAgentByIndex < agents.size()) {
                agentForInfo = agents.get(infoForAgentByIndex);
            }
            if (infoForAgentByName != null) {
                for (AgentInformation agent : agents) {
                    if (agent.getName().equalsIgnoreCase(infoForAgentByName))
                        agentForInfo = agent;
                }
            }

            if (agentForInfo != null) {
                Project project = this.getProject();
                project.setProperty(getAgentNameProperty(), agentForInfo.getName());
                project.setProperty(getAgentHostNameProperty(), agentForInfo.getHost());
                project.setProperty(getAgentProcessIdProperty(), String.valueOf(agentForInfo.getProcessId()));
            }

        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public String getAgentCountProperty() {
        return agentCountProperty;
    }

    public void setAgentCountProperty(String agentCountProperty) {
        this.agentCountProperty = agentCountProperty;
    }

    public int getInfoForAgentByIndex() {
        return infoForAgentByIndex;
    }

    public void setInfoForAgentByIndex(int infoForAgentByIndex) {
        this.infoForAgentByIndex = infoForAgentByIndex;
    }

    public String getInfoForAgentByName() {
        return infoForAgentByName;
    }

    public void setInfoForAgentByName(String infoForAgentByName) {
        this.infoForAgentByName = infoForAgentByName;
    }

    public String getAgentNameProperty() {
        return agentNameProperty;
    }

    public void setAgentNameProperty(String agentNameProperty) {
        this.agentNameProperty = agentNameProperty;
    }

    public String getAgentHostNameProperty() {
        return agentHostNameProperty;
    }

    public void setAgentHostNameProperty(String agentHostNameProperty) {
        this.agentHostNameProperty = agentHostNameProperty;
    }

    public String getAgentProcessIdProperty() {
        return agentProcessIdProperty;
    }

    public void setAgentProcessIdProperty(String agentProcessIdProperty) {
        this.agentProcessIdProperty = agentProcessIdProperty;
    }
}
