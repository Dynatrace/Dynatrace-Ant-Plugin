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
import com.dynatrace.sdk.server.testautomation.TestAutomation;
import com.dynatrace.sdk.server.testautomation.models.TestRun;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TestAutomation.class, DtFinishTest.class})
public class DtFinishTestTest extends AbstractDynatraceTest<DtFinishTest> {
    private static final String TESTRUN_ID_PROPERTY_NAME = "dtTestrunID";
    private static final String EXAMPLE_TEST_RUN_ID = "7f98a064-d00d-4224-8803-2f87f4988584";

    TestAutomation testAutomation;

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtFinishTest.class).withAnyArguments().thenReturn(this.getTask());

        testAutomation = spy(new TestAutomation(this.getTask().getDynatraceClient()));
        doReturn(mock(TestRun.class)).when(testAutomation).finishTestRun(Mockito.any(),Mockito.any());
        whenNew(TestAutomation.class).withAnyArguments().thenReturn(testAutomation);
    }

    @Override
    protected String getTaskTargetName() {
        return TESTRUN_ID_PROPERTY_NAME;
    }

    @Override
    protected DtFinishTest createNewInstanceOfTheTask() {
        return new DtFinishTest();
    }

    @Test
    public void finishShouldBeExecuted() throws Exception {
        this.applyFreshEnvironment();
        this.getTask().setTestRunId(EXAMPLE_TEST_RUN_ID);

        this.getTask().execute();

        Mockito.verify(testAutomation).finishTestRun(Mockito.any(),Mockito.any());
    }

    @Test(expected = BuildException.class)
    public void finishWithEmpyTestRunIdShouldFail() throws Exception{
        this.applyFreshEnvironment();
        this.getTask().getProject().setProperty(TESTRUN_ID_PROPERTY_NAME,null);

        this.getTask().execute();
    }
}