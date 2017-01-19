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
        doReturn("example-session-location").when(sessions).stopRecording("stop-recording");

        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).stopRecording("stop-recording-with-exception");

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);

        /** verify default values */
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

            this.getTask().getProject().setProperty("dtSessionLocationProperty", "other-session-location-property");
            this.getTask().setSessionLocationProperty("session-location-property");
            this.getTask().setProfileName("stop-recording");
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("session-location-property"), is("example-session-location"));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }


    @Test
    public void testStopRecordingWithSessionLocationPropertyFromProject() throws Exception {
        this.applyFreshEnvironment();

        try {

            this.getTask().getProject().setProperty("dtSessionLocationProperty", "session-name-property");
            this.getTask().setProfileName("stop-recording");
            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("session-name-property"), is("example-session-location"));
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
            this.getTask().setStopDelay(2500);
            this.getTask().setSessionLocationProperty("abc");

            assertThat(this.getTask().getStopDelay(), is(2500));
            assertThat(this.getTask().getSessionLocationProperty(), is("abc"));

        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}