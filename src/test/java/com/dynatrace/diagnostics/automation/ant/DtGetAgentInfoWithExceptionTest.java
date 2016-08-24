package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AgentsAndCollectors.class, DtGetAgentInfo.class})
public class DtGetAgentInfoWithExceptionTest extends AbstractDynatraceTest<DtGetAgentInfo> {
    private static final String GET_AGENT_INFO_TARGET_NAME = "getAgentInfo";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtGetAgentInfo.class).withAnyArguments().thenReturn(this.getTask());

        AgentsAndCollectors agentsAndCollectors = spy(new AgentsAndCollectors(this.getTask().getDynatraceClient()));

        /** define responses */
        doThrow(new ServerConnectionException("message", new Exception())).when(agentsAndCollectors).fetchAgents();
        whenNew(AgentsAndCollectors.class).withAnyArguments().thenReturn(agentsAndCollectors);

        /** verify default values */
       /* assertThat(this.getTask().getInfoForAgentByIndex(), is(-1));*/
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
    public void testGetAgentInfoByIndexWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setInfoForAgentByIndex(0);

            this.getTask().setAgentCountProperty("agents-count");
            this.getTask().setAgentNameProperty("agent-name");
            this.getTask().setAgentHostNameProperty("agent-hostname");
            this.getTask().setAgentProcessIdProperty("agent-process-id");

            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}
