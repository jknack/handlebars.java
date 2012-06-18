package com.github.edgarespina.handlerbars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import com.github.edgarespina.handlerbars.ResourceLocator;

/**
 * A resource locator that let you load files from a classpath. A base path can
 * be specified at creation time. By default all the templates are loaded from
 * '/' (a.k.a. root classpath).
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ClasspathResourceLocator extends ResourceLocator {

  /**
   * The base path. Required.
   */
  private String basepath;

  /**
   * Creates a new {@link ClasspathResourceLocator}.
   *
   * @param basepath The base path. Required.
   */
  public ClasspathResourceLocator(final String basepath) {
    checkNotNull(basepath, "A base path is required.");
    checkArgument(basepath.length() > 0, "A base path is required.");
    this.basepath = basepath;
    if (!this.basepath.endsWith("/")) {
      this.basepath += "/";
    }
  }

  /**
   * Creates a new {@link ClasspathResourceLocator}. It looks for templates
   * stored in the root of the classpath.
   */
  public ClasspathResourceLocator() {
    this("/");
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    String path = basepath + uri;
    InputStream input = getClass().getResourceAsStream(path);
    if (input == null) {
      return null;
    }
    return new InputStreamReader(input);
  }

}
