package com.github.edgarespina.handlebars.io;

import static org.parboiled.common.Preconditions.checkArgument;
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
public class ServletContextTemplateLoader extends TemplateLoader<String> {

  /**
   * The base path. Required.
   */
  private final String basepath;

  /**
   * The servlet context. Required.
   */
  private final ServletContext servletContext;

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   * @param basepath The base path. Required.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext,
      final String basepath) {
    checkNotNull(servletContext, "The servlet context is required.");
    checkNotNull(basepath, "A base path is required.");
    checkArgument(basepath.length() > 0, "A base path is required.");
    this.servletContext = servletContext;
    this.basepath = basepath;
  }

  /**
   * Creates a new {@link ServletContextTemplateLoader}.
   *
   * @param servletContext The servlet context. Required.
   */
  public ServletContextTemplateLoader(final ServletContext servletContext) {
    this(servletContext, "/");
  }

  @Override
  protected String resolve(final String uri) {
    return basepath + uri;
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
