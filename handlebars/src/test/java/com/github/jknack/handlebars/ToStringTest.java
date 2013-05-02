package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;

public class ToStringTest {

    public static class UnsafeString {
        String underlying;

        public UnsafeString(String underlying) {
            this.underlying = underlying;
        }

        @Override
        public String toString() {
            return "<h1>" + underlying + "</h1>";
        }
    }

    @Test
    public void unsafeString() throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template = handlebars.compileInline("{{this}}");

        String result = template.apply(new UnsafeString("Hello"));

        assertEquals("&lt;h1&gt;Hello&lt;/h1&gt;", result);
    }
}
