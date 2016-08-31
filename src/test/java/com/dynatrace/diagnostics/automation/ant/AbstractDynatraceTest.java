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

import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

abstract class AbstractDynatraceTest<T extends DtServerBase> {
    public static final String DEFAULT_TEST_PLUGIN_CONFIG_PATH = "src/test/resources/build.xml";

    private T task;

    protected Task createConfiguredTask(T taskInstance) throws Exception {
        BuildFileRule buildFileRule = new BuildFileRule();
        buildFileRule.configureProject(DEFAULT_TEST_PLUGIN_CONFIG_PATH);

        Target target = new Target();

        taskInstance.setProject(buildFileRule.getProject());
        taskInstance.setOwningTarget(target);

        target.setName(this.getTaskTargetName());
        target.addTask(taskInstance);

        buildFileRule.getProject().addTarget(target);

        return taskInstance;
    }

    @SuppressWarnings("unchecked")
    protected void applyFreshEnvironment() throws Exception {
        this.task = (T) this.createConfiguredTask(this.createNewInstanceOfTheTask());
        this.task.setDynatraceClientWithCustomHttpClient(null);
    }

    protected void executeTaskWithProjectContext() {
        this.getTask().getOwningTarget().execute();
    }

    protected T getTask() {
        return this.task;
    }

    protected abstract String getTaskTargetName();

    protected abstract T createNewInstanceOfTheTask();
}
