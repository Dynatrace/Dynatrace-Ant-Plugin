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
@PrepareForTest({Sessions.class, DtStopRecording.class})
public class DtStopRecordingTest extends AbstractDynatraceTest<DtStopRecording> {
    private static final String STOP_RECORDING_TARGET_NAME = "stopRecording";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtStopRecording.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn("example-session-name").when(sessions).stopRecording("stop-recording");
        doReturn(true).when(sessions).reanalyze("example-session-name");
        doReturn(true).when(sessions).getReanalysisStatus("example-session-name");

        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).stopRecording("stop-recording-with-exception");

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);

        /** verify default values */
        assertThat(this.getTask().isDoReanalyzeSession(), is(false));
        assertThat(this.getTask().getReanalyzeSessionTimeout(), is(60000));
        assertThat(this.getTask().getReanalyzeSessionPollingInterval(), is(5000));
        assertThat(this.getTask().getStopDelay(), is(0));
        assertThat(this.getTask().isFailOnError(), is(true));
    }

    @Override
    protected String getTaskTargetName() {
        return STOP_RECORDING_TARGET_NAME;
    }

    @Override
    protected DtStopRecording createNewInstanceOfTheTask() {
        return new DtStopRecording();
    }

    @Test
    public void testStopRecordingWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().getProject().setProperty("dtSessionNameProperty", "other-session-name-property");
            this.getTask().setSessionNameProperty("session-name-property");
            this.getTask().setProfileName("stop-recording");
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("session-name-property"), is("example-session-name"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }


    @Test
    public void testStopRecordingWithSessionNamePropertyFromProject() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().getProject().setProperty("dtSessionNameProperty", "session-name-property");
            this.getTask().setProfileName("stop-recording");
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("session-name-property"), is("example-session-name"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStopRecordingWithReanalyzeSession() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("stop-recording");
            this.getTask().setSessionNameProperty("session-name-property");
            this.getTask().setReanalyzeStatusProperty("reanalyze-status-property");
            this.getTask().setDoReanalyzeSession(true);
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("session-name-property"), is("example-session-name"));
            assertThat(this.getTask().getProject().getProperty("reanalyze-status-property"), is("true"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStopRecordingWithException() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("stop-recording-with-exception");
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
            this.getTask().setDoReanalyzeSession(true);
            this.getTask().setReanalyzeSessionTimeout(30000);
            this.getTask().setReanalyzeSessionPollingInterval(2500);
            this.getTask().setStopDelay(2500);
            this.getTask().setSessionNameProperty("abc");
            this.getTask().setReanalyzeStatusProperty("def");

            assertThat(this.getTask().isDoReanalyzeSession(), is(true));
            assertThat(this.getTask().getReanalyzeSessionTimeout(), is(30000));
            assertThat(this.getTask().getReanalyzeSessionPollingInterval(), is(2500));
            assertThat(this.getTask().getStopDelay(), is(2500));
            assertThat(this.getTask().getSessionNameProperty(), is("abc"));
            assertThat(this.getTask().getReanalyzeStatusProperty(), is("def"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}