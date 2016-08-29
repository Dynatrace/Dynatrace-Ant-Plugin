package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.sessions.Sessions;
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

;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Sessions.class, DtReanalyzeSession.class})
public class DtReanalyzeSessionTest extends AbstractDynatraceTest<DtReanalyzeSession> {
    private static final String REANALYZE_SESSION_TARGET_NAME = "reanalyzeSession";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtReanalyzeSession.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(sessions).reanalyze("example-session-name");
        doReturn(true).when(sessions).getReanalysisStatus("example-session-name");

        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).reanalyze("reanalyze-session-with-exception");

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);

        /** verify default values */
        assertThat(this.getTask().getReanalyzeSessionTimeout(), is(60000));
        assertThat(this.getTask().getReanalyzeSessionPollingInterval(), is(5000));
    }

    @Override
    protected String getTaskTargetName() {
        return REANALYZE_SESSION_TARGET_NAME;
    }

    @Override
    protected DtReanalyzeSession createNewInstanceOfTheTask() {
        return new DtReanalyzeSession();
    }

    @Test
    public void testStopRecordingWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setSessionName("example-session-name");
            this.getTask().setReanalyzeStatusProperty("reanalyze-status-property");
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("reanalyze-status-property"), is("true"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStopRecordingWithException() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setSessionName("reanalyze-session-with-exception");
            this.getTask().setReanalyzeStatusProperty("reanalyze-status-property");
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }


    @Test
    public void testStopRecordingProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setReanalyzeSessionTimeout(30000);
            this.getTask().setReanalyzeSessionPollingInterval(2500);
            this.getTask().setReanalyzeStatusProperty("def");

            assertThat(this.getTask().getReanalyzeSessionTimeout(), is(30000));
            assertThat(this.getTask().getReanalyzeSessionPollingInterval(), is(2500));
            assertThat(this.getTask().getReanalyzeStatusProperty(), is("def"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}