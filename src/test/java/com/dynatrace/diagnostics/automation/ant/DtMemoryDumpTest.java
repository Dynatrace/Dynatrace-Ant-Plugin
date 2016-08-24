package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.memorydumps.MemoryDumps;
import com.dynatrace.sdk.server.memorydumps.models.JobState;
import com.dynatrace.sdk.server.memorydumps.models.MemoryDumpJob;
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
@PrepareForTest({MemoryDumps.class, DtMemoryDump.class})
public class DtMemoryDumpTest extends AbstractDynatraceTest<DtMemoryDump> {
    private static final String MEMORY_DUMP_MOJO_NAME = "memoryDump";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtMemoryDump.class).withAnyArguments().thenReturn(this.getTask());

        MemoryDumps memoryDumps = spy(new MemoryDumps(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn("https://localhost:8021/rest/management/profiles/system-profile-success/memorydumpjobs/Memory%20Dump%20%5B11880540745601%5D")
                .when(memoryDumps).createMemoryDumpJob(Mockito.anyString(), Mockito.any(MemoryDumpJob.class));

        MemoryDumpJob memoryDumpJob = new MemoryDumpJob();
        memoryDumpJob.setState(JobState.FINISHED);

        MemoryDumpJob memoryDumpJob2 = new MemoryDumpJob();
        memoryDumpJob2.setState(JobState.RUNNING);

        doReturn(memoryDumpJob).when(memoryDumps).getMemoryDumpJob("system-profile-success", "Memory Dump [11880540745601]");
        doReturn(memoryDumpJob2).when(memoryDumps).getMemoryDumpJob("system-profile-timeout", "Memory Dump [11880540745601]");
        doThrow(new ServerConnectionException("message", new Exception())).when(memoryDumps).getMemoryDumpJob("system-profile-exception", "Memory Dump [11880540745601]");
        doThrow(new ServerResponseException(500, "message", new Exception())).when(memoryDumps).getMemoryDumpJob("system-profile-exception-continue", "Memory Dump [11880540745601]");

        whenNew(MemoryDumps.class).withAnyArguments().thenReturn(memoryDumps);

        /** verify default values */
        /*
        assertThat(this.getTask().getDumpType(), is("memdump_simple"));
        assertThat(this.getTask().isSessionLocked(), is(true));
        assertThat(this.getTask().getDoGc(), is(false));
        assertThat(this.getTask().getAutoPostProcess(), is(false));
        assertThat(this.getTask().getCapturePrimitives(), is(false));
        assertThat(this.getTask().getCaptureStrings(), is(false));
        assertThat(this.getTask().getWaitForDumpTimeout(), is(60000));
        assertThat(this.getTask().getWaitForDumpPollingInterval(), is(5000));*/
    }

    @Override
    protected String getTaskTargetName() {
        return MEMORY_DUMP_MOJO_NAME;
    }

    @Override
    protected DtMemoryDump createNewInstanceOfTheTask() {
        return new DtMemoryDump();
    }

    @Test
    public void testMemoryDumpWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("system-profile-success");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().setMemoryDumpNameProperty("dump-name");
            this.getTask().setDumpStatusProperty("dump-status");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("dump-name"), is("Memory Dump [11880540745601]"));
            assertThat(this.getTask().getProject().getProperty("dump-status"), is("true"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testMemoryDumpWithTimeout() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("system-profile-timeout");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().setMemoryDumpNameProperty("dump-name");
            this.getTask().setDumpStatusProperty("dump-status");

            this.getTask().setWaitForDumpTimeout(100);
            this.getTask().setWaitForDumpPollingInterval(10);

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("dump-name"), is("Memory Dump [11880540745601]"));
            assertThat(this.getTask().getProject().getProperty("dump-status"), is("false"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testMemoryDumpWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("system-profile-exception");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().setMemoryDumpNameProperty("dump-name");
            this.getTask().setDumpStatusProperty("dump-status");

            this.getTask().setWaitForDumpTimeout(100);
            this.getTask().setWaitForDumpPollingInterval(10);

            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testMemoryDumpWithExceptionContinue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("system-profile-exception-continue");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().setMemoryDumpNameProperty("dump-name");
            this.getTask().setDumpStatusProperty("dump-status");

            this.getTask().setWaitForDumpTimeout(100);
            this.getTask().setWaitForDumpPollingInterval(10);

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("dump-name"), is("Memory Dump [11880540745601]"));
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
}