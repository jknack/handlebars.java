package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Issue585 {

  @Test
  public void shouldReadTemplateUsingProvidedCharset() throws Exception {
    assertArrayEquals(
        new byte[]{63, 63, 63, 63, 63, 63, 63, 63, 63, 44, 32, 63, 63, 63, 63, 63, 63},
        bytes(StandardCharsets.US_ASCII));

    assertArrayEquals(
        new byte[]{-20, -124, -72, -22, -77, -124, -20, -107, -68, 44, 32, -20, -107, -120, -21, -123, -107},
        bytes(StandardCharsets.UTF_8));
  }

  private byte[] bytes(Charset charset) throws IOException {
    Handlebars hbs = new Handlebars().setCharset(charset);
    String result = hbs.compile("issue585").apply(null);
    byte[] array = result.getBytes(charset);
    return array;
  }
}
