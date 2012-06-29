package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.edgarespina.handlebars.io.ClassTemplateLoader;

@RunWith(Parameterized.class)
public class InheritanceTest {

  static Handlebars handlebars =
      new Handlebars(new ClassTemplateLoader("/inheritance"));

  private String name;

  public InheritanceTest(final String name) {
    this.name = name;
  }

  @Test
  public void inheritance() throws IOException {
    try {

      Template template = handlebars.compile(URI.create(name));
      CharSequence result = template.apply(new Object());
      String expected =
          toString(getClass().getResourceAsStream(
              "/inheritance/" + name + ".expected"));
      assertEquals(expected, result);
    } catch (HandlebarsException ex) {
      Handlebars.error(ex.getMessage());
      throw ex;
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    Collection<Object[]> data =
        Arrays.asList(new Object[] {"home" }, new Object[] {"about" },
            new Object[] {"base" });
    return data;
  }

  static String toString(final InputStream input)
      throws IOException {
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
