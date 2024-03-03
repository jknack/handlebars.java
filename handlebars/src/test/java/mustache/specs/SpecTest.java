/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.abort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.custom.Blog;
import com.github.jknack.handlebars.custom.Comment;

public abstract class SpecTest {

  private static class Report {

    public void header(final int size) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < size; i++) {
        buffer.append("*");
      }
      Handlebars.log(buffer.toString());
    }

    public void append(final Object message) {
      Handlebars.log(message == null ? "" : message.toString());
    }

    public void append(final Object message, final Object... arguments) {
      Handlebars.log(message == null ? "" : message.toString(), arguments);
    }
  }

  private static long start;

  private static long count;

  @BeforeAll
  public static void onStart() {
    start = System.currentTimeMillis();
  }

  @AfterAll
  public static void onFinish() {
    long end = System.currentTimeMillis();
    System.out.printf("Number of executions: %s\n", count);
    System.out.printf("Total Time: %sms\n", end - start);
  }

  public void runSpec(Spec spec) throws HandlebarsException, IOException {
    if (!skip(spec)) {
      run(alter(spec));
    } else {
      Report report = new Report();
      report.header(80);
      report.append("Skipping Test: %s", spec.id());
      report.header(80);
      abort(spec.name());
    }
  }

  public static List<Spec> data(final String filename) throws IOException {
    return data(SpecTest.class, filename);
  }

  public static String path(final Class<?> loader) {
    return "/" + loader.getPackage().getName().replace(".", "/") + "/";
  }

  @SuppressWarnings("unchecked")
  public static List<Spec> data(final Class<?> loader, final String filename) throws IOException {
    Constructor constructor = new Constructor(new LoaderOptions());
    constructor.addTypeDescription(new TypeDescription(Blog.class, "!blog"));
    constructor.addTypeDescription(new TypeDescription(Comment.class, "!comment"));
    constructor.addTypeDescription(new TypeDescription(Map.class, "!code"));

    Yaml yaml = new Yaml(constructor);

    String location = path(loader) + filename;
    String input = FileUtils.readFileToString(new File("src/test/resources", location));
    Map<String, Object> data = (Map<String, Object>) yaml.load(input);
    List<Map<String, Object>> tests = (List<Map<String, Object>>) data.get("tests");
    int number = 0;
    List<Spec> dataset = new ArrayList<>();
    for (Map<String, Object> test : tests) {
      test.put("number", number++);
      dataset.add(new Spec(test));
    }
    return dataset;
  }

  protected boolean skip(final Spec spec) {
    return false;
  }

  protected Spec alter(final Spec spec) {
    return spec;
  }

  private void run(final Spec spec) throws IOException {
    count++;
    Report report = new Report();
    report.header(80);
    report.append("* %s", spec.description());
    final String input = spec.template();
    final String expected = spec.expected();
    Object data = spec.data();
    report.append("DATA:");
    report.append(data.toString());
    report.append("PARTIALS:");
    report.append(spec.partials());
    report.append("INPUT:");
    report.append(input);
    report.append("EXPECTED:");
    report.append(expected);
    long startCompile = System.currentTimeMillis();
    Handlebars handlebars = new Handlebars(new SpecResourceLocator(spec));
    handlebars.setPrettyPrint(true);
    configure(handlebars);
    Template template = handlebars.compile("template");
    long endCompile = System.currentTimeMillis();
    long startMerge = System.currentTimeMillis();
    CharSequence output = template.apply(data);
    long endMerge = System.currentTimeMillis();
    long total = endMerge - startCompile;
    long compile = endCompile - startCompile;
    long merge = endMerge - startMerge;
    try {
      assertEquals(expected, output);
      report.append("OUTPUT:");
      report.append(output);
    } catch (HandlebarsException ex) {
      Handlebars.error(ex.getMessage());
    } catch (Exception ex) {
      report.append("FOUND:");
      report.append(ex.getMessage());
      throw ex;
    } finally {
      report.append("TOTAL    : %sms", total);
      if (total > 0) {
        report.append("  (%s%%)compile: %sms", compile * 100 / total, compile);
        report.append("  (%s%%)merge  : %sms", merge * 100 / total, merge);
      }
      report.header(80);
    }
  }

  protected HelperRegistry configure(final Handlebars handlebars) {
    return handlebars;
  }
}
