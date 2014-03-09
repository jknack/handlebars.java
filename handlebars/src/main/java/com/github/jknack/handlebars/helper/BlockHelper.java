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

import static org.apache.commons.lang3.Validate.isTrue;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * <p>
 * The block helper will replace its section with the partial of the same name if it exists.
 * </p>
 * <p>
 * If <code>delete-after-merge</code> is set to <code>true</code>, the partial will be delete once
 * applied it.
 * </p>
 *
 * <pre>
 *  {{block "title" [delete-after-merge=false]}}
 * </pre>
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
        template = options.handlebars.compile(path);
      } catch (IOException ex) {
        // assume partial not found
        Handlebars.debug(ex.getMessage());
        template = options.fn;
      }
    }
    CharSequence result = options.apply(template);
    Boolean deletePartials = options.hash("delete-after-merge",
        options.handlebars.deletePartialAfterMerge());
    if (deletePartials) {
      // once applied, remove the template from current execution.
      options.partial(path, null);
    }
    return result;
  }
}
