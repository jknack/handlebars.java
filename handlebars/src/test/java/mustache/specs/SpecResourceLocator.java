/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateLoader;

public class SpecResourceLocator extends URLTemplateLoader {
  private Map<String, String> templates;

  public SpecResourceLocator(final Spec spec) {
    templates = spec.partials();
    if (templates == null) {
      templates = new HashMap<String, String>();
    }
    templates.put("template", spec.template());
  }

  @Override
  public TemplateSource sourceAt(final String uri) throws IOException {
    notNull(uri, "The uri is required.");
    notEmpty(uri.toString(), "The uri is required.");
    String location = resolve(normalize(uri));
    String text = templates.get(uri.toString());
    if (text == null) {
      throw new FileNotFoundException(location);
    }
    return new StringTemplateSource(location, text);
  }

  @Override
  protected URL getResource(final String location) throws IOException {
    throw new UnsupportedOperationException();
  }
}
