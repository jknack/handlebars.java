/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.io.URLTemplateLoader;

public class PrecompileHelperTest {

  private interface Wrapper {
    void accept(String wrapper) throws IOException;
  }

  URLTemplateLoader loader =
      new MapTemplateLoader()
          .define("input", "Hi {{this}}!")
          .define("root", "{{> partial/child}}")
          .define("partial/child", "CHILD!!!");

  Handlebars handlebars = new Handlebars(loader);

  @Test
  public void precompile() throws IOException {
    withTemplates(
        wrapper -> {
          String js =
              handlebars
                  .compileInline("{{precompile \"input\" wrapper=\"" + wrapper + "\"}}")
                  .apply("Handlebar.js");

          InputStream in = getClass().getResourceAsStream("/" + wrapper + ".precompiled.js");

          assertEquals(spaceSafe(IOUtils.toString(in)), spaceSafe(js), wrapper);

          in.close();
        });
  }

  private String spaceSafe(String value) {
    return value.replaceAll("\\s+", " ").trim();
  }

  @Test
  public void precompileWithPartial() throws IOException {
    withTemplates(
        wrapper -> {
          String js = handlebars.compileInline("{{precompile \"root\"}}").apply("Handlebar.js");

          InputStream in = getClass().getResourceAsStream("/partial.precompiled.js");

          assertEquals(spaceSafe(IOUtils.toString(in)), spaceSafe(js));

          in.close();
        });
  }

  private static void withTemplates(Wrapper consumer) throws IOException {
    var templates = new String[] {"anonymous", "none", "amd"};
    for (String template : templates) {
      consumer.accept(template);
    }
  }
}
