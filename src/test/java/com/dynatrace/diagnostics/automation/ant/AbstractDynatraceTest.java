package com.dynatrace.diagnostics.automation.ant;

import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public abstract class AbstractDynatraceTest<T extends DtServerBase> {
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

    protected void applyFreshEnvironment() throws Exception {
        this.task = (T) this.createConfiguredTask(this.createNewInstanceOfTheTask());
        this.task.setDynatraceClientWithCustomHttpClient(null);
    }

    protected void executeTaskWithProjectContext() {
        this.getTask().getOwningTarget().execute();
    }

    protected T getTask() { return this.task; }

    protected abstract String getTaskTargetName();
    protected abstract T createNewInstanceOfTheTask();
}
