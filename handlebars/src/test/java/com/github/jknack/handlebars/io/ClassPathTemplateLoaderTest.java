/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link ClassPathTemplateLoader}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ClassPathTemplateLoaderTest {

  @Test
  public void source() throws IOException {
    TemplateLoader loader = new ClassPathTemplateLoader();
    TemplateSource source = loader.sourceAt("template");
    assertNotNull(source);
  }

  @Test
  public void subFolder() throws IOException {
    URLTemplateLoader loader = new ClassPathTemplateLoader();
    loader.setSuffix(".yml");
    TemplateSource source = loader.sourceAt("mustache/specs/comments");
    assertNotNull(source);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    URLTemplateLoader loader = new ClassPathTemplateLoader();
    loader.setSuffix(".yml");
    TemplateSource source = loader.sourceAt("/mustache/specs/comments");
    assertNotNull(source);
  }

  @Test
  public void failLocate() throws IOException {
    assertThrows(
        FileNotFoundException.class,
        () -> {
          TemplateLoader loader = new ClassPathTemplateLoader();
          loader.sourceAt("notExist");
        });
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader loader = new ClassPathTemplateLoader("/mustache/specs", ".yml");
    TemplateSource source = loader.sourceAt("comments");
    assertNotNull(source);
  }

  @Test
  public void setBasePathWithDashDash() throws IOException {
    TemplateLoader loader = new ClassPathTemplateLoader("/mustache/specs/", ".yml");
    TemplateSource source = loader.sourceAt("comments");
    assertNotNull(source);
  }

  @Test
  public void nullSuffix() throws IOException {
    assertEquals(
        "suffix should be optional",
        new ClassPathTemplateLoader("/", null)
            .sourceAt("noextension")
            .content(StandardCharsets.UTF_8));

    assertEquals(
        "template.hbs",
        new ClassPathTemplateLoader("/", null)
            .sourceAt("template.hbs")
            .content(StandardCharsets.UTF_8));
  }

  @Test
  public void emptySuffix() throws IOException {
    assertEquals(
        "suffix should be optional",
        new ClassPathTemplateLoader("/", "")
            .sourceAt("noextension")
            .content(StandardCharsets.UTF_8));

    assertEquals(
        "template.hbs",
        new ClassPathTemplateLoader("/", "")
            .sourceAt("template.hbs")
            .content(StandardCharsets.UTF_8));
  }
}
