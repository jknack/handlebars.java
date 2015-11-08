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
package com.github.jknack.handlebars.cache;

import java.io.IOException;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * The template cache system.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public interface TemplateCache {

  /**
   * Remove all mappings from the cache.
   */
  void clear();

  /**
   * Evict the mapping for this source from this cache if it is present.
   *
   * @param source the source whose mapping is to be removed from the cache
   */
  void evict(TemplateSource source);

  /**
   * Return the value to which this cache maps the specified key.
   *
   * @param source source whose associated template is to be returned.
   * @param parser The Handlebars parser.
   * @return A template.
   * @throws IOException If input can't be parsed.
   */
  Template get(TemplateSource source, Parser parser) throws IOException;

  /**
   * Turn on/off auto reloading of templates. Auto reload is done using
   * {@link TemplateSource#lastModified()}.
   *
   * @param reload True, for turning off template reload. Default is: false.
   * @return This template loader.
   */
  TemplateCache setReload(boolean reload);
}
