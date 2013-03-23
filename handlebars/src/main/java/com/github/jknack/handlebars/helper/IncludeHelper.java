/**
 * Copyright (c) 2012-2013 Edgar Espina
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
package com.github.jknack.handlebars.helper;

import java.io.IOException;
import java.net.URI;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Allows to include partials with custom context.
 * This is a port of https://github.com/wycats/handlebars.js/pull/368
 */
public class IncludeHelper implements Helper<String> {
  /**
   * A singleton instance of this helper.
   */
  public static final Helper<String> INSTANCE = new IncludeHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "include";

  @Override
  public CharSequence apply(final String partial, final Options options) throws IOException {
    // merge all the hashes into the context
    options.context.data(options.hash);
    Template template = options.handlebars.compile(URI.create(partial));
    return new Handlebars.SafeString(template.apply(options.context));
  }

}
