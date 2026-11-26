/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue1054 extends AbstractTest {

  @Test
  public void escapeSingleBraceDelimiters() throws IOException {
    Handlebars hbs = new Handlebars().startDelimiter("{").endDelimiter("}");
    Hash ctx = $("text", "value");

    assertEquals("{text}", hbs.compileInline("\\{text}").apply(ctx));
    assertEquals("\\{text}", hbs.compileInline("\\{text}").text());
    assertEquals("content {text}", hbs.compileInline("content \\{text}").apply(ctx));
    assertEquals("\\value", hbs.compileInline("\\\\{text}").apply(ctx));
    assertEquals("\\\\ value", hbs.compileInline("\\\\ {text}").apply(ctx));
    assertEquals("{{text}}", hbs.compileInline("\\{{text}}").apply(ctx));
    assertEquals("{{{text}}}", hbs.compileInline("\\{{{text}}}").apply(ctx));
    assertEquals("{!comment}", hbs.compileInline("\\{!comment}").apply(ctx));
    assertEquals("{!--comment--}", hbs.compileInline("\\{!--comment--}").apply(ctx));
    assertEquals("{{~text}}", hbs.compileInline("\\{{~text}}").apply(ctx));
    assertEquals("{=<% %>=}<%text%>", hbs.compileInline("\\{=<% %>=}<%text%>").apply(ctx));
  }

  @Test
  public void escapeSingleAngleBracketDelimiters() throws IOException {
    Handlebars hbs = new Handlebars().startDelimiter("<").endDelimiter(">");
    Hash ctx = $("text", "value");

    assertEquals("<text>", hbs.compileInline("\\<text>").apply(ctx));
    assertEquals("\\<text>", hbs.compileInline("\\<text>").text());
    assertEquals("\\value", hbs.compileInline("\\\\<text>").apply(ctx));
  }

  @Test
  public void escapeDoubleAngleBracketDelimiters() throws IOException {
    Handlebars hbs = new Handlebars().startDelimiter("<<").endDelimiter(">>");
    Hash ctx = $("text", "value");

    assertEquals("<<text>>", hbs.compileInline("\\<<text>>").apply(ctx));
    assertEquals("\\<<text>>", hbs.compileInline("\\<<text>>").text());
    assertEquals("\\value", hbs.compileInline("\\\\<<text>>").apply(ctx));
  }

  @Test
  public void escapeInlineSetDelimiters() throws IOException {
    Hash ctx = $("text", "value");

    shouldCompileTo("{{={ }=}}\\{text}", ctx, "{text}");
    shouldCompileTo("{{={ }=}}\\\\{text}", ctx, "\\value");
    shouldCompileTo("{{={ }=}}\\{{text}}", ctx, "{{text}}");
    shouldCompileTo("{{=<% %>=}}\\<%text%>", ctx, "<%text%>");
    shouldCompileTo("{{=<% %>=}}\\\\<%text%>", ctx, "\\value");
  }

  @Test
  public void shouldEscapeSingleCharExpression() throws IOException {
    Handlebars hbs = new Handlebars().startDelimiter("{").endDelimiter("}");
    assertEquals("{text}", hbs.compileInline("\\{text}").apply($));
    assertEquals("x{text}y", hbs.compileInline("x\\{text}y").apply($));
  }
}
