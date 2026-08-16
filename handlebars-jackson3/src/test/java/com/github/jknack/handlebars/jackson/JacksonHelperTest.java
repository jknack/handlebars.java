/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.jackson.Blog.Views.Public;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.jknack.handlebars.jackson.JacksonHelperTest.IgnoreWindowsLineMatcher.equalsToStringIgnoringWindowsNewLine;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit test for {@link JacksonHelper}.
 *
 * @author edgar.espina, jolarsen
 * @since 4.5.5
 */
class JacksonHelperTest {

  @Test
  void toJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Template template = handlebars.compileInline("{{@json this}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine(
            "{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}"));
  }

  @Test
  void toPrettyJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Template template = handlebars.compileInline("{{@json this pretty=true}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine(
            """
                {
                  "title" : "First Post",
                  "body" : "...",
                  "comments" : [ ]
                }"""));
  }

  @Test
  void toJSONViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Template template =
        handlebars.compileInline(
            "{{@json this view=\"com.github.jknack.handlebars.jackson.Blog$Views$Public\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
  }

  @Test
  void toJSONAliasViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = JsonMapper.builder().build();

    handlebars.registerHelper("@json", new JacksonHelper(mapper).viewAlias("myView", Public.class));

    Template template = handlebars.compileInline("{{@json this view=\"myView\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
  }

  @Test
  void jsonViewNotFound() {
    Assertions.assertThrows(
        HandlebarsException.class,
        () -> {
          Handlebars handlebars = new Handlebars();

          ObjectMapper mapper = JsonMapper.builder().build();

          handlebars.registerHelper("@json", new JacksonHelper(mapper));

          Template template = handlebars.compileInline("{{@json this view=\"missing.ViewClass\"}}");

          assertThat(
              template.apply(new Blog("First Post", "...")),
              equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
        });
  }

  @Test
  void escapeHtml() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Map<String, String> model = new HashMap<String, String>();
    model.put("script", "<script text=\"text/javascript\"></script>");

    assertThat(
        handlebars.compileInline("{{@json this}}").apply(model),
        equalsToStringIgnoringWindowsNewLine(
            "{\"script\":\"<script text=\\\"text/javascript\\\"></script>\"}"));

    assertThat(
        handlebars.compileInline("{{@json this escapeHTML=true}}").apply(model),
        equalsToStringIgnoringWindowsNewLine(
            "{\"script\":\"\\u003Cscript"
                + " text=\\\"text/javascript\\\"\\u003E\\u003C/script\\u003E\"}"));
  }

  static class IgnoreWindowsLineMatcher extends TypeSafeMatcher<String> {

    private final String value;

    private IgnoreWindowsLineMatcher(String value) {
      this.value = value;
    }

    static IgnoreWindowsLineMatcher equalsToStringIgnoringWindowsNewLine(String value) {
      return new IgnoreWindowsLineMatcher(value);
    }

    @Override
    protected boolean matchesSafely(String item) {
      return item.replace("\r\n", "\n").equals(value);
    }

    @Override
    public void describeTo(Description description) {
      description
              .appendText("a string ")
              .appendText("ignoring \\r")
              .appendText(" ")
              .appendValue(value);
    }
  }
}
