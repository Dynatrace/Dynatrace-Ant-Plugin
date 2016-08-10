package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.memorydumps.MemoryDumps;
import com.dynatrace.sdk.server.memorydumps.models.AgentPattern;
import com.dynatrace.sdk.server.memorydumps.models.JobState;
import com.dynatrace.sdk.server.memorydumps.models.MemoryDumpJob;
import com.dynatrace.sdk.server.memorydumps.models.StoredSessionType;
import org.apache.tools.ant.BuildException;

import java.net.URI;
import java.net.URISyntaxException;

public class DtMemoryDump extends DtAgentBase {

    private String dumpType;
    private boolean sessionLocked;
    private String memoryDumpNameProperty;

    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;
    private String dumpStatusProperty;
    private boolean doGc;
    private boolean autoPostProcess;
    private boolean capturePrimitives;
    private boolean captureStrings;

    @Override
    public void execute() throws BuildException {
        System.out.println("Creating Memory Dump for " + getProfileName() + "-" + getAgentName() + "-" + getHostName() + "-" + getProcessId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        MemoryDumps memoryDumps = new MemoryDumps(this.getDynatraceClient());

        MemoryDumpJob memoryDumpJob = new MemoryDumpJob();
        memoryDumpJob.setAgentPattern(new AgentPattern(this.getAgentName(), this.getHostName(), this.getProcessId()));
        memoryDumpJob.setSessionLocked(this.isSessionLocked());
        memoryDumpJob.setCaptureStrings(this.getCaptureStrings());
        memoryDumpJob.setCapturePrimitives(this.getCapturePrimitives());
        memoryDumpJob.setPostProcessed(this.getAutoPostProcess());
        memoryDumpJob.setDogc(this.getDoGc());

        if (this.getDumpType() != null) {
            memoryDumpJob.setStoredSessionType(StoredSessionType.fromInternal(this.getDumpType())); /* TODO FIXME - dump type is wrong? use new values with prefixes! */
        }

        try {
            String memoryDumpLocation = memoryDumps.createMemoryDumpJob(this.getProfileName(), memoryDumpJob);

            URI uri = new URI(memoryDumpLocation);
            String[] uriPathArray = uri.getPath().split("/");

            String memoryDump = null;

            try {
                memoryDump = uriPathArray[uriPathArray.length - 1];
            } catch (Exception e) {
                throw new BuildException("Malformed memory dump response", new Exception()); //$NON-NLS-1$
            }

            if (memoryDumpNameProperty != null && memoryDumpNameProperty.length() > 0) {
                this.getProject().setProperty(memoryDumpNameProperty, memoryDump);
            }

            if (memoryDump == null || memoryDump.length() == 0) {
                throw new BuildException("Memory Dump wasn't taken", new Exception()); //$NON-NLS-1$
            }

            int timeout = waitForDumpTimeout;

            JobState memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), memoryDump).getState();
            boolean dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);

            while (!dumpFinished && (timeout > 0)) {
                try {
                    java.lang.Thread.sleep(waitForDumpPollingInterval);
                    timeout -= waitForDumpPollingInterval;
                } catch (InterruptedException e) {
                }

                memoryDumpJobState = memoryDumps.getMemoryDumpJob(this.getProfileName(), memoryDump).getState();
                dumpFinished = memoryDumpJobState.equals(JobState.FINISHED) || memoryDumpJobState.equals(JobState.FAILED);
            }

            if (dumpStatusProperty != null && dumpStatusProperty.length() > 0)
                this.getProject().setProperty(dumpStatusProperty, String.valueOf(dumpFinished));
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    private boolean getCaptureStrings() {
        return captureStrings;
    }

    public void setCaptureStrings(boolean captureStrings) {
        this.captureStrings = captureStrings;
    }

    private boolean getCapturePrimitives() {
        return capturePrimitives;
    }

    public void setCapturePrimitives(boolean capturePrimitives) {
        this.capturePrimitives = capturePrimitives;
    }

    private boolean getAutoPostProcess() {
        return autoPostProcess;
    }

    public void setAutoPostProcess(boolean autoPostProcess) {
        this.autoPostProcess = autoPostProcess;
    }

    private boolean getDoGc() {
        return doGc;
    }

    public void setDoGc(boolean doGc) {
        this.doGc = doGc;
    }

    public String getDumpType() {
        return dumpType;
    }

    public void setDumpType(String dumpType) {
        this.dumpType = dumpType;
    }

    public boolean isSessionLocked() {
        return sessionLocked;
    }

    public void setSessionLocked(boolean sessionLocked) {
        this.sessionLocked = sessionLocked;
    }

    public String getMemoryDumpNameProperty() {
        return memoryDumpNameProperty;
    }

    public void setMemoryDumpNameProperty(String memoryDumpNameProperty) {
        this.memoryDumpNameProperty = memoryDumpNameProperty;
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
