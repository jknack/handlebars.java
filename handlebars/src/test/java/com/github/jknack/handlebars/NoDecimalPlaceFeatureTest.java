package com.github.jknack.handlebars;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class NoDecimalPlaceFeatureTest {
    private static Handlebars handlebars = new Handlebars();
    private String[] array = {"1", "2"};

    @BeforeClass
    public static void setUp() throws Exception {
        handlebars.registerHelpers(new File("src/test/resources/com/github/jknack/handlebars/js/decimalPlaceFeatureHelper.js"));
    }

    @Test
    public void numberWithoutDecimalPlaceHasToBeReturnedWhenFeatureFlagIsEnabled() throws Exception {
        handlebars.setIntegerWithoutDecimalPlaceFeatureEnabled(true);
        String result = handlebars.compileInline("{{decimalPlaceFeatureHelper this}}").apply(array);
        assertEquals("2", result);
    }

    @Test
    public void numberWithDecimalPlaceHasToBeReturnedWhenFeatureFlagIsDisabled() throws Exception {
        handlebars.setIntegerWithoutDecimalPlaceFeatureEnabled(false);
        String result = handlebars.compileInline("{{decimalPlaceFeatureHelper this}}").apply(array);
        assertEquals("2.0", result);
    }
}
