package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.edgarespina.handlerbars.io.ClasspathResourceLocator;
import com.github.edgarespina.handlerbars.parser.HandlebarsLexer;
import com.github.edgarespina.handlerbars.parser.HandlebarsParser;

public class Handlebars {

  private static final Logger logger = LoggerFactory
      .getLogger(Handlebars.class);

  private ResourceLocator resourceLocator;

  public Handlebars(final ResourceLocator resourceLocator) {
    this.resourceLocator =
        notNull(resourceLocator, "The resource locator is required.");
  }

  public Handlebars() {
    this(new ClasspathResourceLocator());
  }

  public Template compile(final String uri) throws IOException,
      ParsingException {
    return compile(resourceLocator.locate(uri));
  }

  private Template compile(final Reader reader) throws IOException {
    long start = System.currentTimeMillis();
    try {
      HandlebarsLexer lexer =
          new HandlebarsLexer(new ANTLRReaderStream(reader));
      TokenStream tokens = new CommonTokenStream(lexer);
      HandlebarsParser parser = new HandlebarsParser(tokens);
      return parser.compile(this);
    } catch (RecognitionException ex) {
      throw new ParsingException(ex);
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

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  public static String safeString(final String value) {
    return escapeHtml4(value);
  }
}
