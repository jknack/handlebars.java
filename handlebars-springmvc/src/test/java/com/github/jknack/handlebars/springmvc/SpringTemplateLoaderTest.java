package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

public class SpringTemplateLoaderTest {

  @Test
  public void load() throws IOException {
    SpringTemplateLoader loader =
        new SpringTemplateLoader(new DefaultResourceLoader());

    Reader reader = loader.load(URI.create("template"));

    assertNotNull(reader);
  }
}
