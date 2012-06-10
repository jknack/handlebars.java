package specs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Scopes;
import com.github.edgarespina.handlerbars.Template;

@RunWith(Parameterized.class)
public abstract class SpecTest {

  private static class Report {

    public void header(final int size) {
      StringBuilder buffer = new StringBuilder();
      for (int i = 0; i < size; i++) {
        buffer.append("*");
      }
      buffer.append("\n");
      System.out.println(buffer);
    }

    public void append(final String message) {
      System.out.println(message);
    }

    public void append(final String message, final Object... arguments) {
      System.out.println(String.format(message, arguments));
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
    report.append("INPUT:");
    report.append(input);
    report.append("EXPECTED:");
    report.append(expected);
    long startCompile = System.currentTimeMillis();
    Template template =
        new Handlebars(resourceLocator(test)).compile("template");
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

  protected ResourceLocator resourceLocator(final Map<String, Object> test) {
    return new ResourceLocator() {
      @Override
      protected Reader read(final String uri) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, String> templates =
            (Map<String, String>) test.get("partials");
        if (templates == null) {
          templates = new HashMap<String, String>();
        }
        templates.put("template", (String) test.get("template"));
        return new StringReader(templates.get(uri));
      }
    };
  }

  @Before
  public void initJUnit() throws IOException {
    // Init junit classloader. This reduce the time reported during execution.
    new Handlebars();
  }
}
