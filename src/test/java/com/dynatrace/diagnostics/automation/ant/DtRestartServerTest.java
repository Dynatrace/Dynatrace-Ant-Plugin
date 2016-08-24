package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.servermanagement.ServerManagement;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerManagement.class, DtRestartServer.class})
public class DtRestartServerTest extends AbstractDynatraceTest<DtRestartServer> {
    private static final String RESTART_SERVER_TARGET_NAME = "restartServer";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtRestartServer.class).withAnyArguments().thenReturn(this.getTask());

        ServerManagement serverManagement = spy(new ServerManagement(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(serverManagement).restart();
        doThrow(new ServerConnectionException("message", new Exception())).when(serverManagement).shutdown();

        whenNew(ServerManagement.class).withAnyArguments().thenReturn(serverManagement);
    }

    @Override
    protected String getTaskTargetName() {
        return RESTART_SERVER_TARGET_NAME;
    }

    @Override
    protected DtRestartServer createNewInstanceOfTheTask() {
        return new DtRestartServer();
    }

    @Test
    public void testRestartServer() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setRestart(true);

            this.executeTaskWithProjectContext();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testShutdownServer() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setRestart(false);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}