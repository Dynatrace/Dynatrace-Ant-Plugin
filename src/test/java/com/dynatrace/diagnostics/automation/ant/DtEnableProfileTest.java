package com.dynatrace.diagnostics.automation.ant;

import com.dynatrace.sdk.server.exceptions.ServerConnectionException;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
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
@PrepareForTest({SystemProfiles.class, DtEnableProfile.class})
public class DtEnableProfileTest extends AbstractDynatraceTest<DtEnableProfile> {
    private static final String ENABLE_PROFILE_TARGET_NAME = "enableProfile";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtEnableProfile.class).withAnyArguments().thenReturn(this.getTask());

        SystemProfiles systemProfiles = spy(new SystemProfiles(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(systemProfiles).enableProfile("enable-success-true");
        doReturn(false).when(systemProfiles).enableProfile("enable-success-false");
        doReturn(true).when(systemProfiles).disableProfile("disable-success-true");
        doReturn(false).when(systemProfiles).disableProfile("disable-success-false");

        doThrow(new ServerConnectionException("message", new Exception())).when(systemProfiles).enableProfile("enable-exception");
        doThrow(new ServerResponseException(500, "message", new Exception())).when(systemProfiles).disableProfile("disable-exception");

        whenNew(SystemProfiles.class).withAnyArguments().thenReturn(systemProfiles);

        /** verify default values */
/*        assertThat(this.getTask().isEnable(), is(true));*/
    }

    @Override
    protected String getTaskTargetName() {
        return ENABLE_PROFILE_TARGET_NAME;
    }

    @Override
    protected DtEnableProfile createNewInstanceOfTheTask() {
        return new DtEnableProfile();
    }

    @Test
    public void testEnableTrueSuccessTrue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-success-true");
            this.getTask().setEnable(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableTrueSuccessFalse() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-success-false");
            this.getTask().setEnable(true);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableFalseSuccessTrue() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-success-true");
            this.getTask().setEnable(false);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableFalseSuccessFalse() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-success-false");
            this.getTask().setEnable(false);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testEnableTrueWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("enable-exception");
            this.getTask().setEnable(true);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }

    @Test
    public void testEnableFalseWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setProfileName("disable-exception");
            this.getTask().setEnable(false);
            this.getTask().execute();

            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));

            /** maybe exception message should be more verbose? */
            //assertThat(e.getMessage(), containsString("500"));
        }
    }
}