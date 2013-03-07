package com.github.jknack.handlebars.internal;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;

public class BlockTest {
    private static final Map<String,Object> EMPTY_MAP = Collections.emptyMap();

    @Test
    public void testCustomDelimiter() throws Exception {
        Block block = new Block(new Handlebars(), "test", false, Collections.emptyList(), EMPTY_MAP)
            .startDelimiter("`*`").endDelimiter("`*`")
            .body(new Text("inside"));
        assertEquals("`*`#test`*`inside`*`/test`*`", block.text());
    }
}
