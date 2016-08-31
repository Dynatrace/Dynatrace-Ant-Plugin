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

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.agentsandcollectors.models.AgentInformation;
import com.dynatrace.sdk.server.agentsandcollectors.models.Agents;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Agents.class, AgentInformation.class, AgentsAndCollectors.class, DtGetAgentInfo.class})
public class DtGetAgentInfoTest extends AbstractDynatraceTest<DtGetAgentInfo> {
    private static final String GET_AGENT_INFO_TARGET_NAME = "getAgentInfo";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtGetAgentInfo.class).withAnyArguments().thenReturn(this.getTask());

        AgentsAndCollectors agentsAndCollectors = spy(new AgentsAndCollectors(this.getTask().getDynatraceClient()));

        AgentInformation agentInformation = spy(new AgentInformation());
        doReturn("custom-name").when(agentInformation).getName();
        doReturn("custom-host").when(agentInformation).getHost();
        doReturn(1234).when(agentInformation).getProcessId();

        AgentInformation agentInformation2 = spy(new AgentInformation());
        doReturn("custom-name-2").when(agentInformation2).getName();
        doReturn("custom-host-2").when(agentInformation2).getHost();
        doReturn(5678).when(agentInformation2).getProcessId();

        ArrayList<AgentInformation> agentInformationList = new ArrayList<>();
        agentInformationList.add(agentInformation);
        agentInformationList.add(agentInformation2);

        Agents agents = spy(new Agents());
        doReturn(agentInformationList).when(agents).getAgents();

        /** define responses */
        doReturn(agents).when(agentsAndCollectors).fetchAgents();

        whenNew(AgentsAndCollectors.class).withAnyArguments().thenReturn(agentsAndCollectors);

        /** verify default values */
        assertThat(this.getTask().getInfoForAgentByIndex(), is(-1));
    }

    @Override
    protected String getTaskTargetName() {
        return GET_AGENT_INFO_TARGET_NAME;
    }

    @Override
    protected DtGetAgentInfo createNewInstanceOfTheTask() {
        return new DtGetAgentInfo();
    }

    @Test
    public void testGetAgentInfoByIndex() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByIndex(0);

            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("agent-process-id");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("agents-count"), is("2"));
            assertThat(this.getTask().getProject().getProperty("agent-name"), is("custom-name"));
            assertThat(this.getTask().getProject().getProperty("agent-hostname"), is("custom-host"));
            assertThat(this.getTask().getProject().getProperty("agent-process-id"), is("1234"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testGetAgentInfoByName() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByName("custom-name-2");

            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("agent-process-id");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("agents-count"), is("2"));
            assertThat(this.getTask().getProject().getProperty("agent-name"), is("custom-name-2"));
            assertThat(this.getTask().getProject().getProperty("agent-hostname"), is("custom-host-2"));
            assertThat(this.getTask().getProject().getProperty("agent-process-id"), is("5678"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testGetAgentInfoByIndexNotFound() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByIndex(5);

            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("agent-process-id");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("agents-count"), is("2"));

            assertNull(this.getTask().getProject().getProperty("agent-name"));
            assertNull(this.getTask().getProject().getProperty("agent-hostname"));
            assertNull(this.getTask().getProject().getProperty("agent-process-id"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testGetAgentInfoByIndexAndName() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByIndex(0);
            this.getTask().setInfoForAgentByName("custom-name-2");

            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("agent-process-id");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("agents-count"), is("2"));
            assertThat(this.getTask().getProject().getProperty("agent-name"), is("custom-name-2"));
            assertThat(this.getTask().getProject().getProperty("agent-hostname"), is("custom-host-2"));
            assertThat(this.getTask().getProject().getProperty("agent-process-id"), is("5678"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testGetAgentInfoByIndexWithoutProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByIndex(0);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    @Test
    public void testGetAgentInfoByNameWithoutProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByName("custom-name");
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }


    @Test
    public void testGetAgentInfoProperties() throws Exception {
        this.applyFreshEnvironment();

        try {


            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("process-id");
            this.getTask().setInfoForAgentByIndex(5);
            this.getTask().setInfoForAgentByName("agent-name-info");

            assertThat(this.getTask().getAgentCountProperty(), is("agents-count"));
            assertThat(this.getTask().getAgentNameProperty(), is("agent-name"));
            assertThat(this.getTask().getAgentHostNameProperty(), is("agent-hostname"));
            assertThat(this.getTask().getAgentProcessIdProperty(), is("process-id"));
            assertThat(this.getTask().getInfoForAgentByIndex(), is(5));
            assertThat(this.getTask().getInfoForAgentByName(), is("agent-name-info"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }


}
