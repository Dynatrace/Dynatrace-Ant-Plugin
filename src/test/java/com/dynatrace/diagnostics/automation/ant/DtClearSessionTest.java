package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.sessions.Sessions;
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
@PrepareForTest({Sessions.class, DtClearSession.class})
public class DtClearSessionTest extends AbstractDynatraceTest<DtClearSession> {
    private static final String CLEAR_SESSION_TARGET_NAME = "clearSession";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtClearSession.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(sessions).clear("profile-success");
        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).clear("profile-fail");

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);
    }

    @Override
    protected String getTaskTargetName() {
        return CLEAR_SESSION_TARGET_NAME;
    }

    @Override
    protected DtClearSession createNewInstanceOfTheTask() {
        return new DtClearSession();
    }

    @Test
    public void testClearSessionWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile-success");
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testClearSessionWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile-fail");
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}