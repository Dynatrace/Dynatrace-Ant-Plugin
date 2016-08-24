package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.systemprofiles.SystemProfiles;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemProfiles.class, DtActivateConfiguration.class})
public class DtActivateConfigurationTest extends AbstractDynatraceTest<DtActivateConfiguration> {
    private static final String ACTIVATE_CONFIGURATION_TARGET_NAME = "activateConfiguration";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtActivateConfiguration.class).withAnyArguments().thenReturn(this.getTask());

        SystemProfiles systemProfiles = spy(new SystemProfiles(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(systemProfiles).activateProfileConfiguration("profile", "config-true");
        doReturn(false).when(systemProfiles).activateProfileConfiguration("profile", "config-false");
        doThrow(new ServerConnectionException("message", new Exception())).when(systemProfiles).activateProfileConfiguration("profile", "config-exception");

        whenNew(SystemProfiles.class).withAnyArguments().thenReturn(systemProfiles);
    }

    @Override
    protected String getTaskTargetName() {
        return ACTIVATE_CONFIGURATION_TARGET_NAME;
    }

    @Override
    protected DtActivateConfiguration createNewInstanceOfTheTask() {
        return new DtActivateConfiguration();
    }

    @Test
    public void testActivateConfigurationSuccessTrue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile");
            this.getTask().setConfiguration("config-true");
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testActivateConfigurationSuccessFalse() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile");
            this.getTask().setConfiguration("config-false");
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testActivateConfigurationWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("profile");
            this.getTask().setConfiguration("config-exception");
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }
}