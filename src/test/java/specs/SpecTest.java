package specs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Scopes;
import com.github.edgarespina.handlerbars.Template;

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

  private Map<String, Object> data;

  public SpecTest(final Map<String, Object> data) {
    this.data = data;
  }

  @Test
  public void run() throws HandlebarsException, IOException {
    Integer number = (Integer) data.get("number");
    if (enabled(number, (String) data.get("name"))) {
      run(data);
    } else {
      Report report = new Report();
      report.header(80);
      report.append("Skipping Test: * %s. %s", number, data.get("name"));
      report.header(80);
      throw new SkipTestException((String) data.get("name"));
    }
  }

  @SuppressWarnings("unchecked")
  public static Collection<Object[]> data(final String filename) {
    Yaml yaml = new Yaml();

    Map<String, Object> data =
        (Map<String, Object>) yaml.load(SpecTest.class.getResourceAsStream(
            "/specs/" + filename));
    List<Map<String, Object>> tests =
        (List<Map<String, Object>>) data.get("tests");
    int number = 0;
    Collection<Object[]> dataset = new ArrayList<Object[]>();
    for (Map<String, Object> test : tests) {
      test.put("number", number++);
      dataset.add(new Object[] {test });
    }
    return dataset;
  }

  protected boolean enabled(final int number, final String name) {
    return true;
  }

  @SuppressWarnings("unchecked")
  private void run(final Map<String, Object> test)
      throws HandlebarsException,
      IOException {
    Report report = new Report();
    report.header(80);
    report.append("* %s. %s: %s", test.get("number"), test.get("name"),
        test.get("desc"));
    final String input = (String) test.get("template");
    final String expected = (String) test.get("expected");
    Map<String, Object> data = (Map<String, Object>) test.get("data");
    report.append("DATA:");
    report.append(data.toString());
    report.append("PARTIALS:");
    report.append(test.get("partials"));
    report.append("INPUT:");
    report.append(input);
    report.append("EXPECTED:");
    report.append(expected);
    long startCompile = System.currentTimeMillis();
    Template template =
        new Handlebars(new SpecResourceLocator(test)).compile("template");
    long endCompile = System.currentTimeMillis();
    long startMerge = System.currentTimeMillis();
    String output =
        template.merge(data == null ? Scopes.newScope() : Scopes.scope(data));
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

  @Before
  public void initJUnit() throws IOException {
    // Init junit classloader. This reduce the time reported during execution.
    new Handlebars();
  }
}
