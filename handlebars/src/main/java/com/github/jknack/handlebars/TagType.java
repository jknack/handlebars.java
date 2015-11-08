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

/**
 * Tags are indicated by the double mustaches.
 *
 * @author edgar.espina
 * @since 0.12.0
 */
public enum TagType {
  /**
   * The most basic tag type is the variable. A <code>{{name}}</code> tag in a basic template will
   * try to find the name key in the current context. If there is no name key, nothing will be
   * rendered.
   */
  VAR,

  /**
   * A var decorator tag, like: <code>{{*name}}</code>.
   */
  STAR_VAR,

  /**
   * All variables are HTML escaped by default. If you want to return unescaped HTML, use the
   * triple mustache: <code>{{{@literal &}name}}</code>.
   */
  AMP_VAR,

  /**
   * All variables are HTML escaped by default. If you want to return unescaped HTML, use the
   * triple mustache: <code>{{{name}}}</code>.
   */
  TRIPLE_VAR,

  /**
   * Same as {@link #VAR} but can be invoked from inside a helper:
   * <code>{{helper (subexpression)}}</code>.
   */
  SUB_EXPRESSION,

  /**
   * <p>
   * Sections render blocks of text one or more times, depending on the value of the key in the
   * current context.
   * </p>
   *
   * <p>
   * A section begins with a pound and ends with a slash. That is, {{#person}} begins a "person"
   * section while {{/person}} ends it.
   * </p>
   */
  SECTION {
    @Override
    public boolean inline() {
      return false;
    }
  },

  /**
   * Like {{#* decorator}}success{{/decorator}}.
   */
  START_SECTION {
    @Override
    public boolean inline() {
      return false;
    }
  };

  /**
   * True for inline tags.
   *
   * @return True for inline tags.
   */
  public boolean inline() {
    return true;
  }
}
