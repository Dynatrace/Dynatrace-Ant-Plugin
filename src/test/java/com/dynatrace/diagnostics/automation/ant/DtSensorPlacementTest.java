package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AgentsAndCollectors.class, DtSensorPlacement.class})
public class DtSensorPlacementTest extends AbstractDynatraceTest<DtSensorPlacement> {
    private static final String SENSOR_PLACEMENT_TARGET_NAME = "restartCollector";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtSensorPlacement.class).withAnyArguments().thenReturn(this.getTask());

        AgentsAndCollectors agentsAndCollectors = spy(new AgentsAndCollectors(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(agentsAndCollectors).placeHotSensor(1);
        doThrow(new ServerResponseException(500, "message", new Exception())).when(agentsAndCollectors).placeHotSensor(2);

        whenNew(AgentsAndCollectors.class).withAnyArguments().thenReturn(agentsAndCollectors);
    }

    @Override
    protected String getTaskTargetName() {
        return SENSOR_PLACEMENT_TARGET_NAME;
    }

    @Override
    protected DtSensorPlacement createNewInstanceOfTheTask() {
        return new DtSensorPlacement();
    }

    @Test
    public void testSensorPlacementWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(1);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testSensorPlacementWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(2);
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }


    @Test
    public void testSensorPlacementProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(-1);

            assertThat(this.getTask().getAgentId(), is(-1));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }


}