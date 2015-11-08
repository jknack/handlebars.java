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
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * <p>
 * Strategy interface for loading resources (i.e class path or file system resources)
 * </p>
 * <h3>Templates prefix and suffix</h3>
 * <p>
 * A <code>TemplateLoader</code> provides two important properties:
 * </p>
 * <ul>
 * <li>prefix: useful for setting a default prefix where templates are stored.</li>
 * <li>suffix: useful for setting a default suffix or file extension for your templates. Default is:
 * <code>'.hbs'</code></li>
 * </ul>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 * TemplateLoader loader = new ClassPathTemplateLoader();
 * loader.setPrefix("/templates");
 * loader.setSuffix(".html");
 * Handlebars handlebars = new Handlebars(loader);
 *
 * Template template = handlebars.compile("mytemplate");
 *
 * System.out.println(template.apply("Handlebars.java"));
 * </pre>
 *
 * <p>
 * The template loader resolve <code>mytemplate</code> to <code>/templates/mytemplate.html</code>
 * and load it.
 * </p>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class URLTemplateLoader extends AbstractTemplateLoader {

  @Override
  public TemplateSource sourceAt(final String uri) throws IOException {
    notEmpty(uri, "The uri is required.");
    String location = resolve(normalize(uri));
    URL resource = getResource(location);
    if (resource == null) {
      throw new FileNotFoundException(location);
    }
    return new URLTemplateSource(location, resource);
  }

  /**
   * Get a template resource for the given location.
   *
   * @param location The location of the template source. Required.
   * @return A new template resource.
   * @throws IOException If the url can't be resolved.
   */
  protected abstract URL getResource(String location) throws IOException;

}
