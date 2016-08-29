package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
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
@PrepareForTest({AgentsAndCollectors.class, DtRestartCollector.class})
public class DtRestartCollectorTest extends AbstractDynatraceTest<DtRestartCollector> {
    private static final String RESTART_COLLECTOR_TARGET_NAME = "restartCollector";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtRestartCollector.class).withAnyArguments().thenReturn(this.getTask());

        AgentsAndCollectors agentsAndCollectors = spy(new AgentsAndCollectors(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(agentsAndCollectors).restartCollector("collector-restart-true");
        doReturn(true).when(agentsAndCollectors).shutdownCollector("collector-shutdown-true");
        doThrow(new ServerConnectionException("message", new Exception())).when(agentsAndCollectors).restartCollector("collector-restart-exception");
        doThrow(new ServerConnectionException("message", new Exception())).when(agentsAndCollectors).shutdownCollector("collector-shutdown-exception");

        whenNew(AgentsAndCollectors.class).withAnyArguments().thenReturn(agentsAndCollectors);

        /** verify default values */
        assertThat(this.getTask().isRestart(), is(true));
    }

    @Override
    protected String getTaskTargetName() {
        return RESTART_COLLECTOR_TARGET_NAME;
    }

    @Override
    protected DtRestartCollector createNewInstanceOfTheTask() {
        return new DtRestartCollector();
    }

    @Test
    public void testRestartCollectorWithoutCollectorName() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setRestart(true);
            this.getTask().execute();

            fail("Exception should be thrown - collector name is null");
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    @Test
    public void testRestartCollectorWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setCollector("collector-restart-true");
            this.getTask().setRestart(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testShutdownCollectorWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setCollector("collector-shutdown-true");
            this.getTask().setRestart(false);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testRestartCollectorWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setCollector("collector-restart-exception");
            this.getTask().setRestart(true);
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testShutdownCollectorWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setCollector("collector-shutdown-exception");
            this.getTask().setRestart(false);
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}