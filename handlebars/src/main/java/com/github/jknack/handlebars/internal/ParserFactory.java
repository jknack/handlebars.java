/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.util.LinkedList;
import java.util.Map;

import org.parboiled.Parboiled;

import com.github.jknack.handlebars.Handlebars;

/**
 * A simple facade for parser creation. This class was introduced in order to
 * remove dynamic creation of parboiled classes. In restricted environments like
 * Google App Engine, Parboiled isn't able to create a dynamic classes.
 * The hack consist in creating Parboiled classes at build time and replace this
 * class from final jar with one that if found in the resources directory and it
 * is added at build-time.
 *
 * @author edgar.espina
 * @since 0.4.1
 */
public class ParserFactory {

  /**
   * Creates a new {@link Parser}.
   *
   * @param handlebars The parser owner.
   * @param filename The file's name.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return A new {@link Parser}.
   */
  public static Parser create(final Handlebars handlebars,
      final String filename,
      final String startDelimiter,
      final String endDelimiter) {
    return create(handlebars, filename, null,
        startDelimiter, endDelimiter, new LinkedList<Stacktrace>());
  }

  /**
   * Creates a new {@link Parser}.
   *
   * @param handlebars The parser owner.
   * @param filename The file's name.
   * @param partials The partials.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @param stacktrace The stack-trace.
   * @return A new {@link Parser}.
   */
  public static Parser create(final Handlebars handlebars,
      final String filename, final Map<String, Partial> partials,
      final String startDelimiter,
      final String endDelimiter,
      final LinkedList<Stacktrace> stacktrace) {
    return Parboiled.createParser(Parser.class, handlebars, filename, partials,
        startDelimiter, endDelimiter, stacktrace);
  }
}
