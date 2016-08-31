/*
 * Dynatrace Ant Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

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
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

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
        assertNull(this.getTask().getDumpStatusProperty());
        assertThat(this.getTask().isSessionLocked(), is(false));
        assertThat(this.getTask().getDoGc(), is(false));
        assertThat(this.getTask().getAutoPostProcess(), is(false));
        assertThat(this.getTask().getCapturePrimitives(), is(false));
        assertThat(this.getTask().getCaptureStrings(), is(false));
        assertThat(this.getTask().getWaitForDumpTimeout(), is(60000));
        assertThat(this.getTask().getWaitForDumpPollingInterval(), is(5000));
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
            this.getTask().setDumpType("memdump_simple");

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
    public void testMemoryDumpWithoutProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }


    @Test
    public void testMemoryDumpProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setDumpType("memdump_simple");
            this.getTask().setMemoryDumpNameProperty("prop");
            this.getTask().setDumpStatusProperty("other-prop");
            this.getTask().setCaptureStrings(true);
            this.getTask().setCapturePrimitives(true);
            this.getTask().setAutoPostProcess(true);
            this.getTask().setDoGc(true);
            this.getTask().setSessionLocked(true);
            this.getTask().setWaitForDumpTimeout(1111);
            this.getTask().setWaitForDumpPollingInterval(412);


            assertThat(this.getTask().getDumpType(), is("memdump_simple"));
            assertThat(this.getTask().getMemoryDumpNameProperty(), is("prop"));
            assertThat(this.getTask().getDumpStatusProperty(), is("other-prop"));
            assertThat(this.getTask().isSessionLocked(), is(true));
            assertThat(this.getTask().getDoGc(), is(true));
            assertThat(this.getTask().getAutoPostProcess(), is(true));
            assertThat(this.getTask().getCapturePrimitives(), is(true));
            assertThat(this.getTask().getCaptureStrings(), is(true));
            assertThat(this.getTask().getWaitForDumpTimeout(), is(1111));
            assertThat(this.getTask().getWaitForDumpPollingInterval(), is(412));


        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}