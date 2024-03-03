/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

@RunWith(Parameterized.class)
public class InheritanceTest {

  static Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/inheritance"));

  static {
    handlebars.setPrettyPrint(true);
  }

  private String name;

  public InheritanceTest(final String name) {
    this.name = name;
  }

  @Test
  public void inheritance() throws IOException {
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

  @Parameters
  public static Collection<Object[]> data() {
    Collection<Object[]> data =
        Arrays.asList(new Object[] {"home"}, new Object[] {"about"}, new Object[] {"base"});
    return data;
  }

  static String toString(final InputStream input) throws IOException {
    StringBuilder buffer = new StringBuilder(1024 * 4);
    int ch;
    while ((ch = input.read()) != -1) {
      buffer.append((char) ch);
    }
    buffer.trimToSize();
    input.close();
    return buffer.toString();
  }
}
