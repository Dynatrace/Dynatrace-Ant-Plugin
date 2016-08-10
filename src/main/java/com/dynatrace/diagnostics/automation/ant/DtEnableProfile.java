package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
import org.apache.tools.ant.BuildException;

public class DtEnableProfile extends DtServerProfileBase {

    private boolean enable;

    public void execute() throws BuildException {
        try {
            SystemProfiles systemProfiles = new SystemProfiles(this.getDynatraceClient());
            if (this.enable) {
                systemProfiles.enableProfile(this.getProfileName());
            } else {
                systemProfiles.disableProfile(this.getProfileName());
            }
        } catch (ServerConnectionException | ServerResponseException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
