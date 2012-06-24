package com.github.edgarespina.handlebars.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.edgarespina.handlebars.TemplateLoader;

/**
 * Load templates from the class-path. A base path can be specified at creation
 * time. By default all the templates are loaded from '/' (a.k.a. root
 * classpath).
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ClassTemplateLoader extends TemplateLoader {

  /**
   * Creates a new {@link ClassTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   * @param suffix The view suffix. Required.
   */
  public ClassTemplateLoader(final String prefix, final String suffix) {
    setPrefix(prefix);
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link ClassTemplateLoader}.
   *
   * @param prefix The view prefix. Required.
   */
  public ClassTemplateLoader(final String prefix) {
    this(prefix, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link ClassTemplateLoader}. It looks for templates
   * stored in the root of the classpath.
   */
  public ClassTemplateLoader() {
    this("/");
  }

  @Override
  protected Reader read(final String location) throws IOException {
    InputStream input = getClass().getResourceAsStream(location);
    if (input == null) {
      return null;
    }
    return new InputStreamReader(input);
  }

}
