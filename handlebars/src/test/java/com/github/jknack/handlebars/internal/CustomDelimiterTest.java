package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.Variable.Type;

public class CustomDelimiterTest {
    private static final Map<String,Object> EMPTY_MAP = Collections.emptyMap();

    @Test
    public void block() throws Exception {
        Block block = new Block(new Handlebars(), "test", false, Collections.emptyList(), EMPTY_MAP)
            .startDelimiter("`*`").endDelimiter("`*`")
            .body(new Text("inside"));
        assertEquals("`*`#test`*`inside`*`/test`*`", block.text());
    }

    @Test
    public void partial() throws Exception {
        Partial partial = new Partial().template("test", new Text(""), "this")
            .startDelimiter("^^").endDelimiter("%%");
        assertEquals("^^>test%%", partial.text());
    }

    @Test
    public void variable() throws Exception {
        Variable variable =
            new Variable(new Handlebars(), "test", null, Type.VAR).startDelimiter("+-+").endDelimiter("-+-");
        assertEquals("+-+test-+-", variable.text());
    }

    @Test
    public void variableUnescaped() throws Exception {
        Variable variable =
            new Variable(new Handlebars(), "test", null, Type.AMPERSAND_VAR).startDelimiter("-+-").endDelimiter("+-+");
        assertEquals("-+-&test+-+", variable.text());
    }
}
