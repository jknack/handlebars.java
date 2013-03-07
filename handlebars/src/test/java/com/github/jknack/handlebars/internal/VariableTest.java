package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.Variable.Type;

public class VariableTest {
    @Test
    public void testCustomDelimiterText() throws Exception {
        Variable variable =
            new Variable(new Handlebars(), "test", null, Type.VAR).startDelimiter("+-+").endDelimiter("-+-");
        assertEquals("+-+test-+-", variable.text());
    }

    @Test
    public void testCustomDelimiterUnescaped() throws Exception {
        Variable variable =
            new Variable(new Handlebars(), "test", null, Type.AMPERSAND_VAR).startDelimiter("-+-").endDelimiter("+-+");
        assertEquals("-+-&test+-+", variable.text());
    }
}