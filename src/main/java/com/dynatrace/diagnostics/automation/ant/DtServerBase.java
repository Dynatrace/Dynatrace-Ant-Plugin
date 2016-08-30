/*
 * Dynatrace Ant Plugin
 * Copyright (c) 2008-2016, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *  Neither the name of the dynaTrace software nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.diagnostics.automation.util.DtUtil;
import com.dynatrace.sdk.org.apache.http.client.utils.URIBuilder;
import com.dynatrace.sdk.org.apache.http.impl.client.CloseableHttpClient;
import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Base for Ant tasks which are using server connection
 */
abstract class DtServerBase extends Task {
    private static final String PROTOCOL_WITHOUT_SSL = "http";
    private static final String PROTOCOL_WITH_SSL = "https";

    /**
     * Use unlimited connection timeout
     */
    private static final int CONNECTION_TIMEOUT = 0;

    private String username;
    private String password;
    private String serverUrl;
    private Boolean ignoreSSLErrors;

    /**
     * contains Dynatrace client
     */
    private DynatraceClient dynatraceClient;

    /**
     * Builds configuration required for {@link DynatraceClient}
     *
     * @return {@link BasicServerConfiguration} containing configuration based on parameters provided in properties
     * @throws BuildException whenever connecting to the server, parsing a response or execution fails
     */
    private BasicServerConfiguration buildServerConfiguration() throws BuildException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.getServerUrl());
            URI uri = uriBuilder.build();

            String protocol = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            boolean ssl = this.isProtocolCompatibleWithSsl(protocol);

            return new BasicServerConfiguration(this.getUsername(), this.getPassword(), ssl, host, port, !this.getIgnoreSSLErrors(), CONNECTION_TIMEOUT);
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     * Checks whether given protocol is http (without SSL) or https (with SSL)
     *
     * @param protocol - protocol name extracted from url
     * @return boolean that describes that the given protocol has SSL
     * @throws IllegalArgumentException whenever given protocol name isn't valid (isn't http or https)
     */
    private boolean isProtocolCompatibleWithSsl(String protocol) throws IllegalArgumentException {
        if (!DtUtil.isEmpty(protocol) && (protocol.equals(PROTOCOL_WITH_SSL) || protocol.equals(PROTOCOL_WITHOUT_SSL))) {
            return protocol.equals(PROTOCOL_WITH_SSL);
        }

        throw new IllegalArgumentException(String.format("Invalid protocol name: %s", protocol), new Exception());
    }

    /**
     * Returns {@link DynatraceClient} required for Server SDK classes
     *
     * @return {@link DynatraceClient} with parameters provided in properties
     * @throws BuildException whenever execution fails
     */
    public DynatraceClient getDynatraceClient() throws BuildException {
        if (this.dynatraceClient == null) {
            this.log(String.format("Connection to dynaTrace Server via %s with username %s, ignoring SSL errors: %b", this.getServerUrl(), this.getUsername(), this.getIgnoreSSLErrors()));
            this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration());
        }

        return this.dynatraceClient;
    }

    /**
     * Returns {@link DynatraceClient} required for Server SDK classes
     * <p>
     * Used only for testing purposes
     *
     * @param client - user-defined {@link CloseableHttpClient}
     * @return {@link DynatraceClient} with parameters provided in properties
     * @throws BuildException whenever execution fails
     */
    public void setDynatraceClientWithCustomHttpClient(CloseableHttpClient client) throws BuildException {
        this.log(String.format("Connection to dynaTrace Server via %s with username %s, ignoring SSL errors: %b", this.getServerUrl(), this.getUsername(), this.getIgnoreSSLErrors()));
        this.dynatraceClient = new DynatraceClient(this.buildServerConfiguration(), client);
    }

    public String getUsername() {
        if (this.username == null) {
            String userNameFromProperty = this.getProject().getProperty("dtUsername");

            if (!DtUtil.isEmpty(userNameFromProperty)) {
                this.username = userNameFromProperty;
            }
        }

        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (this.password == null) {
            String passwordFromProperty = this.getProject().getProperty("dtPassword");

            if (!DtUtil.isEmpty(passwordFromProperty)) {
                this.password = passwordFromProperty;
            }
        }

        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerUrl() {
        if (this.serverUrl == null) {
            String serverUrlFromProperty = this.getProject().getProperty("dtServerUrl");

            if (!DtUtil.isEmpty(serverUrlFromProperty)) {
                this.serverUrl = serverUrlFromProperty;
            }
        }

        return this.serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Returns {@code ignoreSSLErrors} flag value. Should never return {@code null}.
     * In case {@code ignoreSSLErrors} flag is not set, also assigns value given
     * in {@code dtIgnoreSSLErrors} Ant property or the default value {@code TRUE}.
     */
    public Boolean getIgnoreSSLErrors() {
        if (this.ignoreSSLErrors == null) {
            String ignoreSSLErrorsFromProperty = this.getProject().getProperty("dtIgnoreSSLErrors");
            // only override default value if property is a valid boolean string representation
            // without that malformed property value would cause returning false
            if (Boolean.FALSE.toString().equalsIgnoreCase(ignoreSSLErrorsFromProperty) || Boolean.TRUE.toString().equalsIgnoreCase(ignoreSSLErrorsFromProperty)) {
                this.ignoreSSLErrors = Boolean.valueOf(ignoreSSLErrorsFromProperty);
            } else {
                /* malformed property value, assign default value */
                this.ignoreSSLErrors = Boolean.TRUE;
            }
        }

        return this.ignoreSSLErrors;
    }

    public void setIgnoreSSLErrors(Boolean ignoreSSLErrors) {
        this.ignoreSSLErrors = ignoreSSLErrors;
    }
}
