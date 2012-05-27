package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.edgarespina.handlerbars.parser.HandlebarsLexer;
import com.github.edgarespina.handlerbars.parser.HandlebarsParser;

public class Handlebars {

  private static final Logger logger = LoggerFactory
      .getLogger(Handlebars.class);

  public Handlebars() {
  }

  public Template compile(final String input) throws IOException,
      RecognitionException {
    return compile(new StringReader(input));
  }

  public Template compile(final Reader reader) throws IOException,
      RecognitionException {
    long start = System.currentTimeMillis();
    try {
      HandlebarsLexer lexer =
          new HandlebarsLexer(new ANTLRReaderStream(reader));
      TokenStream tokens = new CommonTokenStream(lexer);
      HandlebarsParser parser = new HandlebarsParser(tokens);
      return parser.compile();
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

  public static String safeString(final String value) {
    return escapeHtml4(value);
  }
}
