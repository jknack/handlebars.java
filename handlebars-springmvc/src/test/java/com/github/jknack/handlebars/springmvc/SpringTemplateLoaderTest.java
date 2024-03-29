/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;

public class SpringTemplateLoaderTest {

  @Test
  public void sourceAt() throws IOException {
    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());

    TemplateSource source = loader.sourceAt("template");

    assertNotNull(source);
  }

  @Test
  public void fileNotFound() throws IOException {
    assertThrows(
        IOException.class,
        () -> new SpringTemplateLoader(new DefaultResourceLoader()).sourceAt("missingFile"));
  }
}
