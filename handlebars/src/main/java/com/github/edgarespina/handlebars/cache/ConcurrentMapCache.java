package com.github.edgarespina.handlebars.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.edgarespina.handlebars.Template;
import com.github.edgarespina.handlebars.TemplateCache;

/**
 * A {@link TemplateCache} based on a {@link ConcurrentMap}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ConcurrentMapCache implements TemplateCache {

  /**
   * NULL object.
   */
  private static final Object NULL = new Object();

  /**
   * The object storage.
   */
  private final ConcurrentMap<Object, Template> store =
      new ConcurrentHashMap<Object, Template>();

  @Override
  public void clear() {
    store.clear();
  }

  @Override
  public void evict(final Object key) {
    store.remove(key);
  }

  @Override
  public Template get(final Object key) {
    Template value = this.store.get(key);
    return value == NULL ? null : value;
  }

  @Override
  public void put(final Object key, final Template template) {
    store.put(key, template);
  }

}
