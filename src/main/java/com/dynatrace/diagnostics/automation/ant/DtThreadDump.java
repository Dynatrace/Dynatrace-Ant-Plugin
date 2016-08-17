package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.resourcedumps.ResourceDumps;
import com.dynatrace.sdk.server.resourcedumps.models.CreateThreadDumpRequest;
import org.apache.tools.ant.BuildException;

public class DtThreadDump extends DtAgentBase {

    private boolean sessionLocked;
    private String threadDumpNameProperty;

    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;
    private String dumpStatusProperty;

    @Override
    public void execute() throws BuildException {
        System.out.println("Creating Thread Dump for " + getProfileName() + "-" + getAgentName() + "-" + getHostName() + "-" + getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        ResourceDumps resourceDumps = new ResourceDumps(this.getDynatraceClient());
        CreateThreadDumpRequest createThreadDumpRequest = new CreateThreadDumpRequest(this.getProfileName(), this.getAgentName(), this.getHostName(), this.getProcessId());
        createThreadDumpRequest.setSessionLocked(this.isSessionLocked());

        try {
            String threadDump = resourceDumps.createThreadDump(createThreadDumpRequest);

            if (threadDumpNameProperty != null && threadDumpNameProperty.length() > 0) {
                this.getProject().setProperty(threadDumpNameProperty, threadDump);
            }

            int timeout = waitForDumpTimeout;

            Boolean dumpStatusResult = resourceDumps.getThreadDumpStatus(this.getProfileName(), threadDump).getResultValue();
            boolean dumpFinished = (dumpStatusResult != null) && (dumpStatusResult);
            while (!dumpFinished && (timeout > 0)) {
                try {
                    java.lang.Thread.sleep(waitForDumpPollingInterval);
                    timeout -= waitForDumpPollingInterval;
                } catch (InterruptedException e) {
                }

                dumpStatusResult = resourceDumps.getThreadDumpStatus(this.getProfileName(), threadDump).getResultValue();
                dumpFinished = (dumpStatusResult != null) && (dumpStatusResult);
            }

            if (dumpStatusProperty != null && dumpStatusProperty.length() > 0) {
                this.getProject().setProperty(dumpStatusProperty, String.valueOf(dumpFinished));
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public boolean isSessionLocked() {
        return sessionLocked;
    }

    public void setSessionLocked(boolean sessionLocked) {
        this.sessionLocked = sessionLocked;
    }

    public String getThreadDumpNameProperty() {
        return threadDumpNameProperty;
    }

    public void setThreadDumpNameProperty(String threadDumpNameProperty) {
        this.threadDumpNameProperty = threadDumpNameProperty;
    }

    public int getWaitForDumpTimeout() {
        return waitForDumpTimeout;
    }

    public void setWaitForDumpTimeout(int waitForDumpTimeout) {
        this.waitForDumpTimeout = waitForDumpTimeout;
    }

    public int getWaitForDumpPollingInterval() {
        return waitForDumpPollingInterval;
    }

    public void setWaitForDumpPollingInterval(int waitForDumpPollingInterval) {
        this.waitForDumpPollingInterval = waitForDumpPollingInterval;
    }

    public String getDumpStatusProperty() {
        return dumpStatusProperty;
    }

    public void setDumpStatusProperty(String dumpStatusProperty) {
        this.dumpStatusProperty = dumpStatusProperty;
    }
}
