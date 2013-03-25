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
package com.github.jknack.handlebars.cache;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * A simple {@link TemplateCache} built on top of {@link ConcurrentHashMap}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ConcurrentMapTemplateCache implements TemplateCache {

  /**
   * The logging system.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * The map cache.
   */
  private final Map<TemplateSource, Pair<TemplateSource, Template>> cache =
      new ConcurrentHashMap<TemplateSource, Pair<TemplateSource, Template>>();

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public void evict(final TemplateSource source) {
    cache.remove(source);
  }

  @Override
  public Template get(final TemplateSource source, final Parser parser) throws IOException {
    notNull(source, "The source is required.");
    notNull(parser, "The parser is required.");

    Pair<TemplateSource, Template> entry = cache.get(source);
    if (entry == null) {
      logger.debug("Loading: {}", source);
      entry = Pair.of(source, parser.parse(source));
      cache.put(source, entry);
    } else if (source.lastModified() != entry.getKey().lastModified()) {
      // remove current entry.
      evict(source);
      logger.info("Reloading: {}", source);
      entry = Pair.of(source, parser.parse(source));
      cache.put(source, entry);
    } else {
      logger.debug("Found in cache: {}", source);
    }
    return entry.getValue();
  }

}
