/*
 * Dynatrace Ant Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.agentsandcollectors.models.AgentInformation;
import com.dynatrace.sdk.server.agentsandcollectors.models.Agents;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.List;

/**
 * Ant task for getting information about agent
 */
public class DtGetAgentInfo extends DtServerBase {

    private String agentCountProperty;
    private String agentNameProperty;
    private String agentHostNameProperty;
    private String agentProcessIdProperty;
    private int infoForAgentByIndex = -1;
    private String infoForAgentByName;

    /**
     * Executes ant task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @Override
    public void execute() throws BuildException {
        this.log(String.format("Execute with %s %s %s %s", this.agentCountProperty, this.getUsername(), this.getPassword(), this.getServerUrl()));

        AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());

        try {
            Agents agentsContainer = agentsAndCollectors.fetchAgents();
            List<AgentInformation> agentsList = agentsContainer.getAgents();

            if (!DtUtil.isEmpty(this.agentCountProperty)) {
                this.log(String.format("Set %s to %s", this.agentCountProperty, String.valueOf(agentsList.size())));
                this.getProject().setProperty(this.agentCountProperty, String.valueOf(agentsList.size()));
            }

            AgentInformation agent = this.getAgentInformationByNameOrAtIndex(agentsList, this.infoForAgentByName, this.infoForAgentByIndex);

            if (agent != null) {
                this.log(String.format("Return agent info: %s/%s/%s", agent.getName(), agent.getHost(), agent.getProcessId()));

                Project project = this.getProject();

                /* when properties below aren't defined, throws @NullPointerException - it's purposed behavior? */
                project.setProperty(getAgentNameProperty(), agent.getName());
                project.setProperty(getAgentHostNameProperty(), agent.getHost());
                project.setProperty(getAgentProcessIdProperty(), String.valueOf(agent.getProcessId()));
            }

        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(String.format("Error while trying to get information about agent: %s", e.getMessage()), e);
        }
    }


    /**
     * Returns agent information by given name and index found in list
     * <p>
     * If agent with given name was found, returns it, otherwise looks for agent at given index
     *
     * @param agents - list containing {@link AgentInformation}
     * @param name   - agent name to find in given list
     * @param index  - agent index to find in given list
     * @return {@link AgentInformation} that matches given index or name if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationByNameOrAtIndex(List<AgentInformation> agents, String name, int index) {
        AgentInformation agentInformation = this.getAgentInformationByName(agents, name);

        if (agentInformation != null) {
            return agentInformation;
        }

        return this.getAgentInformationAtIndex(agents, index);
    }

    /**
     * Returns agent information by index found in given list
     *
     * @param agents - list containing {@link AgentInformation}
     * @param index  - agent index to find in given list
     * @return {@link AgentInformation} that matches given index if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationAtIndex(List<AgentInformation> agents, int index) {
        if (index >= 0 && index < agents.size()) {
            return agents.get(index);
        }

        return null;
    }

    /**
     * Returns agent information by name found in given list
     *
     * @param agents - list containing {@link AgentInformation}
     * @param name   - agent name to find in given list
     * @return {@link AgentInformation} that matches given name if it's found, otherwise returns {@code null}
     */
    private AgentInformation getAgentInformationByName(List<AgentInformation> agents, String name) {
        if (name != null) {
            for (AgentInformation agent : agents) {
                if (agent.getName().equalsIgnoreCase(name)) {
                    return agent;
                }
            }
        }

        return null;
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
