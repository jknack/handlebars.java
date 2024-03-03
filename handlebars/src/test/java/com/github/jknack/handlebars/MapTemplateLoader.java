/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateLoader;

/**
 * Template loader for testing.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class MapTemplateLoader extends URLTemplateLoader {

  private Map<String, String> map;

  public MapTemplateLoader(final Map<String, String> map) {
    this.map = map;
  }

  public MapTemplateLoader() {
    this(new HashMap<String, String>());
  }

  public MapTemplateLoader define(final String name, final String content) {
    map.put(getPrefix() + name + getSuffix(), content);
    return this;
  }

  @Override
  public TemplateSource sourceAt(final String uri) throws FileNotFoundException {
    String location = resolve(normalize(uri));
    String text = map.get(location);
    if (text == null) {
      throw new FileNotFoundException(location);
    }
    return new StringTemplateSource(location, text);
  }

  @Override
  protected URL getResource(final String location) {
    throw new UnsupportedOperationException();
  }
}
