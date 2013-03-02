package com.github.jknack.handlebars.bench;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MapTemplateLoader;

public class CompilerBenchTest {

  /**
   * Enables the benchmark rule.
   */
  @Rule
  public TestRule benchmarkRun = new BenchmarkRule();

  public static final int ROUNDS = 300;

  public static final int WARM_UP = 5;

  public static final Handlebars handlebars;

  static {
    final Map<String, String> templates = new HashMap<String, String>();
    templates.put("/variables.hbs",
        "Hello {{name}}! You have {{count}} new messages.");

    templates.put("/recursion.hbs",
        "{{name}}{{#each kids}}{{>recursion}}{{/each}}");

    handlebars = new Handlebars(new MapTemplateLoader(templates));
    handlebars.setAllowInifiteLoops(true);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void helloWorld() throws IOException {
    final String template = "Hello World!";
    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void variables() throws IOException {
    final String template = "Hello {{name}}! You have {{count}} new messages.";
    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void object() throws IOException {
    final String template = "{{#with person}}{{name}}{{age}}{{/with}}";
    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void array() throws IOException {
    final String template = "{{#each names}}{{name}}{{/each}}";
    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void complex() throws IOException {
    final String template =
        "<h1>{{header}}</h1>{{#if items}}<ul>{{#each items}}{{#if current}}" +
            "<li><strong>{{name}}</strong></li>{{^}}" +
            "<li><a href=\"{{url}}\">{{name}}</a></li>{{/if}}" +
            "{{/each}}</ul>{{^}}<p>The list is empty.</p>{{/if}}";

    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void recursion() throws IOException {
    final String template = "{{name}}{{#each kids}}{{>recursion}}{{/each}}";

    handlebars.compile(template);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void partial() throws IOException {
    final String template = "{{#each peeps}}{{>variables}}{{/each}}";

    handlebars.compile(template);
  }

}
