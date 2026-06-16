/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i1169;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class SourceTextTest extends AbstractTest {

  @Test
  public void testGetSourceTextPreservesWhitespace() throws IOException {
    assertEquals(" {{foo}} ", compile(" {{foo}} ").text());
    assertEquals("{{foo}}", compile("{{foo}}").text());
    assertEquals("{{ foo }}", compile("{{ foo }}").text());
    assertEquals("{{  foo  }}", compile("{{  foo  }}").text());
  }

  @Test
  public void testGetSourceTextVarAmp() throws IOException {
    assertEquals("{{&var}}", compile("{{&var}}").text());
    assertEquals("{{& var }}", compile("{{& var }}").text());
    assertEquals("{{&  var  }}", compile("{{&  var  }}").text());
  }

  @Test
  public void testGetSourceTextVar3() throws IOException {
    assertEquals("{{{var}}}", compile("{{{var}}}").text());
    assertEquals("{{{ var }}}", compile("{{{ var }}}").text());
    assertEquals("{{{  var  }}}", compile("{{{  var  }}}").text());
  }

  @Test
  public void testGetSourceTextSection() throws IOException {
    assertEquals(
        "{{#section}}content{{/section}}", compile("{{#section}}content{{/section}}").text());
    assertEquals(
        "{{# section }}content{{/ section }}",
        compile("{{# section }}content{{/ section }}").text());
  }

  @Test
  public void testGetSourceTextInvertedSection() throws IOException {
    assertEquals(
        "{{^section}}content{{/section}}", compile("{{^section}}content{{/section}}").text());
    assertEquals(
        "{{^ section }}content{{/ section }}",
        compile("{{^ section }}content{{/ section }}").text());
  }

  @Test
  public void testGetSourceTextPartial() throws IOException {
    assertEquals("{{>user}}", compile("{{>user}}", $(), $("user", "{{user}}")).text());
    assertEquals("{{> user }}", compile("{{> user }}", $(), $("user", "{{user}}")).text());
  }

  @Test
  public void testGetSourceTextPartialWithContext() throws IOException {
    assertEquals(
        "{{>user context}}", compile("{{>user context}}", $(), $("user", "{{user}}")).text());
    assertEquals(
        "{{> user context }}", compile("{{> user context }}", $(), $("user", "{{user}}")).text());
  }

  @Test
  public void testGetSourceTextHelper() throws IOException {
    assertEquals(
        "{{with context arg0 hash=hash0}}", compile("{{with context arg0 hash=hash0}}").text());
    assertEquals(
        "{{ with context arg0 hash=hash0 }}", compile("{{ with context arg0 hash=hash0 }}").text());
  }

  @Test
  public void testGetSourceTextBlockHelper() throws IOException {
    assertEquals(
        "{{#with context}}content{{/with}}", compile("{{#with context}}content{{/with}}").text());
    assertEquals(
        "{{# with context }}content{{/ with }}",
        compile("{{# with context }}content{{/ with }}").text());
  }

  @Test
  public void testGetSourceTextPreservesAllWhitespaceTypes() throws IOException {
    // Template with tabs
    String source1 = "{{\tfoo\t}}";
    assertEquals(source1, compile(source1).text());

    // Template with multiple spaces
    String source2 = "{{ foo }}  {{  bar  }}";
    assertEquals(source2, compile(source2).text());

    // Template with mixed whitespace
    String source3 = "{{  \tfoo\t  }}";
    assertEquals(source3, compile(source3).text());
  }

  @Test
  public void testGetSourceTextWithTextContent() throws IOException {
    String source = "Hello {{ name }}, welcome!";
    assertEquals(source, compile(source).text());
  }
}
