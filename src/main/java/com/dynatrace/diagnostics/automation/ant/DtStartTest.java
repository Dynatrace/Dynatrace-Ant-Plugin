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

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.server.testautomation.TestAutomation;
import com.dynatrace.sdk.server.testautomation.models.CreateTestRunRequest;
import com.dynatrace.sdk.server.testautomation.models.TestCategory;
import com.dynatrace.sdk.server.testautomation.models.TestMetaData;
import com.dynatrace.sdk.server.testautomation.models.TestMetricFilter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ant task to start a test run
 */
public class DtStartTest extends DtServerProfileBase {

    /* Attributes and properties names  */
    public static final String ATTRIBUTE_TEST_BUILD = "versionBuild";
    public static final String ATTRIBUTE_TEST_CATEGORY = "testCategory";
    public static final String DT_AGENT_TESTRUN_OPTION = "optionTestRunIdJava";

    /* Messages */
    private static final String INVALID_BUILD_NUMBER_MESSAGE = "Build number cannot be '-' or empty";
    private static final String MISSING_BUILD_MESSAGE = "Task requires attribute \"" + ATTRIBUTE_TEST_BUILD + "\"";
    private static final String INVALID_CATEGORY_MESSAGE = "\"" + ATTRIBUTE_TEST_CATEGORY + "\" has invalid value \"{0}\"." +
            "Select one from " + Arrays.toString(DtUtil.getTestCategoryInternalValues());
    private static final String TESTRUN_ID_PROPERTY_MESSAGE = "Setting property <" + TESTRUN_ID_PROPERTY_NAME + "> to value <{0}>. " +
            "Remember to pass it to DT agent in <" + DT_AGENT_TESTRUN_OPTION + "> parameter";

    private static final String INDENTATION_WITH_NEW_LINE = "\n\t";
    private static final String DEEP_INDENTATION_WITH_NEW_LINE = "\n\t\t";

    private final List<CustomProperty> properties = new ArrayList<>();
    private String versionMajor;
    private String versionMinor;
    private String versionRevision;
    private String versionMilestone;
    private String versionBuild;
    private String marker;
    private String category;
    private String platform;

    /**
     * Flag to make this task fail on error. Default: true
     */
    private boolean failOnError = true;
    private List<TestMetricFilter> metricFilters = new ArrayList<>();

    /**
     * Used to add a custom property to the test meta data
     *
     * @param property
     */
    public void addCustomProperty(final CustomProperty property) {
        properties.add(property);
    }

    /**
     * Executes ant task
     *
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    @Override
    public void execute() throws BuildException {
        try {
            this.checkParameters();
            this.checkVersionInformation();

            this.log(this.generateInfoMessage());
            TestAutomation testAutomation = new TestAutomation(this.getDynatraceClient());
            String testRunUUID = testAutomation.createTestRun(this.buildTestRunRequest()).getId();

            this.getProject().setProperty(TESTRUN_ID_PROPERTY_NAME, testRunUUID);

            this.log(MessageFormat.format(TESTRUN_ID_PROPERTY_MESSAGE, testRunUUID));
        } catch (Exception e) {
            if (this.failOnError) {
                throw new BuildException(String.format("Exception when executing: %s", e.getMessage()), e);
            }

            this.log(String.format("Exception when executing: %s", e.getMessage()), e, Project.MSG_ERR);
        }
    }

    /**
     * Validates provided parameters
     *
     * @throws BuildException whenever provided properties are invalid
     */
    private void checkParameters() throws BuildException {
        if (this.versionBuild == null) {
            throw new BuildException(MISSING_BUILD_MESSAGE);
        }

        try {
            TestCategory.fromInternal(this.category);
        } catch (IllegalArgumentException e) {
            throw new BuildException(MessageFormat.format(INVALID_CATEGORY_MESSAGE, this.category));
        }
    }

    /**
     * Validates provided build version
     *
     * @throws BuildException whenever build version is invalid
     */
    private void checkVersionInformation() {
        if (this.versionBuild != null && !isBuildNumberValid(this.versionBuild)) {
            throw new BuildException(INVALID_BUILD_NUMBER_MESSAGE);
        }
    }

    /**
     * Validates build number
     *
     * @param buildNumber - build version to check
     * @return boolean {@code true} when build number has proper format, {@code false} otherwise
     */
    private boolean isBuildNumberValid(String buildNumber) {
        if (buildNumber == null || "".equals(buildNumber.trim())) {
            return false;
        }

        return !"-".equals(buildNumber);
    }

    /**
     * Creates test run request required for {@code TestAutomation#createTestRun} method
     *
     * @return {@CreateTestRunRequest} that contains configured request
     */
    private CreateTestRunRequest buildTestRunRequest() {
        CreateTestRunRequest request = new CreateTestRunRequest();

        request.setSystemProfile(this.getProfileName());
        request.setVersionMajor(this.versionMajor);
        request.setVersionMinor(this.versionMinor);
        request.setVersionRevision(this.versionRevision);
        request.setVersionBuild(this.versionBuild);
        request.setVersionMilestone(this.versionMilestone);
        request.setMarker(this.marker);
        request.setCategory(TestCategory.fromInternal(this.category));
        request.setPlatform(this.platform);

        TestMetaData testMetaData = new TestMetaData();

        for (CustomProperty property : this.properties) {
            testMetaData.setValue(property.getKey(), property.getValue());
        }
        request.setAdditionalMetaData(testMetaData);

        if (!metricFilters.isEmpty()) {
            request.setIncludedMetrics(metricFilters);
        }

        return request;
    }

    /**
     * Returns a debug message with all the details regarding provided test run information
     *
     * @return {@String} that contains formatted log message with test run request properties values
     */
    private String generateInfoMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Setting Test Information for system profile: ").append(this.getProfileName());
        stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("version: ").append(this.versionMajor).append(".").append(this.versionMinor).append(".").
                append(this.versionRevision).append(".").append(this.versionBuild).append(" milestone: ").append(this.versionMilestone);

        if (!DtUtil.isEmpty(this.marker)) {
            stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("marker: ").append(this.marker);
        }

        if (!DtUtil.isEmpty(this.category)) {
            stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("category: ").append(this.category);
        }

        if (!DtUtil.isEmpty(this.platform)) {
            stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("platform: ").append(this.platform);
        }

        if (!this.properties.isEmpty()) {
            stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("custom properties: ");

            for (CustomProperty property : this.properties) {
                stringBuilder.append(DEEP_INDENTATION_WITH_NEW_LINE).append(property.getKey()).append("=").append(property.getValue());
            }
        }

        if (!this.metricFilters.isEmpty()) {

            stringBuilder.append(INDENTATION_WITH_NEW_LINE).append("metric filter: ");
            for (TestMetricFilter metricFilter : metricFilters) {
                stringBuilder.append(DEEP_INDENTATION_WITH_NEW_LINE)
                        .append("group").append("=").append(metricFilter.getGroup())
                        .append(", metric").append("=").append(metricFilter.getMetric());
            }
        }

        return stringBuilder.toString();
    }


    public final String getVersionMajor() {
        return versionMajor;
    }

    public final void setVersionMajor(String versionMajor) {
        this.versionMajor = versionMajor;
    }

    public final String getVersionMinor() {
        return versionMinor;
    }

    public final void setVersionMinor(String versionMinor) {
        this.versionMinor = versionMinor;
    }

    public final String getVersionRevision() {
        return versionRevision;
    }

    public final void setVersionRevision(String versionRevision) {
        this.versionRevision = versionRevision;
    }

    public final String getVersionMilestone() {
        return versionMilestone;
    }

    public final void setVersionMilestone(String versionMilestone) {
        this.versionMilestone = versionMilestone;
    }

    public final String getVersionBuild() {
        return versionBuild;
    }

    public final void setVersionBuild(String versionBuild) {
        this.versionBuild = versionBuild;
    }

    public final String getMarker() {
        return marker;
    }

    public final void setMarker(String marker) {
        this.marker = marker;
    }

    public final String getCategory() {
        return category;
    }

    public final void setCategory(String category) {
        this.category = category;
    }

    public final String getPlatform() {
        return platform;
    }

    public final void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public void addMetricFilter(TestMetricFilter metricFilter) {
        this.metricFilters.add(metricFilter);
    }
}
