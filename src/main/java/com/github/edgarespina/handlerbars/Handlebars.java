package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;

import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.edgarespina.handlerbars.io.ClasspathResourceLocator;
import com.github.edgarespina.handlerbars.parser.Parser;

public class Handlebars {

  private static final Logger logger = LoggerFactory
      .getLogger(Handlebars.class);

  private ResourceLocator resourceLocator;

  public Handlebars(final ResourceLocator resourceLocator) {
    this.resourceLocator =
        notNull(resourceLocator, "The resource locator is required.");
    initialize();
  }

  public Handlebars() {
    this(new ClasspathResourceLocator());
  }

  public Template compile(final String uri) throws IOException,
      HandlebarsException {
    return compile(resourceLocator.locate(uri));
  }

  private Template compile(final Reader reader) throws IOException {
    long start = System.currentTimeMillis();
    try {
      Parser parser = newParser();
      ParseRunner<Template> runner =
          new ReportingParseRunner<Template>(parser.template());
      ParsingResult<Template> result = runner.run(toString(reader));
      if (result.hasErrors()) {
        throw new HandlebarsException(ErrorUtils.printParseErrors(result));
      }
      return result.resultValue;
    } finally {
      long end = System.currentTimeMillis();
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          logger.trace("Cannot close the input reader", ex);
        }
      }
      logger.trace("Compilation took: {}ms", end - start);
    }
  }

  private void initialize() {
    newParser();
  }

  private Parser newParser() {
    return Parboiled.createParser(Parser.class);
  }

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  public static String safeString(final String value) {
    return escapeHtml4(value);
  }

  public static String toString(final Reader reader)
      throws IOException {
    StringBuilder buffer = new StringBuilder(1024 * 4);
    int ch;
    while ((ch = reader.read()) != -1) {
      buffer.append((char) ch);
    }
    buffer.trimToSize();
    return buffer.toString();
  }
}
