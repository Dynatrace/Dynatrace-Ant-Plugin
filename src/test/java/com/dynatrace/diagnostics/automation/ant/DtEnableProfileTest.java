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
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemProfiles.class, DtEnableProfile.class})
public class DtEnableProfileTest extends AbstractDynatraceTest<DtEnableProfile> {
    private static final String ENABLE_PROFILE_TARGET_NAME = "enableProfile";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtEnableProfile.class).withAnyArguments().thenReturn(this.getTask());

        SystemProfiles systemProfiles = spy(new SystemProfiles(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(systemProfiles).enableProfile("enable-success-true");
        doReturn(false).when(systemProfiles).enableProfile("enable-success-false");
        doReturn(true).when(systemProfiles).disableProfile("disable-success-true");
        doReturn(false).when(systemProfiles).disableProfile("disable-success-false");

        doThrow(new ServerConnectionException("message", new Exception())).when(systemProfiles).enableProfile("enable-exception");
        doThrow(new ServerResponseException(500, "message", new Exception())).when(systemProfiles).disableProfile("disable-exception");

        whenNew(SystemProfiles.class).withAnyArguments().thenReturn(systemProfiles);

        /** verify default values */
        assertThat(this.getTask().isEnable(), is(true));
    }

    @Override
    protected String getTaskTargetName() {
        return ENABLE_PROFILE_TARGET_NAME;
    }

    @Override
    protected DtEnableProfile createNewInstanceOfTheTask() {
        return new DtEnableProfile();
    }

    @Test
    public void testEnableTrueSuccessTrue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-success-true");
            this.getTask().setEnable(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableTrueSuccessFalse() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-success-false");
            this.getTask().setEnable(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableFalseSuccessTrue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-success-true");
            this.getTask().setEnable(false);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableFalseSuccessFalse() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-success-false");
            this.getTask().setEnable(false);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableTrueWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-exception");
            this.getTask().setEnable(true);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testEnableFalseWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-exception");
            this.getTask().setEnable(false);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));

            /** maybe exception message should be more verbose? */
            //assertThat(e.getMessage(), containsString("500"));
        }
    }

}