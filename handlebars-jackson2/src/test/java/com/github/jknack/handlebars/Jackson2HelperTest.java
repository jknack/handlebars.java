/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static com.github.jknack.handlebars.IgnoreWindowsLineMatcher.equalsToStringIgnoringWindowsNewLine;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Blog.Views.Public;

/**
 * Unit test for {@link Jackson2Helper}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Jackson2HelperTest {

  @Test
  public void toJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", Jackson2Helper.INSTANCE);

    Template template = handlebars.compileInline("{{@json this}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine(
            "{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}"));
  }

  @Test
  public void toPrettyJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", Jackson2Helper.INSTANCE);

    Template template = handlebars.compileInline("{{@json this pretty=true}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine(
            "{\n"
                + "  \"title\" : \"First Post\",\n"
                + "  \"body\" : \"...\",\n"
                + "  \"comments\" : [ ]\n"
                + "}"));
  }

  @Test
  public void toJSONViewInclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    handlebars.registerHelper("@json", Jackson2Helper.INSTANCE);

    Template template =
        handlebars.compileInline(
            "{{@json this view=\"com.github.jknack.handlebars.Blog$Views$Public\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine(
            "{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}"));
  }

  @Test
  public void toJSONViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json", new Jackson2Helper(mapper));

    Template template =
        handlebars.compileInline(
            "{{@json this view=\"com.github.jknack.handlebars.Blog$Views$Public\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
  }

  @Test
  public void toJSONAliasViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper(
        "@json", new Jackson2Helper(mapper).viewAlias("myView", Public.class));

    Template template = handlebars.compileInline("{{@json this view=\"myView\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
  }

  @Test(expected = HandlebarsException.class)
  public void jsonViewNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json", new Jackson2Helper(mapper));

    Template template = handlebars.compileInline("{{@json this view=\"missing.ViewClass\"}}");

    assertThat(
        template.apply(new Blog("First Post", "...")),
        equalsToStringIgnoringWindowsNewLine("{\"title\":\"First Post\"}"));
  }

  @Test
  public void escapeHtml() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", Jackson2Helper.INSTANCE);

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
}
