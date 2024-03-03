/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import com.github.jknack.handlebars.io.URLTemplateLoader;

/**
 * A template loader for a Spring application.
 *
 * <ul>
 *   <li>Must support fully qualified URLs, e.g. "file:C:/page.html".
 *   <li>Must support classpath pseudo-URLs, e.g. "classpath:page.html".
 *   <li>Should support relative file paths, e.g. "WEB-INF/page.html".
 * </ul>
 *
 * @author edgar.espina
 * @since 0.4.1
 * @see ResourceLoader#getResource(String)
 */
public class SpringTemplateLoader extends URLTemplateLoader {

  /** The Spring {@link ResourceLoader}. */
  private ResourceLoader loader;

  /**
   * Creates a new {@link SpringTemplateLoader}.
   *
   * @param loader The resource loader. Required.
   */
  public SpringTemplateLoader(final ResourceLoader loader) {
    this.loader = requireNonNull(loader, "A resource loader is required.");
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
  protected URL getResource(final String location) throws IOException {
    Resource resource = loader.getResource(location);
    if (!resource.exists()) {
      return null;
    }
    return resource.getURL();
  }

  @Override
  public String resolve(final String location) {
    String protocol = null;
    if (location.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
      protocol = ResourceUtils.CLASSPATH_URL_PREFIX;
    } else if (location.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
      protocol = ResourceUtils.FILE_URL_PREFIX;
    }
    if (protocol == null) {
      return super.resolve(location);
    }
    return protocol + super.resolve(location.substring(protocol.length()));
  }
}
