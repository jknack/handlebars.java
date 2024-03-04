/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.guava;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.base.Stopwatch;

public class GuavaCachedTemplateLoaderTest {

  @Test
  public void testCacheWithExpiration() throws Exception {
    TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"));
    TemplateLoader cachedLoader =
        GuavaCachedTemplateLoader.cacheWithExpiration(loader, 200, TimeUnit.MILLISECONDS);

    assertNotNull(loader.sourceAt("template"));
    assertNotNull(cachedLoader.sourceAt("template"));

    final int TOTAL = 1000;

    Stopwatch sw = Stopwatch.createStarted();
    for (int i = 0; i < TOTAL; i++) {
      loader.sourceAt("template");
    }
    sw.stop();
    out.println("Loader took: " + sw);
    sw.reset().start();
    for (int i = 0; i < TOTAL; i++) {
      cachedLoader.sourceAt("template");
    }
    out.println("Cached took: " + sw);
  }

  @Test
  public void testCacheWithExpirationAgain() throws Exception {
    // lazy mans JVM warming
    testCacheWithExpiration();
  }
}
