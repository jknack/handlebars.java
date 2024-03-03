/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * Unit test for {@link ClassPathTemplateLoader}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileTemplateLoaderTest {

  @Test
  public void sourceAt() throws IOException {
    TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"));
    TemplateSource source = loader.sourceAt("template");
    assertNotNull(source);
  }

  @Test
  public void subFolder() throws IOException {
    TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"), ".yml");
    TemplateSource source = loader.sourceAt("mustache/specs/comments");
    assertNotNull(source);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"), ".yml");
    TemplateSource source = loader.sourceAt("/mustache/specs/comments");
    assertNotNull(source);
  }

  @Test(expected = FileNotFoundException.class)
  public void failLocate() throws IOException {
    TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"));
    loader.sourceAt("notExist");
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader loader =
        new FileTemplateLoader(new File("src/test/resources/mustache/specs"), ".yml");
    TemplateSource source = loader.sourceAt("comments");
    assertNotNull(source);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    TemplateLoader loader =
        new FileTemplateLoader(new File("src/test/resources/mustache/specs/"), ".yml");
    TemplateSource source = loader.sourceAt("comments");
    assertNotNull(source);
  }

  @Test
  public void nullSuffix() throws IOException {
    assertEquals(
        "suffix should be optional",
        new FileTemplateLoader("src/test/resources/", null)
            .sourceAt("noextension")
            .content(StandardCharsets.UTF_8));
  }

  @Test
  public void emptySuffix() throws IOException {
    assertEquals(
        "suffix should be optional",
        new FileTemplateLoader("src/test/resources/", "")
            .sourceAt("noextension")
            .content(StandardCharsets.UTF_8));
  }
}
