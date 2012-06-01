package specs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.ParsingException;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Scopes;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.parser.HandlebarsLexer;
import com.github.edgarespina.handlerbars.parser.HandlebarsParser;

public abstract class SpecTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @SuppressWarnings("unchecked")
  @Test
  public void runAll() throws ParsingException, IOException {
    Yaml yaml = new Yaml();

    Map<String, Object> data =
        (Map<String, Object>) yaml.load(getClass().getResourceAsStream(
            "/specs/" + specName() + ".yml"));
    List<Map<String, Object>> tests =
        (List<Map<String, Object>>) data.get("tests");
    int number = 1;
    for (Map<String, Object> test : tests) {
      if (enabled(number, (String) test.get("name"))) {
        test.put("number", number);
        runOne(test);
      }
      number++;
    }
  }

  protected boolean enabled(final int number, final String name) {
    return true;
  }

  @SuppressWarnings("unchecked")
  private void runOne(final Map<String, Object> test) throws ParsingException,
      IOException {
    logger.info("*************************************************");
    logger.info("* {}. {}: {}",
        new Object[] {test.get("number"), test.get("name"), test.get("desc") });
    final String input = (String) test.get("template");
    final String expected = (String) test.get("expected");
    Map<String, Object> data = (Map<String, Object>) test.get("data");
    logger.info("INPUT:");
    logger.info(input);
    logger.info("DATA:");
    logger.info(data.toString());
    long startCompile = System.currentTimeMillis();
    Template template =
        new Handlebars(resourceLocator(test)).compile("template.html");
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
      logger.info("OUTPUT:");
      logger.info(output);
    } catch (ComparisonFailure ex) {
      logger.error("   Found: '{}'", ex.getActual());
      logger.error("Expected: '{}'", ex.getExpected());
      throw ex;
    } finally {
      logger.info("TOTAL    : {}ms", total);
      if (total > 0) {
        logger.info("  ({}%)compile: {}ms", compile * 100 / total, compile);
        logger.info("  ({}%)merge  : {}ms", merge * 100 / total, merge);
      }
      logger.info("*************************************************");
    }
  }

  protected ResourceLocator resourceLocator(final Map<String, Object> test) {
    return new ResourceLocator() {

      @Override
      protected Reader read(final String uri) throws IOException {
        return new StringReader((String) test.get("template"));
      }
    };
  }

  public abstract String specName();

  @Before
  public void initJUnit() throws IOException, RecognitionException {
    // Init junit classloader. This reduce the time reported during execution.
    new Handlebars();
    new HandlebarsLexer();
    new HandlebarsParser(null);
  }
}
