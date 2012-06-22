package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Template;

/**
 * Unit test for {@link JSONHelper}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class JSONHelperTest {

  @Test
  public void toJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", new JSONHelper());

    Template template = handlebars.compile("{{@json this}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}",
        result);
  }
}
