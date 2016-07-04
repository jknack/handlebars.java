package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.v4Test;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class Hbs507 extends v4Test {

  @Test
  public void shouldCallPartialWithoutSideEffect() throws IOException {
    noSideEffect(NullTemplateCache.INSTANCE);
    noSideEffect(new HighConcurrencyTemplateCache());
  }

  private void noSideEffect(final TemplateCache tcache) throws IOException {
    Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/hbs507"))
        .with(tcache)
        .parentScopeResolution(false);
    handlebars.registerHelper("inline", new Helper<Object>() {
      // I suppose I could use the TemplateCache here instead
      private Map<String, Template> cache = new ConcurrentHashMap<>();

      @Override
      public CharSequence apply(final Object partial, final Options options)
          throws IOException {
        if (partial == null) {
          throw new IllegalArgumentException("must provide a partial name as a parameter");
        }

        String path = partial.toString();
        options.context.data(options.hash);

        if (!cache.containsKey(path)) {
          Template template = options.handlebars.compile(path);
          cache.put(path, template);
        }

        Template template = cache.get(path);
        return new Handlebars.SafeString(template.apply(options.context));
      }
    });

    Hash h1 = $("metadata",
        $("aggregateId", "ID:x0", "aggregateType", "brett", "businessKey", "favre"));

    Hash h2 = $("metadata",
        $("aggregateId", "ID:y0", "aggregateType", "brett", "businessKey", "favre"));

    Template template = handlebars.compile("a");
    assertEquals("This is a test for: \"ID:x0\"\n" +
        "\n" +
        "\"metadata.aggregateId\" : \"ID:x0\" \n" +
        "\"metadata.businessKey\" : \"favre\"\n" +
        "\n" +
        "End test", template.apply(h1));

    assertEquals("This is a test for: \"ID:y0\"\n" +
        "\n" +
        "\"metadata.aggregateId\" : \"ID:y0\" \n" +
        "\"metadata.businessKey\" : \"favre\"\n" +
        "\n" +
        "End test", template.apply(h2));
  }

}
