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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;


/**
 * Ant task to start a test run
 */
public class DtFinishTest extends DtServerProfileBase {

    public static final String TESTRUN_ID_PROPERTY_NAME = "dtTestrunID";
    private String systemProfile = null;
    private String testRunId = null;

    @Override
    public void execute() throws BuildException {
        try {
            TestAutomation testAutomation = new TestAutomation(this.getDynatraceClient());
            if(systemProfile==null) {
                systemProfile = this.getProfileName();
            }
            if(testRunId==null) {
                testRunId = this.getProject().getProperty(TESTRUN_ID_PROPERTY_NAME);
            }
            testAutomation.finishTestRun(systemProfile,testRunId);
            this.log(String.format("Finis testRun on profile %1$s with ID=%2$s", systemProfile,testRunId));
        } catch (Exception e) {
            this.log(String.format("Exception when finishing testRun: %s", e.getMessage()), e, Project.MSG_ERR);
        }
    }

    public final String getSystemProfile() { return systemProfile;}

    public final void setSystemProfile(String systemProfile) {
        this.systemProfile = systemProfile;
    }

    public final String getTestRunId() {
        return testRunId;
    }

    public final void setTestRunId(String testRunId) {
        this.testRunId = testRunId;
    }

}
