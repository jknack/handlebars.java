package com.github.jknack.handlebars.i294;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue294 extends AbstractTest {

  @Test
  public void escapeVars() throws IOException {
    shouldCompileTo("\\{{foo}}", $, "{{foo}}");
  }

  @Test
  public void escapeVarsWithText() throws IOException {
    shouldCompileTo("before \\{{foo}} after", $, "before {{foo}} after");
  }

  @Test
  public void escapeVsUnescape() throws IOException {
    shouldCompileTo("\\{{foo}} {{foo}}", $("foo", "bar"), "{{foo}} bar");
  }

  @Test
  public void escapeMultiline() throws IOException {
    shouldCompileTo("\\{{foo\n}}", $("foo", "bar"), "{{foo\n}}");
  }

  @Test
  public void blockEscape() throws IOException {
    shouldCompileTo("\\{{#foo}}", $("foo", "bar"), "{{#foo}}");
  }

  @Test
  public void blockEscapeWithParams() throws IOException {
    shouldCompileTo("\\{{#foo x a x}}", $("foo", "bar"), "{{#foo x a x}}");
  }

  @Test
  public void escapeVarToText() throws IOException {
    assertEquals("\\{{foo}}", compile("\\{{foo}}").text());
  }

}
