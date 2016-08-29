package com.dynatrace.diagnostics.automation.ant;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CustomPropertyTest {
    @Test
    public void setAndGetKeyTest() throws Exception {
        CustomProperty customProperty = new CustomProperty();
        customProperty.setKey("abc");

        assertThat(customProperty.getKey(), is("abc"));
    }

    @Test
    public void setAndGetValueTest() throws Exception {
        CustomProperty customProperty = new CustomProperty();
        customProperty.setValue("abc");

        assertThat(customProperty.getValue(), is("abc"));

    }
}