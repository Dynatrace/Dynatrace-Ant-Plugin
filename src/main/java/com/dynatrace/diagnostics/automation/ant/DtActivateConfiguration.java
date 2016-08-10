package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
import org.apache.tools.ant.BuildException;

public class DtActivateConfiguration extends DtServerProfileBase {

    private String configuration;

    public void execute() throws BuildException {
        try {
            SystemProfiles systemProfiles = new SystemProfiles(this.getDynatraceClient());
            systemProfiles.activateProfileConfiguration(this.getProfileName(), this.getConfiguration());
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
