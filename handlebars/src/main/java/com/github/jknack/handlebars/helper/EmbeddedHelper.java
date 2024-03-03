/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Given: home.hbs
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

  /** A singleton instance of this helper. */
  public static final Helper<String> INSTANCE = new EmbeddedHelper();

  /** The helper's name. */
  public static final String NAME = "embedded";

  @Override
  public Object apply(final String path, final Options options) throws IOException {
    notEmpty(path, "found '%s', expected 'partial's name'", path);
    String suffix = options.handlebars.getLoader().getSuffix();
    String defaultId = (path + suffix).replace('/', '-').replace('.', '-');
    String id = options.param(0, defaultId);
    TemplateSource source = options.handlebars.getLoader().sourceAt(path);
    StringBuilder script = new StringBuilder();
    script.append("<script id=\"").append(id).append("\" type=\"text/x-handlebars\">\n");
    script.append(source.content(options.handlebars.getCharset())).append("\n");
    script.append("</script>");
    return new Handlebars.SafeString(script);
  }
}
