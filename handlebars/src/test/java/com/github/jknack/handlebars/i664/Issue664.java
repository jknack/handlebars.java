package com.github.jknack.handlebars.i664;


import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Issue664 extends AbstractTest {

    // Windows newline "\r\n" should not cause problems during compilation of templates
    @Test
    public void windowsNewlineShouldNotCauseErrors() throws IOException {
        assertEquals("{{#if value}}true{{else}}false{{/if}}",
                compile("{{#if\r\nvalue}}true{{else}}false{{/if}}").text());

       }

}