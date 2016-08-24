package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.diagnostics.automation.ant.matchers.StartRecordingRequestProfileNameMatcher;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;
import com.dynatrace.sdk.server.sessions.models.StartRecordingRequest;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Sessions.class, DtStartRecording.class})
public class DtStartRecordingTest extends AbstractDynatraceTest<DtStartRecording> {
    private static final String START_RECORDING_TARGET_NAME = "startRecording";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtStartRecording.class).withAnyArguments().thenReturn(this.getTask());
        StartRecordingRequest startRecordingRequest = new StartRecordingRequest("system-profile-name");

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn("example-session-name").when(sessions).startRecording(Mockito.argThat(new StartRecordingRequestProfileNameMatcher("start-recording-success")));
        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).startRecording(Mockito.argThat(new StartRecordingRequestProfileNameMatcher("start-recording-with-exception")));

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);

        /** verify default values */
       /* assertThat(this.getTask().getRecordingOption(), is("all"));*/
    }

    @Override
    protected String getTaskTargetName() {
        return START_RECORDING_TARGET_NAME;
    }

    @Override
    protected DtStartRecording createNewInstanceOfTheTask() {
        return new DtStartRecording();
    }

    @Test
    public void testStartRecordingWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("start-recording-success");
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStartRecordingWithException() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("start-recording-with-exception");
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testStartRecordingWithSessionNamePropertySet() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("start-recording-success");
            this.getTask().setSessionNameProperty("someProperty");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("someProperty"), is("example-session-name"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStartRecordingProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setSessionName("a");
            this.getTask().setSessionDescription("b");
            this.getTask().setRecordingOption(RecordingOption.ALL.getInternal());
            this.getTask().setSessionNameProperty("c");
            this.getTask().setSessionLocked(true);
            this.getTask().setAppendTimestamp(true);

            assertThat(this.getTask().getSessionName(), is("a"));
            assertThat(this.getTask().getSessionDescription(), is("b"));
            assertThat(this.getTask().getRecordingOption(), is(RecordingOption.ALL.getInternal()));
            assertThat(this.getTask().getSessionNameProperty(), is("c"));
            assertThat(this.getTask().isSessionLocked(), is(true));
            assertThat(this.getTask().isAppendTimestamp(), is(true));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}