package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;

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
    handlebars.registerHelper("@json", JSONHelper.INSTANCE);

    Template template = handlebars.compile("{{@json this}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}",
        result);
  }

  @Test
  public void toJSONViewInclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    handlebars.registerHelper("@json", JSONHelper.INSTANCE);

    Template template =
        handlebars
            .compile("{{@json this view=\"com.github.edgarespina.handlebars.Blog$Views$Public\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}",
        result);
  }

  @Test
  public void toJSONViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json", new JSONHelper(mapper));

    Template template =
        handlebars
            .compile("{{@json this view=\"com.github.edgarespina.handlebars.Blog$Views$Public\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\"}", result);
  }
}
