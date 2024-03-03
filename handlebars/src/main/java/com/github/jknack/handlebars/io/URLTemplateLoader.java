/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Strategy interface for loading resources (i.e class path or file system resources)
 *
 * <h3>Templates prefix and suffix</h3>
 *
 * <p>A <code>TemplateLoader</code> provides two important properties:
 *
 * <ul>
 *   <li>prefix: useful for setting a default prefix where templates are stored.
 *   <li>suffix: useful for setting a default suffix or file extension for your templates. Default
 *       is: <code>'.hbs'</code>
 * </ul>
 *
 * <p>Usage:
 *
 * <pre>
 * TemplateLoader loader = new ClassPathTemplateLoader();
 * loader.setPrefix("/templates");
 * loader.setSuffix(".html");
 * Handlebars handlebars = new Handlebars(loader);
 *
 * Template template = handlebars.compile("mytemplate");
 *
 * System.out.println(template.apply("Handlebars.java"));
 * </pre>
 *
 * <p>The template loader resolve <code>mytemplate</code> to <code>/templates/mytemplate.html</code>
 * and load it.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class URLTemplateLoader extends AbstractTemplateLoader {

  @Override
  public TemplateSource sourceAt(final String uri) throws IOException {
    notEmpty(uri, "The uri is required.");
    String location = resolve(normalize(uri));
    URL resource = getResource(location);
    if (resource == null) {
      throw new FileNotFoundException(location);
    }
    return new URLTemplateSource(location, resource);
  }

  /**
   * Get a template resource for the given location.
   *
   * @param location The location of the template source. Required.
   * @return A new template resource.
   * @throws IOException If the url can't be resolved.
   */
  protected abstract URL getResource(String location) throws IOException;
}
