/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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
