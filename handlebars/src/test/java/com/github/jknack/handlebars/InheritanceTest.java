/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class InheritanceTest {

  static Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/inheritance"));

  static {
    handlebars.setPrettyPrint(true);
  }

  @Test
  public void inheritance() throws IOException {
    var templates = new String[] {"home", "about", "base"};
    for (var name : templates) {
      try {
        Template template = handlebars.compile(name);
        CharSequence result = template.apply(new Object());
        String expected =
            FileUtils.readFileToString(
                new File("src/test/resources/inheritance/" + name + ".expected"));
        assertEquals(expected, result);
      } catch (HandlebarsException ex) {
        Handlebars.error(ex.getMessage());
        throw ex;
      }
    }
  }

  //  static String toString(final InputStream input) throws IOException {
  //    StringBuilder buffer = new StringBuilder(1024 * 4);
  //    int ch;
  //    while ((ch = input.read()) != -1) {
  //      buffer.append((char) ch);
  //    }
  //    buffer.trimToSize();
  //    input.close();
  //    return buffer.toString();
  //  }
}
