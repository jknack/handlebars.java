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

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.custom.Blog;
import com.github.edgarespina.handlerbars.custom.Comment;

@RunWith(SpecRunner.class)
public abstract class SpecTest {

  private static class Report {

    public void header(final int size) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < size; i++) {
        buffer.append("*");
      }
      System.out.println(buffer);
    }

    public void append(final Object message) {
      System.out.println(message == null ? "" : message.toString());
    }

    public void append(final Object message, final Object... arguments) {
      System.out.println(String.format(
          message == null ? "" : message.toString(), arguments));
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
    String output = template.apply(data);
    long endMerge = System.currentTimeMillis();
    long total = endMerge - startCompile;
    long compile = endCompile - startCompile;
    long merge = endMerge - startMerge;
    try {
      assertEquals(expected, output);
      report.append("OUTPUT:");
      report.append(output);
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
