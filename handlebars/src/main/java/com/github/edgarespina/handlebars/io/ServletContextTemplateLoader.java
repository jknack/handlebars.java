package com.github.edgarespina.handlebars.io;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletContext;

import com.github.edgarespina.handlebars.TemplateLoader;

/**
 * Load templates from the {@link ServletContext}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ServletContextTemplateLoader extends TemplateLoader {

  /**
   * The servlet context. Required.
   */
  private final ServletContext servletContext;

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   * @param prefix The prefix that gets prepended to view names when building a
   *        URI.
   * @param suffix The suffix that gets appended to view names when building a
   *        URI. Required.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext,
      final String prefix, final String suffix) {
    setPrefix(prefix);
    setSuffix(suffix);
    this.servletContext =
        checkNotNull(servletContext, "The servlet context is required.");
  }

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   * @param prefix The prefix that gets prepended to view names when building a
   *        URI.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext,
      final String prefix) {
    this(servletContext, prefix, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext) {
    this(servletContext, "/", DEFAULT_SUFFIX);
  }

  @Override
  protected Reader read(final String path) throws IOException {
    InputStream input = servletContext.getResourceAsStream(path);
    if (input == null) {
      return null;
    }
    return new InputStreamReader(input);
  }

}
