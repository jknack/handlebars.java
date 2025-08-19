package com.github.jknack.handlebars;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EscapingStrategyTest {
    @Test
    public void shouldEscapeVariableForJSON() throws IOException {
        Template template = new Handlebars().with(EscapingStrategy.JSON).compileInline("{{this}}");

        assertEquals("\\\"", template.apply("\""));
        assertEquals("\\\\", template.apply("\\"));
        assertEquals("\\/", template.apply("/"));
    }
}
