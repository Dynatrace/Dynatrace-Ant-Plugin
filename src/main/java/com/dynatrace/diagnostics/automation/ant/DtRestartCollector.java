package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.apache.tools.ant.BuildException;

public class DtRestartCollector extends DtServerBase {
    private boolean restart = true;
    private String collector;

    public void execute() throws BuildException {
        AgentsAndCollectors agentsAndCollectors = new AgentsAndCollectors(this.getDynatraceClient());

        try {
            if (this.restart) {
                agentsAndCollectors.restartCollector(this.getCollector());
            } else {
                agentsAndCollectors.shutdownCollector(this.getCollector());
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }
}
