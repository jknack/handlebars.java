package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PrecompiledHelperTest {

  TemplateLoader loader = new MapTemplateLoader()
      .define("input", "Hi {{this}}!");

  Handlebars handlebars = new Handlebars(loader);

  private String wrapper;

  public PrecompiledHelperTest(final String wrapper) {
    this.wrapper = wrapper;
  }

  @Test
  public void precompiled() throws IOException {
    String js =
        handlebars.compile(
            "{{precompiled \"input\" wrapper=\"" + wrapper + "\"}}").apply(
            "Handlebar.js");

    InputStream in =
        getClass().getResourceAsStream("/" + wrapper + ".precompiled.js");

    assertEquals(IOUtils.toString(in), js);

    in.close();

  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[] {"default" }, new Object[] {"simple" },
        new Object[] {"amd" });
  }
}
