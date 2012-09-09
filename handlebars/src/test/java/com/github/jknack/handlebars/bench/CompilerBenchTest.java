package com.github.jknack.handlebars.bench;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.bench.Bench.Unit;

public class CompilerBenchTest {

  @Test
  public void helloWorld() throws IOException {
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile("Hello World!");
      }

      @Override
      public String toString() {
        return "compile hello world";
      }
    });
  }

  @Test
  public void varibales() throws IOException {
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile("Hello {{name}}! You have {{count}} new messages.");
      }

      @Override
      public String toString() {
        return "compile variables";
      }
    });
  }

  @Test
  public void object() throws IOException {
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile("{{#with person}}{{name}}{{age}}{{/with}}");
      }

      @Override
      public String toString() {
        return "compile object";
      }
    });
  }

  @Test
  public void array() throws IOException {
    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile("{{#each names}}{{name}}{{/each}}");
      }

      @Override
      public String toString() {
        return "compile array";
      }
    });
  }

  @Test
  public void complex() throws IOException {
    final String complex =
        "<h1>{{header}}</h1>{{#if items}}<ul>{{#each items}}{{#if current}}" +
            "<li><strong>{{name}}</strong></li>{{^}}" +
            "<li><a href=\"{{url}}\">{{name}}</a></li>{{/if}}" +
            "{{/each}}</ul>{{^}}<p>The list is empty.</p>{{/if}}";

    new Bench().run(new Unit() {

      @Override
      public void run() throws IOException {
        Handlebars handlebars = new Handlebars();
        handlebars.compile(complex);
      }

      @Override
      public String toString() {
        return "compile complex";
      }
    });
  }
}
