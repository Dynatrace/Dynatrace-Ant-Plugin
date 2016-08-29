package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.diagnostics.automation.ant.matchers.StoreSessionRequestProfileNameMatcher;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.sessions.Sessions;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Sessions.class, DtStorePurePaths.class})
public class DtStorePurePathsTest extends AbstractDynatraceTest<DtStorePurePaths> {
    private static final String STORE_PURE_PATHS_TARGET_NAME = "storePurePaths";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtStorePurePaths.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn("example-session-name").when(sessions).store(Mockito.argThat(new StoreSessionRequestProfileNameMatcher("store-pure-paths-success")));
        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).store(Mockito.argThat(new StoreSessionRequestProfileNameMatcher("store-pure-paths-with-exception")));

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);

        /** verify default values */
        assertNull(this.getTask().getRecordingOption());
        assertThat(this.getTask().isSessionLocked(), is(false));
        assertThat(this.getTask().isAppendTimestamp(), is(false));
    }

    @Override
    protected String getTaskTargetName() {
        return STORE_PURE_PATHS_TARGET_NAME;
    }

    @Override
    protected DtStorePurePaths createNewInstanceOfTheTask() {
        return new DtStorePurePaths();
    }

    @Test
    public void testStorePurePathWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("store-pure-paths-success");
            this.getTask().setRecordingOption("all");
            this.getTask().setSessionLocked(true);
            this.getTask().setAppendTimestamp(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStorePurePathWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("store-pure-paths-with-exception");
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}