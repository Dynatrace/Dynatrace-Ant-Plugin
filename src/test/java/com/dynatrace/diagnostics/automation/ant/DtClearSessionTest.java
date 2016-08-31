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

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Sessions.class, DtClearSession.class})
public class DtClearSessionTest extends AbstractDynatraceTest<DtClearSession> {
    private static final String CLEAR_SESSION_TARGET_NAME = "clearSession";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtClearSession.class).withAnyArguments().thenReturn(this.getTask());

        Sessions sessions = spy(new Sessions(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(sessions).clear("profile-success");
        doThrow(new ServerConnectionException("message", new Exception())).when(sessions).clear("profile-fail");

        whenNew(Sessions.class).withAnyArguments().thenReturn(sessions);
    }

    @Override
    protected String getTaskTargetName() {
        return CLEAR_SESSION_TARGET_NAME;
    }

    @Override
    protected DtClearSession createNewInstanceOfTheTask() {
        return new DtClearSession();
    }

    @Test
    public void testClearSessionWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile-success");
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testClearSessionWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile-fail");
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}