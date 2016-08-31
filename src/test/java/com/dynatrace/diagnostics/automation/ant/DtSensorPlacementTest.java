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

import com.dynatrace.sdk.server.agentsandcollectors.AgentsAndCollectors;
import com.dynatrace.sdk.server.exceptions.ServerResponseException;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AgentsAndCollectors.class, DtSensorPlacement.class})
public class DtSensorPlacementTest extends AbstractDynatraceTest<DtSensorPlacement> {
    private static final String SENSOR_PLACEMENT_TARGET_NAME = "restartCollector";

    @Before
    public void setUp() throws Exception {
        this.applyFreshEnvironment();
        whenNew(DtSensorPlacement.class).withAnyArguments().thenReturn(this.getTask());

        AgentsAndCollectors agentsAndCollectors = spy(new AgentsAndCollectors(this.getTask().getDynatraceClient()));

        /** define responses */
        doReturn(true).when(agentsAndCollectors).placeHotSensor(1);
        doThrow(new ServerResponseException(500, "message", new Exception())).when(agentsAndCollectors).placeHotSensor(2);

        whenNew(AgentsAndCollectors.class).withAnyArguments().thenReturn(agentsAndCollectors);
    }

    @Override
    protected String getTaskTargetName() {
        return SENSOR_PLACEMENT_TARGET_NAME;
    }

    @Override
    protected DtSensorPlacement createNewInstanceOfTheTask() {
        return new DtSensorPlacement();
    }

    @Test
    public void testSensorPlacementWithSuccess() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(1);
            this.getTask().execute();
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }

    @Test
    public void testSensorPlacementWithException() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(2);
            this.getTask().execute();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertThat(e, instanceOf(BuildException.class));
        }
    }


    @Test
    public void testSensorPlacementProperties() throws Exception {
        this.applyFreshEnvironment();

        try {
            this.getTask().setAgentId(-1);

            assertThat(this.getTask().getAgentId(), is(-1));
        } catch (Exception e) {
            fail(String.format("Exception shouldn't be thrown: %s", e.getMessage()));
        }
    }


}