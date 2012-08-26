/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import com.github.jknack.handlebars.TemplateLoader;

/**
 * A template loader for a Spring application.
 * <ul>
 * <li>Must support fully qualified URLs, e.g. "file:C:/page.html".
 * <li>Must support classpath pseudo-URLs, e.g. "classpath:page.html".
 * <li>Should support relative file paths, e.g. "WEB-INF/page.html".
 * </ul>
 *
 * @author edgar.espina
 * @since 0.4.1
 * @see ResourceLoader#getResource(String)
 */
public class SpringTemplateLoader extends TemplateLoader {

  /**
   * The Spring {@link ResourceLoader}.
   */
  private ResourceLoader loader;

  /**
   * Creates a new {@link SpringTemplateLoader}.
   *
   * @param loader The resource loader. Required.
   */
  public SpringTemplateLoader(final ResourceLoader loader) {
    this.loader = notNull(loader, "A resource loader is required.");
  }

  /**
   * Creates a new {@link SpringTemplateLoader}.
   *
   * @param applicationContext The application's context. Required.
   */
  public SpringTemplateLoader(final ApplicationContext applicationContext) {
    this((ResourceLoader) applicationContext);
  }

  @Override
  public String resolve(final String uri) {
    String protocol = null;
    if (uri.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
      protocol = ResourceUtils.CLASSPATH_URL_PREFIX;
    } else if (uri.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
      protocol = ResourceUtils.FILE_URL_PREFIX;
    }
    if (protocol == null) {
      return super.resolve(uri);
    }
    return protocol + super.resolve(uri.substring(protocol.length()));
  }

  @Override
  protected Reader read(final String location) throws IOException {
    Resource resource = loader.getResource(location);
    if (resource.exists()) {
      return new InputStreamReader(resource.getInputStream());
    }
    return null;
  }

}
