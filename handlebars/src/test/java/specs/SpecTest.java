/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package specs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.custom.Blog;
import com.github.jknack.handlebars.custom.Comment;

@RunWith(SpecRunner.class)
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

  private Spec spec;

  public SpecTest(final Spec spec) {
    this.spec = spec;
  }

  @Test
  public void run() throws HandlebarsException, IOException {
    if (!skip(spec)) {
      run(alter(spec));
    } else {
      Report report = new Report();
      report.header(80);
      report.append("Skipping Test: %s", spec.id());
      report.header(80);
      throw new SkipTestException(spec.name());
    }
  }

  public static Collection<Object[]> data(final String filename) {
    return data(SpecTest.class, filename);
  }

  public static String path(final Class<?> loader) {
    return "/" + loader.getPackage().getName().replace(".", "/") + "/";
  }

  @SuppressWarnings("unchecked")
  public static Collection<Object[]> data(final Class<?> loader,
      final String filename) {
    Constructor constructor = new Constructor();
    constructor.addTypeDescription(new TypeDescription(Blog.class, "!blog"));
    constructor.addTypeDescription(new TypeDescription(Comment.class,
        "!comment"));

    Yaml yaml = new Yaml(constructor);

    String location = path(loader) + filename;
    Map<String, Object> data =
        (Map<String, Object>) yaml.load(
            SpecTest.class.getResourceAsStream(location));
    List<Map<String, Object>> tests =
        (List<Map<String, Object>>) data.get("tests");
    int number = 0;
    Collection<Object[]> dataset = new ArrayList<Object[]>();
    for (Map<String, Object> test : tests) {
      test.put("number", number++);
      dataset.add(new Object[] {new Spec(test) });
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
    Template template =
        configure(new Handlebars(new SpecResourceLocator(spec))).compile(URI
            .create("template"));
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
    } catch(HandlebarsException ex) {
      Handlebars.error(ex.getMessage());
    } catch (ComparisonFailure ex) {
      report.append("FOUND:");
      report.append(ex.getActual());
      throw ex;
    } finally {
      report.append("TOTAL    : %sms", total);
      if (total > 0) {
        report.append("  (%s%%)compile: %sms", compile * 100 / total,
            compile);
        report.append("  (%s%%)merge  : %sms", merge * 100 / total, merge);
      }
      report.header(80);
    }
  }

  protected Handlebars configure(final Handlebars handlebars) {
    return handlebars;
  }

  @Before
  public void initJUnit() throws IOException {
    // Init junit classloader. This reduce the time reported during execution.
    new Handlebars();
  }
}
