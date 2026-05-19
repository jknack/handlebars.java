/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.LookupTranslator;

/**
 * A strategy for determining how to escape a variable (<code>{{variable}}</code>)..
 *
 * <p>Usage:
 *
 * <pre>
 * EscapingStrategy escapingStrategy = new EscapingStrategy() {
 * 	public String escape(final CharSequence value) {
 * 		// return the character sequence escaped however you want
 * 	}
 * };
 * Handlebars handlebars = new Handlebars().with(escapingStrategy);
 * </pre>
 *
 * @author Tristan Burch
 * @since 1.2.0
 */
public interface EscapingStrategy {

  /**
   * Handlebars escaping strategy. Escape is done via a string map. See {@link
   * EscapingStrategy#HTML_ENTITY}.
   *
   * @author edgar
   * @since 4.0.4
   */
  class Hbs implements EscapingStrategy {

    /** EMPTY. */
    private static final String EMPTY = "";

    /** Translator. */
    private final LookupTranslator translator;

    /**
     * Creates a new {@link Hbs} escaping strategy.
     *
     * @param escapeMap A escape map.
     */
    public Hbs(final String[][] escapeMap) {
      this(escapeMap(escapeMap));
    }

    /**
     * Creates a new {@link Hbs} escaping strategy.
     *
     * @param escapeMap A escape map.
     */
    public Hbs(final Map<CharSequence, CharSequence> escapeMap) {
      translator = new LookupTranslator(escapeMap);
    }

    @Override
    public CharSequence escape(final CharSequence value) {
      if (value instanceof Handlebars.SafeString) {
        return ((Handlebars.SafeString) value).content;
      }
      return value == null || value.length() == 0 ? EMPTY : translator.translate(value);
    }

    /**
     * Convert a table to a hash (internal usage).
     *
     * @param table Table.
     * @return A hash.
     */
    private static Map<CharSequence, CharSequence> escapeMap(final String[][] table) {
      Map<CharSequence, CharSequence> result = new HashMap<>();
      for (String[] row : table) {
        result.put(row[0], row[1]);
      }
      return result;
    }
  }

  /** The default HTML Entity escaping strategy. */
  EscapingStrategy HTML_ENTITY =
      new Hbs(
          new String[][] {
            {"<", "&lt;"},
            {">", "&gt;"},
            {"\"", "&quot;"},
            {"'", "&#x27;"},
            {"`", "&#x60;"},
            {"&", "&amp;"},
            {"=", "&#x3D;"}
          });

  /** Like {@link #HTML_ENTITY} but ignores <code>=</code>. */
  EscapingStrategy HBS3 =
      new Hbs(
          new String[][] {
            {"<", "&lt;"},
            {">", "&gt;"},
            {"\"", "&quot;"},
            {"'", "&#x27;"},
            {"`", "&#x60;"},
            {"&", "&amp;"}
          });

  /** Default escaping strategy for Handlebars 4.x . */
  EscapingStrategy HBS4 = HTML_ENTITY;

  /** Escape variable for CSV. */
  EscapingStrategy CSV =
      value -> value == null ? null : StringEscapeUtils.escapeCsv(value.toString());

  /** Escape variable for XML. */
  EscapingStrategy XML =
      value -> value == null ? null : StringEscapeUtils.escapeXml11(value.toString());

  /** Escape variable for JavaScript. */
  EscapingStrategy JS =
      value -> value == null ? null : StringEscapeUtils.escapeEcmaScript(value.toString());

  /** Escape variable for JSON. */
  EscapingStrategy JSON = value ->
    value == null ? null : StringEscapeUtils.escapeJson(value.toString());

  /** NOOP escaping. */
  EscapingStrategy NOOP = value -> value;

  /** Default escaping strategy. */
  EscapingStrategy DEF = HBS4;

  /**
   * Escape the {@link java.lang.CharSequence}.
   *
   * @param value the character sequence to be escaped.
   * @return the escaped character sequence.
   */
  CharSequence escape(CharSequence value);
}
