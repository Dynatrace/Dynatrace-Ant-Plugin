package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import org.apache.tools.ant.BuildException;

public class DtReanalyzeSession extends DtServerBase {

    private String sessionName;
    private int reanalyzeSessionTimeout = 60000;
    private int reanalyzeSessionPollingInterval = 5000;
    private String reanalyzeStatusProperty;

    public void execute() throws BuildException {
        boolean reanalyzeFinished = false;

        Sessions sessions = new Sessions(this.getDynatraceClient());

        try {
            if (sessions.reanalyze(this.getSessionName())) {
                int timeout = reanalyzeSessionTimeout;
                reanalyzeFinished = sessions.getReanalysisStatus(this.getSessionName());
                while (!reanalyzeFinished && (timeout > 0)) {
                    try {
                        java.lang.Thread.sleep(getReanalyzeSessionPollingInterval());
                        timeout -= getReanalyzeSessionPollingInterval();
                    } catch (InterruptedException e) {
                    }

                    reanalyzeFinished = sessions.getReanalysisStatus(this.getSessionName());
                }
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }

        if (getReanalyzeStatusProperty() != null && getReanalyzeStatusProperty().length() > 0) {
            this.getProject().setProperty(getReanalyzeStatusProperty(), String.valueOf(reanalyzeFinished));
        }
    }

    public int getReanalyzeSessionTimeout() {
        return reanalyzeSessionTimeout;
    }

    public void setReanalyzeSessionTimeout(int reanalyzeSessionTimeout) {
        this.reanalyzeSessionTimeout = reanalyzeSessionTimeout;
    }

    public String getReanalyzeStatusProperty() {
        return reanalyzeStatusProperty;
    }

    public void setReanalyzeStatusProperty(String reanalyzeStatusProperty) {
        this.reanalyzeStatusProperty = reanalyzeStatusProperty;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public int getReanalyzeSessionPollingInterval() {
        return reanalyzeSessionPollingInterval;
    }

    public void setReanalyzeSessionPollingInterval(
            int reanalyzeSessionPollingInterval) {
        this.reanalyzeSessionPollingInterval = reanalyzeSessionPollingInterval;
    }
}
