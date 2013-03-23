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
package com.github.jknack.handlebars;


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
   * Evict the mapping for this key from this cache if it is present.
   *
   * @param key the key whose mapping is to be removed from the cache
   */
  void evict(Object key);

  /**
   * Return the value to which this cache maps the specified key. Returns
   * <code>null</code> if the cache contains no mapping for this key.
   *
   * @param key key whose associated value is to be returned.
   * @return the value to which this cache maps the specified key,
   *         or <code>null</code> if the cache contains no mapping for this key
   */
  Template get(Object key);

  /**
   * Associate the specified value with the specified key in this cache.
   * <p>
   * If the cache previously contained a mapping for this key, the old value is
   * replaced by the specified value.
   *
   * @param key the key with which the specified value is to be associated
   * @param template the value to be associated with the specified key
   */
  void put(Object key, Template template);
}
