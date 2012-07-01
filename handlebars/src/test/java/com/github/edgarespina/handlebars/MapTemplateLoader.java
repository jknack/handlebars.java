package com.github.edgarespina.handlebars;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

/**
 * Template loader for testing.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class MapTemplateLoader extends TemplateLoader {

  private Map<String, String> map;

  public MapTemplateLoader(final Map<String, String> map) {
    this.map = map;
  }

  @Override
  protected Reader read(final String location) throws IOException {
    String text = map.get(location);
    return text == null ? null : new StringReader(text);
  }

}
