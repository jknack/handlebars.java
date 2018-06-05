package com.github.jknack.handlebars;

import org.junit.Test;

import java.io.IOException;

public class WriteIntoContextTest extends AbstractTest {
    protected void configure(final Handlebars handlebars) {
        handlebars.registerHelpers(new WriteIntoContextTest.SetHelperClass());
    }

    @Test
    public void shouldBeAbleToWriteIntoContext() throws IOException {
        shouldCompileTo("{{set \"foo\" this}}{{foo}}", "bar", "bar");
    }

    @Test
    public void shouldBeAbleToWriteIntoContextWhenInBlockHelper() throws IOException {
        shouldCompileTo("{{#with data}}{{set \"foo\" field}}{{foo}}{{/with}}", "{\"data\" : {\"field\": \"bar\"}}", "bar");
    }

    @Test
    public void shouldBeAbleToWriteIntoContextWhenInPartial() throws IOException {
        shouldCompileToWithPartials("{{> partial}}", "bar",
                constructPartials("partial", "{{set \"foo\" this}}{{foo}}"),
                "bar");
    }

    private Hash constructPartials(String name, String content) throws IOException {
        return new Hash().$(name, content);
    }

    public static class SetHelperClass {
        public String set(String key, Object value, Options options) throws NoSuchFieldException {
            options.context.combine(key, value);
            return "";
        }
    }
}
