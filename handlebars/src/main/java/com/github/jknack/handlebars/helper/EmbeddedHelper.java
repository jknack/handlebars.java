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
package com.github.jknack.handlebars.helper;

import com.github.jknack.handlebars.io.TemplateSource;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Given:
 * home.hbs
 *
 * <pre>
 * &lt;html&gt;
 * ...
 * {{emdedded "user" ["id"]}}
 * &lt;/html&gt;
 * </pre>
 *
 * where user.hbs is:
 *
 * <pre>
 * &lt;tr&gt;
 * &lt;td&gt;{{firstName}}&lt;/td&gt;
 * &lt;td&gt;{{lastName}}&lt;/td&gt;
 * &lt;/tr&gt;
 * </pre>
 *
 * expected output is:
 *
 * <pre>
 * &lt;script id="user-hbs" type="text/x-handlebars-template"&gt;
 * &lt;tr&gt;
 * &lt;td&gt;{{firstName}}&lt;/td&gt;
 * &lt;td&gt;{{lastName}}&lt;/td&gt;
 * &lt;/tr&gt;
 * &lt;/script&gt;
 * </pre>
 *
 * Optionally, a user can set the template's name:
 *
 * <pre>
 * {{emdedded "user" "user-tmpl" }}
 * </pre>
 *
 * expected output is:
 *
 * <pre>
 * &lt;script id="user-tmpl" type="text/x-handlebars-template"&gt;
 * &lt;tr&gt;
 * &lt;td&gt;{{firstName}}&lt;/td&gt;
 * &lt;td&gt;{{lastName}}&lt;/td&gt;
 * &lt;/tr&gt;
 * &lt;/script&gt;
 * </pre>
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class EmbeddedHelper implements Helper<String> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<String> INSTANCE = new EmbeddedHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "embedded";

  @Override
  public Object apply(final String path, final Options options)
      throws IOException {
    notEmpty(path, "found '%s', expected 'partial's name'", path);
    String suffix = options.handlebars.getLoader().getSuffix();
    String defaultId = (path + suffix).replace('/', '-').replace('.', '-');
    String id = options.param(0, defaultId);
    TemplateSource source = options.handlebars.getLoader().sourceAt(path);
    StringBuilder script = new StringBuilder();
    script.append("<script id=\"").append(id)
        .append("\" type=\"text/x-handlebars\">\n");
    script.append(source.content(options.handlebars.getCharset())).append("\n");
    script.append("</script>");
    return new Handlebars.SafeString(script);
  }
}
