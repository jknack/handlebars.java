package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;

public class SpringTemplateLoaderTest {

  @Test
  public void sourceAt() throws IOException {
    SpringTemplateLoader loader =
        new SpringTemplateLoader(new DefaultResourceLoader());

    TemplateSource source = loader.sourceAt("template");

    assertNotNull(source);
  }

  @Test(expected = IOException.class)
  public void fileNotFound() throws IOException {
    new SpringTemplateLoader(new DefaultResourceLoader()).sourceAt("missingFile");
  }
}
