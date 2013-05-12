/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
          new ClassPathTemplateLoader(),
          new FileTemplateLoader("src/test/resources/inheritance")
      );

  @Test
  public void handlebarsWithCompositeLoader() throws IOException {
    Handlebars handlebars = new Handlebars()
        .with(loader);
    assertNotNull(handlebars.compile("template"));
    assertNotNull(handlebars.compile("home"));
  }

  @Test
  public void handlebarsWithTemplateLoaders() throws IOException {
    Handlebars handlebars = new Handlebars()
        .with(
            new ClassPathTemplateLoader(),
            new FileTemplateLoader("src/test/resources/inheritance")
        );
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
    assertEquals(new File("src/test/resources/inheritance", "home.hbs").getPath(),
        loader.resolve("home"));
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
