package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.resourcedumps.ResourceDumps;
import com.dynatrace.sdk.server.resourcedumps.models.CreateThreadDumpRequest;
import com.dynatrace.sdk.server.resourcedumps.models.ThreadDumpStatus;
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
@PrepareForTest({ThreadDumpStatus.class, ResourceDumps.class, DtThreadDump.class})
public class DtThreadDumpWithExceptionTest extends AbstractDynatraceTest<DtThreadDump> {
    private static final String THREAD_DUMP_MOJO_NAME = "threadDump";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtThreadDump.class).withAnyArguments().thenReturn(this.getTask());

        ResourceDumps resourceDumps = spy(new ResourceDumps(this.getTask().getDynatraceClient()));

        /** define responses */
        ThreadDumpStatus threadDumpStatus = spy(new ThreadDumpStatus());
        doReturn(true).when(threadDumpStatus).getResultValue();

        doThrow(new ServerConnectionException("message", new Exception())).when(resourceDumps).createThreadDump(Mockito.any(CreateThreadDumpRequest.class));

        whenNew(ResourceDumps.class).withAnyArguments().thenReturn(resourceDumps);

        /** verify default values */
        assertThat(this.getTask().getWaitForDumpTimeout(), is(60000));
        assertThat(this.getTask().getWaitForDumpPollingInterval(), is(5000));
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
    public void testThreadDumpWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("some-profile");
            this.getTask().setAgentName("agent-name");
            this.getTask().setHostName("host-name");
            this.getTask().setProcessId(1234);

            this.getTask().execute();

            fail("Exception should be thrown - build version is not set");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }


}