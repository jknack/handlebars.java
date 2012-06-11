package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import com.github.edgarespina.handlerbars.io.ClasspathResourceLocator;
import com.github.edgarespina.handlerbars.parser.Parser;

public class Handlebars {

  public static final String DELIM_START = "{{";

  public static final String DELIM_END = "}}";

  private ResourceLocator resourceLocator;

  static {
    Parser.initialize();
  }

  public Handlebars(final ResourceLocator resourceLocator) {
    this.resourceLocator =
        notNull(resourceLocator, "The resource locator is required.");
  }

  public Handlebars() {
    this(new ClasspathResourceLocator());
  }

  public Template compile(final URI uri) throws IOException {
    return compile(uri, DELIM_START, DELIM_END);
  }

  public Template compile(final URI uri, final String delimStart,
      final String delimEnd) throws IOException {
    Reader reader = resourceLocator.locate(uri);
    return Parser.create(this, null, delimStart, delimEnd).parse(reader);
  }

  public Template compile(final String input) throws IOException {
    return compile(input, DELIM_START, DELIM_END);
  }

  public Template compile(final String input, final String delimStart,
      final String delimEnd) throws IOException {
    return Parser.create(this, null, delimStart, delimEnd).parse(input);
  }

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  public static String safeString(final String value) {
    return escapeHtml4(value);
  }

}
