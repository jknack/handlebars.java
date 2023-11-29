package com.github.jknack.handlebars.i1084;

import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Issue1084 extends AbstractTest {

    @Test
    public void escapeRawVars() throws IOException {
        shouldCompileTo("\\{{{foo}}}", $, "{{{foo}}}");
    }

    @Test
    public void escapeRawVarsWithText() throws IOException {
        shouldCompileTo("before \\{{{foo}}} after", $, "before {{{foo}}} after");
    }

    @Test
    public void escapeRawVsUnescape() throws IOException {
        shouldCompileTo("\\{{{foo}}} {{{foo}}}", $("foo", "bar"), "{{{foo}}} bar");
    }

    @Test
    public void escapeRawMultiline() throws IOException {
        shouldCompileTo("\\{{{foo\n}}}", $("foo", "bar"), "{{{foo\n}}}");
    }

    @Test
    public void rawBlockEscape() throws IOException {
        shouldCompileTo("\\{{{#foo}}}", $("foo", "bar"), "{{{#foo}}}");
    }

    @Test
    public void rawBlockEscapeWithParams() throws IOException {
        shouldCompileTo("\\{{{#foo x a x}}}", $("foo", "bar"), "{{{#foo x a x}}}");
    }

    @Test
    public void escapeRawVarToText() throws IOException {
        assertEquals("\\{{{foo}}}", compile("\\{{{foo}}}").text());
    }

}

