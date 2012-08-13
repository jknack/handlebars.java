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
package specs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.TemplateLoader;

public class SpecResourceLocator extends TemplateLoader {
  private Map<String, String> templates;

  public SpecResourceLocator(final Spec spec) {
    templates = spec.partials();
    if (templates == null) {
      templates = new HashMap<String, String>();
    }
    templates.put("template", spec.template());
  }

  @Override
  public String resolve(final String uri) {
    return uri;
  }

  @Override
  protected Reader read(final String uri) throws IOException {
    String template = templates.get(uri);
    return template == null ? null : new StringReader(template);
  }

}
