package com.github.jknack.handlebars.bench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Literals;
import com.github.jknack.handlebars.MapTemplateLoader;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.bench.Bench.Unit;

public class TemplateApplyBenchTest {

  @Test
  public void helloWorld() throws IOException {
    final String template = "Hello World!";
    final Handlebars handlebars = new Handlebars();
    final Template t = handlebars.compile(template);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(null);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @Test
  public void variables() throws IOException {
    final String template = "Hello {{name}}! You have {{count}} new messages.";
    final Handlebars handlebars = new Handlebars();
    final Template t = handlebars.compile(template);

    final Map<String, Object> context = new HashMap<String, Object>();
    context.put("name", "Mick");
    context.put("count", 30);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @Test
  public void object() throws IOException {
    final String template = "{{#with person}}{{name}}{{age}}{{/with}}";
    final Handlebars handlebars = new Handlebars();
    final Template t = handlebars.compile(template);
    final Map<String, Object> context = new HashMap<String, Object>();
    Map<String, Object> person = new HashMap<String, Object>();
    person.put("name", "Larry");
    person.put("age", 45);
    context.put("person", person);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @Test
  public void array() throws IOException {
    final String template = "{{#each names}}{{name}}{{/each}}";
    final Handlebars handlebars = new Handlebars();
    final Map<String, Object> context = new HashMap<String, Object>();
    List<Map<String, String>> names = new ArrayList<Map<String, String>>();
    names.add(Literals.$("name", "Moe"));
    names.add(Literals.$("name", "Larry"));
    names.add(Literals.$("name", "Curly"));
    names.add(Literals.$("name", "Shemp"));
    context.put("names", names);

    final Template t = handlebars.compile(template);
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @Test
  public void complex() throws IOException {
    final String template =
        "<h1>{{header}}</h1>{{#if items}}<ul>{{#each items}}{{#if current}}" +
            "<li><strong>{{name}}</strong></li>{{^}}" +
            "<li><a href=\"{{url}}\">{{name}}</a></li>{{/if}}" +
            "{{/each}}</ul>{{^}}<p>The list is empty.</p>{{/if}}";
    final Handlebars handlebars = new Handlebars();
    final Template t = handlebars.compile(template);
    final Map<String, Object> context = new HashMap<String, Object>();

    context.put("header", new Lambda<Object, String>() {
      @Override
      public String apply(final Object context, final Template template)
          throws IOException {
        return "Colors";
      }
    });
    List<Map<String, String>> items = new ArrayList<Map<String, String>>();
    items.add(Literals.$("name", "red").$("current", "yes").$("url", "#Red"));
    items
        .add(Literals.$("name", "green").$("current", "no").$("url", "#Green"));
    items.add(Literals.$("name", "blue").$("current", "no").$("url", "#Blue"));
    context.put("items", items);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void recursion() throws IOException {
    final String template = "{{name}}{{#each kids}}{{>recursion}}{{/each}}";
    final Map<String, String> templates = new HashMap<String, String>();
    templates.put("/recursion.hbs",
        "{{name}}{{#each kids}}{{>recursion}}{{/each}}");
    final Handlebars handlebars =
        new Handlebars(new MapTemplateLoader(templates));

    final Map<String, Object> kids111 = new HashMap<String, Object>();
    kids111.put("name", "1.1.1");
    kids111.put("kids", new ArrayList<Object>());

    final Map<String, Object> kids11 = new HashMap<String, Object>();
    kids11.put("name", "1.1");
    kids11.put("kids", Arrays.asList(kids111));

    final Map<String, Object> context = new HashMap<String, Object>();
    context.put("name", "1");
    context.put("kids", Arrays.asList(kids11));

    final Template t = handlebars.compile(template);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  @Test
  public void partial() throws IOException {
    final String template = "{{#each peeps}}{{>variables}}{{/each}}";
    final Map<String, String> templates = new HashMap<String, String>();
    templates.put("/variables.hbs",
        "Hello {{name}}! You have {{count}} new messages.");
    final Handlebars handlebars =
        new Handlebars(new MapTemplateLoader(templates));

    List<Map<String, String>> peeps = new ArrayList<Map<String, String>>();
    peeps.add(Literals.$("name", "Moe").$("count", "15"));
    peeps.add(Literals.$("name", "Larry").$("count", "5"));
    peeps.add(Literals.$("name", "Curly").$("count", "1"));
    final Map<String, Object> context = new HashMap<String, Object>();
    context.put("peeps", peeps);

    final Template t = handlebars.compile(template);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        t.apply(context);
      }

      @Override
      public String toString() {
        return label(template);
      }
    });
  }

  private String label(final String template) {
    return "template.apply:\n" + template;
  }
}
