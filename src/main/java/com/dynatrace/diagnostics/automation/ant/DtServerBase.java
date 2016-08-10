package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.org.apache.http.client.utils.URIBuilder;
import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class DtServerBase extends Task {

    private static final String PROTOCOL_WITHOUT_SSL = "http";
    private static final String PROTOCOL_WITH_SSL = "https";
    private String username = null;
    private String password = null;
    private String serverUrl = null;
    private DynatraceClient dynatraceClient;

    /* TODO: default values for BasicServerConfiguration should be better-looking */
    private BasicServerConfiguration buildServerConfiguration() {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.getServerUrl());
            URI uri = uriBuilder.build();

            String protocol = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            boolean ssl = BasicServerConfiguration.DEFAULT_SSL;

            if (protocol != null && (protocol.equals(PROTOCOL_WITH_SSL) || protocol.equals(PROTOCOL_WITHOUT_SSL))) {
                ssl = protocol.equals(PROTOCOL_WITH_SSL);
            } else {
                throw new URISyntaxException(protocol, "Invalid protocol name in serverUrl"); //maybe something better?
            }

            return new BasicServerConfiguration(this.getUsername(), this.getPassword(), ssl, host, port, false, BasicServerConfiguration.DEFAULT_CONNECTION_TIMEOUT);
        } catch (URISyntaxException e) {
            throw new BuildException(e.getMessage(), e); //? proper way?
        }
    }

    public DynatraceClient getDynatraceClient() throws BuildException {
        if (this.dynatraceClient == null) {
            this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration());
        }

        return this.dynatraceClient;
    }

    public String getUsername() {
        if (username == null) {
            String dtUsername = this.getProject().getProperty("dtUsername"); //$NON-NLS-1$
            if (dtUsername != null && dtUsername.length() > 0)
                username = dtUsername;
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (password == null) {
            String dtPassword = this.getProject().getProperty("dtPassword"); //$NON-NLS-1$
            if (dtPassword != null && dtPassword.length() > 0)
                password = dtPassword;
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        if (serverUrl == null) {
            String dtServerUrl = this.getProject().getProperty("dtServerUrl"); //$NON-NLS-1$
            if (dtServerUrl != null && dtServerUrl.length() > 0)
                serverUrl = dtServerUrl;
        }
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
