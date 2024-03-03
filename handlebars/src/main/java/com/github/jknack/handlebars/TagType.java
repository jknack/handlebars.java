/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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

  /** A var decorator tag, like: <code>{{*name}}</code>. */
  STAR_VAR,

  /**
   * All variables are HTML escaped by default. If you want to return unescaped HTML, use the triple
   * mustache: <code>{{{@literal &}name}}</code>.
   */
  AMP_VAR,

  /**
   * All variables are HTML escaped by default. If you want to return unescaped HTML, use the triple
   * mustache: <code>{{{name}}}</code>.
   */
  TRIPLE_VAR,

  /**
   * Same as {@link #VAR} but can be invoked from inside a helper: <code>{{helper (subexpression)}}
   * </code>.
   */
  SUB_EXPRESSION,

  /**
   * Sections render blocks of text one or more times, depending on the value of the key in the
   * current context.
   *
   * <p>A section begins with a pound and ends with a slash. That is, {{#person}} begins a "person"
   * section while {{/person}} ends it.
   */
  SECTION {
    @Override
    public boolean inline() {
      return false;
    }
  },

  /** Like {{#* decorator}}success{{/decorator}}. */
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
