package com.github.jknack.handlebars;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.URLTemplateLoader;

public class Issue95 {

  @Test
  public void issue95() throws IOException {
    URLTemplateLoader loader = new ClassPathTemplateLoader("/issue95");

    Handlebars handlebars = new Handlebars(loader);
    handlebars.setInfiniteLoops(true);
    Template template = handlebars.compile("hbs/start");
    assertNotNull(template);
  }
}
