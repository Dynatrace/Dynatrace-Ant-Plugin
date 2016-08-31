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

import com.dynatrace.sdk.server.testautomation.TestAutomation;
import com.dynatrace.sdk.server.testautomation.models.CreateTestRunRequest;
import com.dynatrace.sdk.server.testautomation.models.TestCategory;
import com.dynatrace.sdk.server.testautomation.models.TestRun;
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
@PrepareForTest({TestAutomation.class, DtStartTest.class})
public class DtStartTestTest extends AbstractDynatraceTest<DtStartTest> {
    private static final String START_TEST_TARGET_NAME = "startTest";
    private static final String EXAMPLE_TEST_RUN_ID = "7f98a064-d00d-4224-8803-2f87f4988584";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtStartTest.class).withAnyArguments().thenReturn(this.getTask());

        TestRun testRun = new TestRun(0L, null, null, TestCategory.UNIT, EXAMPLE_TEST_RUN_ID, null, null, null, null, null, null, null, null, null, null, null, null, null);
        TestAutomation testAutomation = spy(new TestAutomation(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(testRun).when(testAutomation).createTestRun(Mockito.any(CreateTestRunRequest.class));

        whenNew(TestAutomation.class).withAnyArguments().thenReturn(testAutomation);
    }

    @Override
    protected String getTaskTargetName() {
        return START_TEST_TARGET_NAME;
    }

    @Override
    protected DtStartTest createNewInstanceOfTheTask() {
        return new DtStartTest();
    }

    @Test
    public void testStartTest() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setVersionBuild("1");
            this.getTask().setCategory("unit");

            this.getTask().execute();

            assertThat(this.getTask().getProject().getProperty("dtTestrunID"), is(EXAMPLE_TEST_RUN_ID));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testStartTestCategoryNotSet() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setVersionBuild("1");
            this.getTask().execute();

            fail("Exception should be thrown - category is not set");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testStartTestWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setCategory("unit");
            this.getTask().execute();

            fail("Exception should be thrown - build version is not set");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testStartTestWithWrongBuildVersion() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setVersionBuild("");
            this.getTask().setCategory("unit");

            this.getTask().execute();

            fail("Exception should be thrown - build version is not set");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testStartTestProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setVersionMajor("1");
            this.getTask().setVersionMinor("2");
            this.getTask().setVersionRevision("3");
            this.getTask().setVersionMilestone("4");
            this.getTask().setVersionBuild("5");
            this.getTask().setMarker("marker");
            this.getTask().setCategory("unit");
            this.getTask().setPlatform("Linux");

            assertThat(this.getTask().getVersionMajor(), is("1"));
            assertThat(this.getTask().getVersionMinor(), is("2"));
            assertThat(this.getTask().getVersionRevision(), is("3"));
            assertThat(this.getTask().getVersionMilestone(), is("4"));
            assertThat(this.getTask().getVersionBuild(), is("5"));
            assertThat(this.getTask().getMarker(), is("marker"));
            assertThat(this.getTask().getCategory(), is("unit"));
            assertThat(this.getTask().getPlatform(), is("Linux"));

        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }
}