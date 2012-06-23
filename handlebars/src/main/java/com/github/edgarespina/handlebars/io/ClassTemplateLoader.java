package com.github.edgarespina.handlebars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

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
public class ClassTemplateLoader extends TemplateLoader<String> {

  /**
   * The base path. Required.
   */
  private String basepath;

  /**
   * Creates a new {@link ClassTemplateLoader}.
   *
   * @param basepath The base path. Required.
   */
  public ClassTemplateLoader(final String basepath) {
    checkNotNull(basepath, "A base path is required.");
    checkArgument(basepath.length() > 0, "A base path is required.");
    this.basepath = basepath;
    if (!this.basepath.startsWith("/")) {
      this.basepath = "/" + this.basepath;
    }
    if (!this.basepath.endsWith("/")) {
      this.basepath += "/";
    }
  }

  /**
   * Creates a new {@link ClassTemplateLoader}. It looks for templates
   * stored in the root of the classpath.
   */
  public ClassTemplateLoader() {
    this("/");
  }

  @Override
  protected String resolve(final String uri) {
    return basepath + uri;
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
