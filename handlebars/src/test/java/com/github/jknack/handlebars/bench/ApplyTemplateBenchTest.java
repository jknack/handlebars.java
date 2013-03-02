package com.github.jknack.handlebars.bench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.MapTemplateLoader;
import com.github.jknack.handlebars.Template;

public class ApplyTemplateBenchTest extends AbstractTest {

  /**
   * Enables the benchmark rule.
   */
  @Rule
  public TestRule benchmarkRun = new BenchmarkRule();

  public static final int ROUNDS = 3000;

  public static final int WARM_UP = 5;

  public static Handlebars handlebars;

  public static Template helloWorld;

  public static Template variables;

  private static Template object;

  private static Template array;

  private static Template complex;

  private static Template recursion;

  private static Template partial;

  static {
    try {
      final Map<String, String> templates = new HashMap<String, String>();
      templates.put("/variables.hbs",
          "Hello {{name}}! You have {{count}} new messages.");

      templates.put("/recursion.hbs",
          "{{name}}{{#each kids}}{{>recursion}}{{/each}}");

      handlebars = new Handlebars(new MapTemplateLoader(templates));
      handlebars.setAllowInifiteLoops(true);

      helloWorld = handlebars.compile("Hello World!");

      variables = handlebars.compile("Hello {{name}}! You have {{count}} new messages.");

      object = handlebars.compile("{{#with person}}{{name}}{{age}}{{/with}}");

      array = handlebars.compile("{{#each names}}{{name}}{{/each}}");

      complex = handlebars
          .compile("<h1>{{header}}</h1>{{#if items}}<ul>{{#each items}}{{#if current}}" +
              "<li><strong>{{name}}</strong></li>{{^}}" +
              "<li><a href=\"{{url}}\">{{name}}</a></li>{{/if}}" +
              "{{/each}}</ul>{{^}}<p>The list is empty.</p>{{/if}}");

      recursion = handlebars.compile("{{name}}{{#each kids}}{{>recursion}}{{/each}}");

      partial = handlebars.compile("{{#each peeps}}{{>variables}}{{/each}}");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void helloWorld() throws IOException {
    helloWorld.apply(null);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void variables() throws IOException {
    variables.apply($("name", "Mick", "count", 30));
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void object() throws IOException {
    object.apply($("person", $("name", "Larry", "age", 45)));
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void array() throws IOException {
    array.apply($("names", Arrays.asList(
        $("name", "Moe"),
        $("name", "Larry"),
        $("name", "Curly"),
        $("name", "Shemp"))
        ));
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void complex() throws IOException {
    complex.apply($("header",
        new Lambda<Object, String>() {
          @Override
          public String apply(final Object context, final Template template)
              throws IOException {
            return "Colors";
          }
        },
        "items", Arrays.asList(
            $("name", "red").$("current", "yes").$("url", "#Red"),
            $("name", "green").$("current", "no").$("url", "#Green"),
            $("name", "blue").$("current", "no").$("url", "#Blue")
            )));
  }

  @SuppressWarnings("unchecked")
  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void recursion() throws IOException {
    final Map<String, Object> kids111 = new HashMap<String, Object>();
    kids111.put("name", "1.1.1");
    kids111.put("kids", new ArrayList<Object>());

    final Map<String, Object> kids11 = new HashMap<String, Object>();
    kids11.put("name", "1.1");
    kids11.put("kids", Arrays.asList(kids111));

    final Map<String, Object> context = new HashMap<String, Object>();
    context.put("name", "1");
    context.put("kids", Arrays.asList(kids11));

    recursion.apply(context);
  }

  @Test
  @BenchmarkOptions(benchmarkRounds = ROUNDS, warmupRounds = WARM_UP)
  public void partial() throws IOException {
    List<Object> peeps = new ArrayList<Object>();
    peeps.add($("name", "Moe").$("count", "15"));
    peeps.add($("name", "Larry").$("count", "5"));
    peeps.add($("name", "Curly").$("count", "1"));
    final Map<String, Object> context = new HashMap<String, Object>();
    context.put("peeps", peeps);

    partial.apply(context);
  }

}
