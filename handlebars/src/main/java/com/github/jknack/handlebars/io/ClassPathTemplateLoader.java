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

import java.net.URL;


/**
 * Load templates from the class-path. A base path can be specified at creation
 * time. By default all the templates are loaded from '/' (a.k.a. root
 * classpath).
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ClassPathTemplateLoader extends URLTemplateLoader {

  /**
   * Creates a new {@link ClassPathTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   * @param suffix The view suffix. Required.
   */
  public ClassPathTemplateLoader(final String prefix, final String suffix) {
    setPrefix(prefix);
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link ClassPathTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   */
  public ClassPathTemplateLoader(final String prefix) {
    this(prefix, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ClassPathTemplateLoader}. It looks for templates
   * stored in the root of the classpath.
   */
  public ClassPathTemplateLoader() {
    this("/");
  }

  @Override
  protected URL getResource(final String location) {
    return  getClass().getResource(location);
  }
}
