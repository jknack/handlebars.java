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
    // 1. Logical Bounds Check (Parity with d177cdee)
    // Spring locations often contain protocols (classpath:). We must strip it to normalize the
    // path.
    String pathPart = location;
    int protocolIndex = location.indexOf(":");
    if (protocolIndex != -1) {
      pathPart = location.substring(protocolIndex + 1);
    }

    String resolvedPath =
        java.nio.file.Paths.get(pathPart)
            .normalize()
            .toString()
            .replace(java.io.File.separatorChar, '/');
    if (pathPart.startsWith("/") && !resolvedPath.startsWith("/")) {
      resolvedPath = "/" + resolvedPath;
    }

    // Extract the raw path from the configured prefix
    String prefixPath = getPrefix();
    int prefixProtocolIndex = prefixPath.indexOf(":");
    if (prefixProtocolIndex != -1) {
      prefixPath = prefixPath.substring(prefixProtocolIndex + 1);
    }

    // Enforce the boundary
    if (!prefixPath.equals("/") && !resolvedPath.startsWith(prefixPath)) {
      throw new IllegalArgumentException(
          "Path traversal attempt detected. Resolved path escapes Spring base prefix: " + location);
    }

    // 2. Delegate to Spring
    Resource resource = loader.getResource(location);
    if (!resource.exists()) {
      return null;
    }

    // 3. Post-resolution URL Component Validation (Fragment/Query injection)
    URL url = resource.getURL();
    validateNoUnsafeUrlComponents(url);

    return url;
  }

  /**
   * Verifies that the resolved URL does not contain components that can bypass suffix validation.
   *
   * @param url The resolved URL.
   */
  private void validateNoUnsafeUrlComponents(final URL url) {
    if (url.getRef() != null) {
      throw new IllegalArgumentException("Template URL must not contain a fragment: " + url);
    }
    if (url.getQuery() != null) {
      throw new IllegalArgumentException("Template URL must not contain a query: " + url);
    }
  }
}
