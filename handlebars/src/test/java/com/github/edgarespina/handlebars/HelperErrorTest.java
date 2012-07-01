package com.github.edgarespina.handlebars;

import static com.github.edgarespina.handlebars.Literals.$;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.Test;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class HelperErrorTest {

  Map<String, String> source =
      $("/helper.hbs", "\n{{#block}} {{/block}}")
          .$("/embedded.hbs", "\n{{#embedded}} {{/embedded}}")
          .$("/basic.hbs", "\n{{basic}}")
          .$("/notfound.hbs", "\n{{notfound hash=x}}");

  @Test(expected = HandlebarsException.class)
  public void block() throws IOException {
    parse("helper");
  }

  @Test(expected = HandlebarsException.class)
  public void notfound() throws IOException {
    parse("notfound");
  }

  @Test(expected = HandlebarsException.class)
  public void basic() throws IOException {
    parse("basic");
  }

  @Test(expected = HandlebarsException.class)
  public void embedded() throws IOException {
    parse("embedded");
  }

  private Object parse(final String uri) throws IOException {
    try {
      Handlebars handlebars = new Handlebars(new MapTemplateLoader(source));
      handlebars.registerHelper("basic", new Helper<Object>() {
        @Override
        public CharSequence apply(final Object context, final Options options)
            throws IOException {
          throw new IllegalArgumentException("missing parameter: '0'.");
        }
      });
      Template compile = handlebars.compile(URI.create(uri));
      compile.apply(null);
      throw new IllegalStateException("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
