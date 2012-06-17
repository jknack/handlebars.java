package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.edgarespina.handlerbars.internal.Parser;
import com.github.edgarespina.handlerbars.io.ClasspathResourceLocator;

public class Handlebars {

  public static final String DELIM_START = "{{";

  public static final String DELIM_END = "}}";

  private final ResourceLocator resourceLocator;

  private final Map<String, Helper<Object>> helpers =
      new HashMap<String, Helper<Object>>();

  static {
    Parser.initialize();
  }

  public Handlebars(final ResourceLocator resourceLocator) {
    this.resourceLocator =
        notNull(resourceLocator, "The resource locator is required.");
    registerHelpers(BuiltInHelpers.registry());
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
    return Parser.create(this, delimStart, delimEnd).parse(reader);
  }

  public Template compile(final String input) throws IOException {
    return compile(input, DELIM_START, DELIM_END);
  }

  public Template compile(final String input, final String delimStart,
      final String delimEnd) throws IOException {
    return Parser.create(this, delimStart, delimEnd).parse(input);
  }

  @SuppressWarnings("unchecked")
  public <T> Helper<T> helper(final String name) {
    return (Helper<T>) helpers.get(name);
  }

  @SuppressWarnings("unchecked")
  public <T> Handlebars registerHelper(final String name,
      final Helper<T> helper) {
    helpers.put(name, (Helper<Object>) helper);
    return this;
  }

  public Handlebars registerHelpers(final HelperRegistry registry) {
    registry.register(this);
    return this;
  }

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  public static String html(final String value) {
    return escapeHtml4(value);
  }

  public static SafeString safeString(final String value) {
    return new SafeString(value);
  }
}
