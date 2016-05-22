package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class v4Test {

  @SuppressWarnings("serial")
  public static class Hash extends LinkedHashMap<String, Object> {

    public Hash $(final String name, final Object value) {
      put(name, value);
      return this;
    }
  }

  public void shouldCompileTo(final String template, final Hash data, final String expected) throws IOException {
    Template t = compile(template, data);
    Object hash = data.get("hash");
    String result = t.apply(configureContext(hash));
    assertEquals("'" + expected + "' should === '" + result + "': ", expected, result);
  }

  protected Object configureContext(final Object context) {
    return context;
  }

  public void text(final String template, final Hash data, final String expected) throws IOException {
    assertEquals(expected, compile(template, data).text());
  }

  public Template compile(final String template) throws IOException {
    return compile(template, new Hash());
  }

  public Template compile(final String template, final Hash data) throws IOException {
    MapTemplateLoader loader = new MapTemplateLoader();
    Hash partials = (Hash) data.get("partials");
    if (partials != null) {
      for (Entry<String, Object> entry : partials.entrySet()) {
        loader.define(entry.getKey(), (String) entry.getValue());
      }
    }
    Handlebars handlebars = newHandlebars().with(loader);
    configure(handlebars);

    Hash helpers = (Hash) data.get("helpers");
    if (helpers != null) {
      for (Entry<String, Object> entry : helpers.entrySet()) {
        final Object value = entry.getValue();
        final Helper<?> helper;
        if (!(value instanceof Helper)) {
          helper = new Helper<Object>() {
            @Override
            public Object apply(final Object context, final Options options)
                throws IOException {
              return value.toString();
            }
          };
        } else {
          helper = (Helper<?>) value;
        }
        handlebars.registerHelper(entry.getKey(), helper);
      }
    }

    Hash decorators = (Hash) data.get("decorators");
    if (decorators != null) {
      for (Entry<String, Object> entry : decorators.entrySet()) {
        final Object value = entry.getValue();
        handlebars.registerDecorator(entry.getKey(), (Decorator) value);
      }
    }

    Template t = handlebars.compileInline(template);
    return t;
  }

  protected void configure(final Handlebars handlebars) {
  }

  protected Handlebars newHandlebars() {
    return new Handlebars();
  }

  public static final Hash $ = $();

  public static Hash $(final Object... attributes) {
    Hash model = new Hash();
    for (int i = 0; i < attributes.length; i += 2) {
      model.$((String) attributes[i], attributes[i + 1]);
    }
    return model;
  }
}
