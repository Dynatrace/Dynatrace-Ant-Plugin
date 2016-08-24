package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.BasicServerConfiguration;
import com.dynatrace.sdk.server.DynatraceClient;
import org.apache.tools.ant.BuildException;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DtStartTestServerConfigurationTest {

    @Before
    @Test
    public void testBuildServerConfigurationHttpWithSuccess() {
        DtStartTest startTest = new DtStartTest();

        startTest.setUsername("admin");
        startTest.setPassword("adminPassword");
        startTest.setIgnoreSSLErrors(true);
        startTest.setServerUrl("http://localhost:8080");

        DynatraceClient client;

        try {
            startTest.setDynatraceClientWithCustomHttpClient(null);
            client = startTest.getDynatraceClient();

            assertTrue(client.getConfiguration() instanceof BasicServerConfiguration);
            BasicServerConfiguration configuration = (BasicServerConfiguration) client.getConfiguration();

            assertThat(configuration.getName(), is("admin"));
            assertThat(configuration.getPassword(), is("adminPassword"));
            assertThat(configuration.isValidateCertificates(), is(false));
            assertThat(configuration.getHost(), is("localhost"));
            assertThat(configuration.getPort(), is(8080));
            assertThat(configuration.isSSL(), is(false));

            assertThat(startTest.getIgnoreSSLErrors(), is(true));

        } catch (BuildException e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testBuildServerConfigurationHttpsWithSuccess() {
        DtStartTest startTest = new DtStartTest();

        startTest.setUsername("admin");
        startTest.setPassword("adminPassword");
        startTest.setIgnoreSSLErrors(true);
        startTest.setServerUrl("https://localhost:8080");

        DynatraceClient client;

        try {
            startTest.setDynatraceClientWithCustomHttpClient(null);
            client = startTest.getDynatraceClient();

            assertTrue(client.getConfiguration() instanceof BasicServerConfiguration);
            BasicServerConfiguration configuration = (BasicServerConfiguration) client.getConfiguration();

            assertThat(configuration.getName(), is("admin"));
            assertThat(configuration.getPassword(), is("adminPassword"));
            assertThat(configuration.isValidateCertificates(), is(false));
            assertThat(configuration.getHost(), is("localhost"));
            assertThat(configuration.getPort(), is(8080));
            assertThat(configuration.isSSL(), is(true));

        } catch (BuildException e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testBuildServerConfigurationWithFail() {
        DtStartTest startTest = new DtStartTest();

        startTest.setUsername("admin");
        startTest.setPassword("adminPassword");
        startTest.setIgnoreSSLErrors(true);
        startTest.setServerUrl("ftp://localhost:8080");


        try {
            startTest.setDynatraceClientWithCustomHttpClient(null);

            fail("Exception is expected to be thrown.");
        } catch (BuildException e) {
            assertThat(e.getMessage(), StringContains.containsString("ftp"));
        }
    }
}