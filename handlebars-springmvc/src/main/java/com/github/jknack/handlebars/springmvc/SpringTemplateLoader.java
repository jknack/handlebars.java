/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

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
    Resource resource = loader.getResource(location);
    if (!resource.exists()) {
      return null;
    }

    URL url = resource.getURL();
    validateNoUnsafeUrlComponents(url);

    // Delegate to native canonical files if the Spring resource is on disk
    if (resource.isFile()) {
      File file = resource.getFile().getCanonicalFile();
      String prefixPath = getPrefix();
      int protocolIndex = prefixPath.indexOf(":");
      if (protocolIndex != -1) {
        prefixPath = prefixPath.substring(protocolIndex + 1);
      }

      File basedir = new File(prefixPath).getCanonicalFile();
      String canonicalFilePath = file.getPath();
      String canonicalBasePath = basedir.getPath();

      if (!canonicalBasePath.endsWith(File.separator)) {
        canonicalBasePath += File.separator;
      }

      if (!canonicalFilePath.startsWith(canonicalBasePath)) {
        throw new IllegalArgumentException(
            "Path traversal attempt detected. Resolved path escapes Spring base directory: "
                + location);
      }
    } else {
      // Fallback containment for Classpath/URL resources
      String decodedPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
      String resolvedPath =
          Paths.get(decodedPath).normalize().toString().replace(File.separatorChar, '/');

      String prefixPath = getPrefix();
      int protocolIndex = prefixPath.indexOf(":");
      if (protocolIndex != -1) {
        prefixPath = prefixPath.substring(protocolIndex + 1);
      }

      String normalizedPrefix =
          Paths.get(prefixPath).normalize().toString().replace(File.separatorChar, '/');
      if (!normalizedPrefix.endsWith("/")) {
        normalizedPrefix += "/";
      }

      if (!normalizedPrefix.equals("/") && !resolvedPath.startsWith(normalizedPrefix)) {
        throw new IllegalArgumentException(
            "Path traversal attempt detected. Resolved path escapes Spring base prefix: "
                + location);
      }
    }

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
