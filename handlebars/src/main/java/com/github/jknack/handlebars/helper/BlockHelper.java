/**
 * Copyright (c) 2012 Edgar Espina
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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;
import java.net.URI;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.internal.AbstractOptions;

/**
 * The block helper will replace its section with the partial of the
 * same name if it exists.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class BlockHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new BlockHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "block";

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    isTrue(context instanceof String, "found '%s', expected 'partial's name'",
        context);

    String path = (String) context;
    Template template = options.partial(path);
    if (template == null) {
      try {
        template = options.getHandlebars().compile(URI.create(path));
        options.partial(path, template);
      } catch (IOException ex) {
        // partial not found
        Handlebars.debug(ex.getMessage());
        template = options.getFn();
      }
    }
    CharSequence result = options.apply(template);
    return result;
  }
}
