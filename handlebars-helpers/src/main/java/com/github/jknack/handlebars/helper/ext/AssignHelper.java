/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper.ext;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * You can use the assign helper to create auxiliary variables. Example:
 *
 * <pre>
 *  {{#assign "benefitsTitle"}} benefits.{{type}}.title {{/assign}}
 *  &lt;span class="benefit-title"&gt; {{i18n benefitsTitle}} &lt;/span&gt;
 * </pre>
 *
 * @author https://github.com/Jarlakxen
 */
public class AssignHelper implements Helper<String> {

  /** A singleton instance of this helper. */
  public static final Helper<String> INSTANCE = new AssignHelper();

  /** The helper's name. */
  public static final String NAME = "assign";

  @Override
  public Object apply(final String variableName, final Options options) throws IOException {
    CharSequence finalValue = options.apply(options.fn);
    options.context.data(variableName, finalValue.toString().trim());
    return null;
  }
}
