package com.github.jknack.handlebars.i287;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class Issue287 {

  @Test
  public void compositeLoaderMustNotFailWithInlineTemplates() throws IOException {
    Handlebars handlebars = new Handlebars(new CompositeTemplateLoader(new FileTemplateLoader("."),
        new ClassPathTemplateLoader()));

    handlebars.compileInline("{{issue287}}");
  }
}
