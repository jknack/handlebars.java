package com.github.edgarespina.handlebars.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.parser.HandlebarsLexer;
import com.github.edgarespina.handlerbars.parser.HandlebarsParser;

public abstract class TemplateTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Before
  public void initJUnit() throws IOException, RecognitionException {
    // Init junit classloader. This reduce the time reported during execution.
    new Handlebars();
    new HandlebarsLexer();
    new HandlebarsParser(null);
  }

  @Test
  public void compile() throws IOException, RecognitionException {
    logger.info("*************************************************");
    logger.info("* {}:", getClass().getSimpleName().replace("Test", ""));
    logger.info("*************************************************");
    final String input = input();
    logger.info("INPUT:");
    logger.info(input);
    long startCompile = System.currentTimeMillis();
    Template template = new Handlebars(resourceLocator()).compile("template.html");
    long endCompile = System.currentTimeMillis();
    long startMerge = System.currentTimeMillis();
    String output = template.merge(scope());
    long endMerge = System.currentTimeMillis();
    long total = endMerge - startCompile;
    long compile = endCompile - startCompile;
    long merge = endMerge - startMerge;
    logger.info("OUTPUT:");
    logger.info(output);
    logger.info("TOTAL    : {}ms", total);
    if (total > 0) {
      logger.info("  ({}%)compile: {}ms", compile * 100 / total, compile);
      logger.info("  ({}%)merge  : {}ms", merge * 100 / total, merge);
    }
    try {
      assertEquals(output(), output);
    } catch (ComparisonFailure ex) {
      logger.error("Expected: '{}'", ex.getExpected());
      logger.error("   found: '{}'", ex.getActual());
      throw ex;
    } finally {
      logger.info("*************************************************");
    }
  }

  public ResourceLocator resourceLocator() {
    return new ResourceLocator() {

      @Override
      protected Reader read(final String uri) throws IOException {
        return new StringReader(input());
      }
    };
  }

  public abstract Map<String, Object> scope();

  public abstract String input();

  public abstract String output();
}
