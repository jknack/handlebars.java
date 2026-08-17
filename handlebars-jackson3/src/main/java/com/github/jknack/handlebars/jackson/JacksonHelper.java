/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import tools.jackson.core.SerializableString;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * A Jackson 3.x helper.
 *
 * <p>Basic usage:
 *
 * <pre>
 *  Handlebars hbs = new Handlebars();
 *
 *  hbs.registerHelper("json", JacksonHelper.INSTANCE);
 *
 *  ...
 *
 *  {{json model}}
 * </pre>
 *
 * <p>If <code>model</code> is null an empty string is returned.
 *
 * <p>You can change this using the <code>default</code> option:
 *
 * <pre>
 *  {{json model default="{}"}}
 * </pre>
 *
 * <p>Using a view class:
 *
 * <pre>
 *  {{json model view="foo.MyView"}}
 * </pre>
 *
 * <p>Using alias for views:
 *
 * <pre>
 *  {{json model view="myView"}}
 * </pre>
 *
 * <p>Escape HTML chars:
 *
 * <pre>
 *  {{json model escapeHTML=true}}
 * </pre>
 *
 * <p>Pretty printer:
 *
 * <pre>
 *  {{json model pretty=true}}
 * </pre>
 *
 * @author edgar.espina, jolarsen
 * @since 4.5.5
 */
public class JacksonHelper implements Helper<Object> {

  /**
   * Escape HTML chars from JSON content. See
   * http://www.cowtowncoder.com/blog/archives/2012/08/entry_476.html
   *
   * @author edgar.espina, jolarsen
   * @since 4.5.5
   */
  @SuppressWarnings("serial")
  private static class HtmlEscapes extends CharacterEscapes {

    /** The escape table. */
    private final int[] escapeTable;

    {
      // Start with set of characters known to require escaping (double-quote,
      // backslash etc)
      escapeTable = CharacterEscapes.standardAsciiEscapesForJSON();
      // and force escaping of a few others:
      escapeTable['<'] = CharacterEscapes.ESCAPE_STANDARD;
      escapeTable['>'] = CharacterEscapes.ESCAPE_STANDARD;
      escapeTable['&'] = CharacterEscapes.ESCAPE_STANDARD;
      escapeTable['\''] = CharacterEscapes.ESCAPE_STANDARD;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
      return escapeTable;
    }

    @Override
    public SerializableString getEscapeSequence(final int ch) {
      return null;
    }
  }

  /** A singleton version of {@link JacksonHelper}. */
  public static final Helper<Object> INSTANCE = new JacksonHelper();

  /** The JSON parser. */
  private final ObjectMapper mapper;

  /** Class alias registry. */
  private final Map<String, Class<?>> alias = new HashMap<String, Class<?>>();

  /**
   * Creates a new {@link JacksonHelper}.
   *
   * @param objectMapper The object's mapper. Required.
   */
  public JacksonHelper(final ObjectMapper objectMapper) {
    mapper = requireNonNull(objectMapper, "The object mapper is required.");
  }

  /** Creates a new {@link JacksonHelper}. */
  private JacksonHelper() {
    this(JsonMapper.builder().build());
  }

  @Override
  public Object apply(final Object context, final Options options) {
    if (context == null) {
      return options.hash("default", "");
    }

    final String viewName = options.hash("view", "");
    try {
      final Class<?> viewClass = alias.get(viewName);
      final ObjectWriter viewWriter = Handlebars.Utils.isEmpty(viewName)
              ? mapper.writer()
              : mapper.writerWithView(viewClass != null
                                      ? viewClass
                                      : getClass().getClassLoader().loadClass(viewName));

      final boolean escapeHtml = options.hash("escapeHTML", false);
      final boolean pretty = options.hash("pretty", false);

      final ObjectWriter writer = pretty
              ? viewWriter.withDefaultPrettyPrinter()
              : viewWriter;

      final ObjectWriter configuredWriter = escapeHtml
              ? writer.with(new HtmlEscapes())
              : writer;

      return new Handlebars.SafeString(configuredWriter.writeValueAsString(context));
    } catch (ClassNotFoundException|NoClassDefFoundError ex) {
      throw new IllegalArgumentException(viewName, ex);
    }
  }

  /**
   * Add an alias for the given view class.
   *
   * @param alias The view alias. Required.
   * @param viewClass The view class. Required.
   * @return This helper.
   */
  public JacksonHelper viewAlias(final String alias, final Class<?> viewClass) {
    this.alias.put(
        requireNonNull(alias, "A view alias is required."),
        requireNonNull(viewClass, "A view class is required."));
    return this;
  }
}
