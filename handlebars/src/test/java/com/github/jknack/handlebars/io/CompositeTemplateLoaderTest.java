/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;

/**
 * Unit test for {@link CompositeTemplateLoader}.
 *
 * @author edgar.espina
 * @since 1.0.0
 */
public class CompositeTemplateLoaderTest {

  private CompositeTemplateLoader loader =
      new CompositeTemplateLoader(
          new ClassPathTemplateLoader(), new FileTemplateLoader("src/test/resources/inheritance"));

  @Test
  public void handlebarsWithCompositeLoader() throws IOException {
    Handlebars handlebars = new Handlebars().with(loader);
    assertNotNull(handlebars.compile("template"));
    assertNotNull(handlebars.compile("home"));
  }

  @Test
  public void handlebarsWithTemplateLoaders() throws IOException {
    Handlebars handlebars =
        new Handlebars()
            .with(
                new ClassPathTemplateLoader(),
                new FileTemplateLoader("src/test/resources/inheritance"));
    assertNotNull(handlebars.compile("template"));
    assertNotNull(handlebars.compile("home"));
  }

  @Test
  public void sourceAtCp() throws IOException {
    assertNotNull(loader.sourceAt("template"));
  }

  @Test
  public void resolveSourceAtCp() throws IOException {
    assertEquals("/template.hbs", loader.resolve("template"));
  }

  @Test
  public void sourceAtFs() throws IOException {
    assertNotNull(loader.sourceAt("home"));
  }

  @Test
  public void resolveSourceAtFs() throws IOException {
    assertTrue(
        new File("src/test/resources/inheritance", "home.hbs")
                .getPath()
                .compareTo(new File(loader.resolve("home")).getPath())
            == 0);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getPrefix() throws IOException {
    loader.getPrefix();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getSuffix() throws IOException {
    loader.getSuffix();
  }

  @Test
  public void getDelegates() throws IOException {
    Iterable<TemplateLoader> delegates = loader.getDelegates();
    assertNotNull(delegates);
    Iterator<TemplateLoader> iterator = delegates.iterator();
    assertNotNull(iterator);
    assertTrue(iterator.next() instanceof ClassPathTemplateLoader);
    assertTrue(iterator.next() instanceof FileTemplateLoader);
  }
}
