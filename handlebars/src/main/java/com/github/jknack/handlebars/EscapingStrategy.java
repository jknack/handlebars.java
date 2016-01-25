/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.LookupTranslator;

/**
 * <p>
 * A strategy for determining how to escape a variable (<code>{{variable}}</code>)..
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 *    EscapingStrategy escapingStrategy = new EscapingStrategy() {
 *       public String escape(final CharSequence value) {
 *         // return the character sequence escaped however you want
 *       }
 *    };
 *    Handlebars handlebars = new Handlebars().with(escapingStrategy);
 * </pre>
 *
 * @author Tristan Burch
 * @since 1.2.0
 */
public interface EscapingStrategy {

  /**
   * Handlebars escaping strategy. Escape is done via a string map. See
   * {@link EscapingStrategy#HTML_ENTITY}.
   *
   * @author edgar
   * @since 4.0.4
   */
  public class Hbs implements EscapingStrategy {

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
      translator = new LookupTranslator(escapeMap);
    }

    @Override
    public CharSequence escape(final CharSequence value) {
      if (value instanceof Handlebars.SafeString) {
        return ((Handlebars.SafeString) value).content;
      }
      return value == null || value.length() == 0 ? EMPTY : translator.translate(value);
    }

  }

  /**
   * The default HTML Entity escaping strategy.
   */
  EscapingStrategy HTML_ENTITY = new Hbs(new String[][]{
      {"<", "&lt;" },
      {">", "&gt;" },
      {"\"", "&quot;" },
      {"'", "&#x27;" },
      {"`", "&#x60;" },
      {"&", "&amp;" },
      {"=", "&#x3D;" }
  });

  /**
   * Like {@link #HTML_ENTITY} but ignores <code>=</code>.
   */
  EscapingStrategy HBS3 = new Hbs(new String[][]{
      {"<", "&lt;" },
      {">", "&gt;" },
      {"\"", "&quot;" },
      {"'", "&#x27;" },
      {"`", "&#x60;" },
      {"&", "&amp;" }
  });

  /** Default escaping strategy for Handlebars 4.x . */
  EscapingStrategy HBS4 = HTML_ENTITY;

  /** Escape variable for CSV. */
  EscapingStrategy CSV = new EscapingStrategy() {
    @Override
    public CharSequence escape(final CharSequence value) {
      return value == null ? null : StringEscapeUtils.escapeCsv(value.toString());
    }
  };

  /** Escape variable for XML. */
  EscapingStrategy XML = new EscapingStrategy() {
    @Override
    public CharSequence escape(final CharSequence value) {
      return value == null ? null : StringEscapeUtils.escapeXml(value.toString());
    }
  };

  /** Escape variable for JavaScript. */
  EscapingStrategy JS = new EscapingStrategy() {
    @Override
    public CharSequence escape(final CharSequence value) {
      return value == null ? null : StringEscapeUtils.escapeEcmaScript(value.toString());
    }
  };

  /** NOOP escaping. */
  EscapingStrategy NOOP = new EscapingStrategy() {
    @Override
    public CharSequence escape(final CharSequence value) {
      return value;
    }
  };

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
