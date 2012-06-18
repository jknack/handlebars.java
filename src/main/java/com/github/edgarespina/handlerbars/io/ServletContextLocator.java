package com.github.edgarespina.handlerbars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import javax.servlet.ServletContext;

import com.github.edgarespina.handlerbars.ResourceLocator;

/**
 * A resource locator that let you load files from a servlet context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ServletContextLocator extends ResourceLocator {

  /**
   * The base path. Required.
   */
  private final String basepath;

  /**
   * The servlet context. Required.
   */
  private final ServletContext servletContext;

  /**
   * Creates a new {@link ServletContextLocator}.
   *
   * @param servletContext The servlet context. Required.
   * @param basepath The base path. Required.
   */
  public ServletContextLocator(final ServletContext servletContext,
      final String basepath) {
    checkNotNull(servletContext, "The servlet context is required.");
    checkNotNull(basepath, "A base path is required.");
    checkArgument(basepath.length() > 0, "A base path is required.");
    this.servletContext = servletContext;
    this.basepath = basepath;
  }

  /**
   * Creates a new {@link ServletContextLocator}.
   *
   * @param servletContext The servlet context. Required.
   */
  public ServletContextLocator(final ServletContext servletContext) {
    this(servletContext, "/");
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    String path = basepath + uri;
    InputStream input = servletContext.getResourceAsStream(path);
    if (input == null) {
      throw new IOException("Not found: " + path);
    }
    return new InputStreamReader(input);
  }

}
