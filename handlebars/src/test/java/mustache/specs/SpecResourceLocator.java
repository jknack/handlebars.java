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
