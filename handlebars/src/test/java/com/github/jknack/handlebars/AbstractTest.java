package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

public class AbstractTest {

  @SuppressWarnings("serial")
  public static class Hash extends LinkedHashMap<String, Object> {

    public Hash $(final String name, final Object value) {
      put(name, value);
      return this;
    }
  }

  public void shouldCompileTo(final String template, final String data,
      final String expected) throws IOException {
    shouldCompileTo(template, data, expected, "");
  }

  public void shouldCompileTo(final String template, final Object data,
      final String expected) throws IOException {
    shouldCompileTo(template, data, expected, "");
  }

  public void shouldCompileTo(final String template, final String context,
      final String expected, final String message) throws IOException {
    Object deserializedContext = context;
    if (deserializedContext != null) {
      deserializedContext = new Yaml().load(context);
    }
    shouldCompileTo(template, deserializedContext, expected, message);
  }

  public void shouldCompileTo(final String template, final Object context,
      final String expected, final String message) throws IOException {
    shouldCompileTo(template, context, new Hash(), expected, message);
  }

  public void shouldCompileTo(final String template, final Object context,
      final Hash helpers, final String expected) throws IOException {
    shouldCompileTo(template, context, helpers, expected, "");
  }

  public void shouldCompileTo(final String template, final String context,
      final Hash helpers, final String expected) throws IOException {
    shouldCompileTo(template, new Yaml().load(context), helpers, expected, "");
  }

  public void shouldCompileTo(final String template, final String context,
      final Hash helpers, final String expected, final String message) throws IOException {
    shouldCompileTo(template, new Yaml().load(context), helpers, expected, message);
  }

  public void shouldCompileTo(final String template, final Object context,
      final Hash helpers, final String expected, final String message) throws IOException {
    shouldCompileTo(template, context, helpers, new Hash(), expected, message);
  }

  public void shouldCompileToWithPartials(final String template, final Object context,
      final Hash partials, final String expected) throws IOException {
    shouldCompileTo(template, context, new Hash(), partials, expected, "");
  }

  public void shouldCompileToWithPartials(final String template, final Object context,
      final Hash partials, final String expected, final String message) throws IOException {
    shouldCompileTo(template, context, new Hash(), partials, expected, message);
  }

  public void shouldCompileTo(final String template, final Object context,
      final Hash helpers, final Hash partials, final String expected, final String message)
      throws IOException {
    Template t = compile(template, helpers, partials);
    String result = t.apply(configureContext(context));
    assertEquals("'" + expected + "' should === '" + result + "': " + message, expected, result);
  }

  protected Object configureContext(final Object context) {
    return context;
  }

  public Template compile(final String template) throws IOException {
    return compile(template, new Hash());
  }

  public Template compile(final String template, final Hash helpers)
      throws IOException {
    return compile(template, helpers, new Hash(), false);
  }

  public Template compile(final String template, final Hash helpers, final boolean stringParams)
      throws IOException {
    return compile(template, helpers, new Hash(), stringParams);
  }

  public Template compile(final String template, final Hash helpers, final Hash partials)
      throws IOException {
    return compile(template, helpers, partials, false);
  }

  public Template compile(final String template, final Hash helpers, final Hash partials,
      final boolean stringParams)
      throws IOException {
    MapTemplateLoader loader = new MapTemplateLoader();
    for (Entry<String, Object> entry : partials.entrySet()) {
      loader.define(entry.getKey(), (String) entry.getValue());
    }
    Handlebars handlebars = newHandlebars().with(loader);
    configure(handlebars);
    handlebars.setStringParams(stringParams);

    for (Entry<String, Object> entry : helpers.entrySet()) {
      final Object value = entry.getValue();
      final Helper<?> helper;
      if (!(value instanceof Helper)) {
        helper = new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return value.toString();
          }
        };
      } else {
        helper = (Helper<?>) value;
      }
      handlebars.registerHelper(entry.getKey(), helper);
    }
    Template t = handlebars.compileInline(template);
    return t;
  }

  protected void configure(final Handlebars handlebars) {
  }

  protected Handlebars newHandlebars() {
    return new Handlebars();
  }

  public static final Object $ = new Object();

  public static Hash $(final Object... attributes) {
    Hash model = new Hash();
    for (int i = 0; i < attributes.length; i += 2) {
      model.$((String) attributes[i], attributes[i + 1]);
    }
    return model;
  }
}
