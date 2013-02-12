package com.github.jknack.handlebars.cache;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TemplateCache;

public class ConcurrentMapCacheTest {

  @Test
  public void putAndRemove() {
    TemplateCache cache = new ConcurrentMapCache();
    Template template = createMock(Template.class);
    String key = "key";
    cache.put(key, template);
    assertEquals(template, cache.get(key));
    cache.evict(key);
    assertNull(cache.get(key));
  }

  @Test
  public void clear() {
    TemplateCache cache = new ConcurrentMapCache();
    Template template = createMock(Template.class);
    String key = "key";
    cache.put(key, template);
    assertEquals(template, cache.get(key));
    cache.clear();
    assertNull(cache.get(key));
  }
}
