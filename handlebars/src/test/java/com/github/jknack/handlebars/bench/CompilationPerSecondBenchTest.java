package com.github.jknack.handlebars.bench;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MapTemplateLoader;
import com.github.jknack.handlebars.bench.Bench.Unit;

public class CompilationPerSecondBenchTest {

  @Before
  public void setup() {
    Assume.assumeTrue(Boolean.valueOf(System.getProperty("run.bench")));
  }

  @Test
  public void helloWorld() throws IOException {
    final String template = "Hello World!";
    final Handlebars handlebars = new Handlebars();

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  @Test
  public void variables() throws IOException {
    final String template = "Hello {{name}}! You have {{count}} new messages.";
    final Handlebars handlebars = new Handlebars();

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  @Test
  public void object() throws IOException {
    final String template = "{{#with person}}{{name}}{{age}}{{/with}}";
    final Handlebars handlebars = new Handlebars();

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  @Test
  public void array() throws IOException {
    final String template = "{{#each names}}{{name}}{{/each}}";
    final Handlebars handlebars = new Handlebars();
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
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

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  @Test
  public void recursion() throws IOException {
    final String template = "{{name}}{{#each kids}}{{>recursion}}{{/each}}";
    final Map<String, String> templates = new HashMap<String, String>();
    templates.put("/recursion.hbs",
        "{{name}}{{#each kids}}{{>recursion}}{{/each}}");
    final Handlebars handlebars =
        new Handlebars(new MapTemplateLoader(templates));
    handlebars.setInfiniteLoops(true);

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  @Test
  public void partial() throws IOException {
    final String template = "{{#each peeps}}{{>variables}}{{/each}}";
    final Map<String, String> templates = new HashMap<String, String>();
    templates.put("/variables.hbs",
        "Hello {{name}}! You have {{count}} new messages.");
    final Handlebars handlebars = new Handlebars(new MapTemplateLoader(templates));

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        handlebars.compileInline(template);
      }

      @Override
      public String toString() {
        return compilerLabel(template);
      }
    });
  }

  private String compilerLabel(final String template) {
    return "handlebars.compile:\n" + template;
  }
}