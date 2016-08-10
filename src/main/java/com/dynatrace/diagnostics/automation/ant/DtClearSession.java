package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.sessions.Sessions;
import org.apache.tools.ant.BuildException;

public class DtClearSession extends DtServerProfileBase {

    public void execute() throws BuildException {
        Sessions sessions = new Sessions(this.getDynatraceClient());

        try {
            sessions.clear(this.getProfileName());
        } catch (ServerResponseException | ServerConnectionException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }
}
