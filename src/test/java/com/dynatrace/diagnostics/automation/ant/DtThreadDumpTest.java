package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.resourcedumps.ResourceDumps;
import com.dynatrace.sdk.server.resourcedumps.models.CreateThreadDumpRequest;
import com.dynatrace.sdk.server.resourcedumps.models.ThreadDumpStatus;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadDumpStatus.class, ResourceDumps.class, DtThreadDump.class})
public class DtThreadDumpTest extends AbstractDynatraceTest<DtThreadDump> {
    private static final String THREAD_DUMP_MOJO_NAME = "threadDump";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtThreadDump.class).withAnyArguments().thenReturn(this.getTask());

        ResourceDumps resourceDumps = spy(new ResourceDumps(this.getTask().getDynatraceClient()));


        /** define responses */
        ThreadDumpStatus threadDumpStatus = spy(new ThreadDumpStatus());
        doReturn(true).when(threadDumpStatus).getResultValue();

        doReturn("thread-dump-schedule-id").when(resourceDumps).createThreadDump(Mockito.any(CreateThreadDumpRequest.class));
        doReturn(threadDumpStatus).when(resourceDumps).getThreadDumpStatus("some-profile", "thread-dump-schedule-id");

        whenNew(ResourceDumps.class).withAnyArguments().thenReturn(resourceDumps);

        /** verify default values */
       /* assertThat(this.getTask().getWaitForDumpTimeout(), is(60000));
        assertThat(this.getTask().getWaitForDumpPollingInterval(), is(5000));*/
    }

    @Override
    protected String getTaskTargetName() {
        return THREAD_DUMP_MOJO_NAME;
    }

    @Override
    protected DtThreadDump createNewInstanceOfTheTask() {
        return new DtThreadDump();
    }


    @Test
    public void testThreadDump() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("some-profile");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().setDumpStatusProperty("dump-status");
            this.getTask().setThreadDumpNameProperty("dump-name");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("dump-name"), is("thread-dump-schedule-id"));
            assertThat(this.getTask().getProject().getProperty("dump-status"), is("true"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testThreadDumpWithoutProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    @Test
    public void testStartTestProperties() throws Exception {
        this.applyFreshEnvironment();

        try {


            this.getTask().setSessionLocked(true);
            this.getTask().setThreadDumpNameProperty("dump-name-property");
            this.getTask().setDumpStatusProperty("dump-status-property");
            this.getTask().setWaitForDumpTimeout(30000);
            this.getTask().setWaitForDumpPollingInterval(2500);

            assertThat(this.getTask().isSessionLocked(), is(true));
            assertThat(this.getTask().getThreadDumpNameProperty(), is("dump-name-property"));
            assertThat(this.getTask().getDumpStatusProperty(), is("dump-status-property"));
            assertThat(this.getTask().getWaitForDumpTimeout(), is(30000));
            assertThat(this.getTask().getWaitForDumpPollingInterval(), is(2500));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

}