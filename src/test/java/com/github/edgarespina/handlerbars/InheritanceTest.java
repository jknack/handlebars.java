package com.github.edgarespina.handlerbars;

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

import com.github.edgarespina.handlerbars.io.ClasspathLocator;

@RunWith(Parameterized.class)
public class InheritanceTest {

  private String name;

  public InheritanceTest(final String name) {
    this.name = name;
  }

  @Test
  public void inheritance() throws IOException {
    Handlebars handlebars =
        new Handlebars(new ClasspathLocator("/inheritance"));
    Template template = handlebars.compile(URI.create(name));
    CharSequence result = template.apply(new Object());
    String expected =
        toString(getClass().getResourceAsStream(
            "/inheritance/" + name + ".expected"));
    assertEquals(expected, result);
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
