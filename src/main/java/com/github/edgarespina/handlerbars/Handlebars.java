package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;

import org.parboiled.Parboiled;

import com.github.edgarespina.handlerbars.io.ClasspathResourceLocator;
import com.github.edgarespina.handlerbars.parser.Parser;

public class Handlebars {

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
    Reader reader = resourceLocator.locate(uri);
    return newParser().parse(reader);
  }

  private void initialize() {
    newParser();
  }

  private Parser newParser() {
    return Parboiled.createParser(Parser.class, this, null);
  }

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  public static String safeString(final String value) {
    return escapeHtml4(value);
  }

}
