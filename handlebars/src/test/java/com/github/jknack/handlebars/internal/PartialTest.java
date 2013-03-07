package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PartialTest {
    @Test
    public void testCustomDelimiter() throws Exception {
        Partial partial = new Partial().template("test", new Text(""), "this")
            .startDelimiter("^^").endDelimiter("%%");
        assertEquals("^^>test%%", partial.text());
    }
}
