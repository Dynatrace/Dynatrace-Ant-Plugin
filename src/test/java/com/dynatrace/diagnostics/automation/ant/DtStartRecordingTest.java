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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.dynatrace.diagnostics.automation.ant.matchers.StartRecordingRequestProfileNameMatcher;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.sessions.Sessions;
import com.dynatrace.sdk.server.sessions.models.RecordingOption;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Sessions.class, DtStartRecording.class})
public class DtStartRecordingTest extends AbstractDynatraceTest<DtStartRecording> {
    private static final String START_RECORDING_TARGET_NAME = "startRecording";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtStartRecording.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn("example-session-name").when(sessions).startRecording(Mockito.argThat(new StartRecordingRequestProfileNameMatcher("start-recording-success")));
        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).startRecording(Mockito.argThat(new StartRecordingRequestProfileNameMatcher("start-recording-with-exception")));

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);
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
    public void testStartRecordingWithSessionUriPropertySet() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().setProfileName("start-recording-success");
            this.getTask().setSessionUriProperty("someProperty");

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
            this.getTask().setSessionUriProperty("c");
            this.getTask().setSessionLocked(true);
            this.getTask().setAppendTimestamp(true);

            assertThat(this.getTask().getSessionName(), is("a"));
            assertThat(this.getTask().getSessionDescription(), is("b"));
            assertThat(this.getTask().getRecordingOption(), is(RecordingOption.ALL.getInternal()));
            assertThat(this.getTask().getSessionUriProperty(), is("c"));
            assertThat(this.getTask().isSessionLocked(), is(true));
            assertThat(this.getTask().isAppendTimestamp(), is(true));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}