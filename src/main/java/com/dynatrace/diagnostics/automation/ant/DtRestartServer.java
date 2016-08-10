package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.servermanagement.ServerManagement;
import org.apache.tools.ant.BuildException;

public class DtRestartServer extends DtServerBase {
    private boolean restart = true;

    public void execute() throws BuildException {
        ServerManagement serverManagement = new ServerManagement(this.getDynatraceClient());

        try {
            if (this.restart) {
                serverManagement.restart();
            } else {
                serverManagement.shutdown();
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
