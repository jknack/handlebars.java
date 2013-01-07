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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.junit.Test;

import com.github.jknack.handlebars.TemplateLoader;

/**
 * Unit test for {@link ClassPathTemplateLoader}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileLocatorTest {

  @Test
  public void locate() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("template"));
    assertNotNull(reader);
  }

  @Test
  public void subFolder() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"), ".yml");
    Reader reader = locator.load(URI.create("mustache/specs/comments"));
    assertNotNull(reader);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"), ".yml");
    Reader reader = locator.load(URI.create("mustache/specs/comments"));
    assertNotNull(reader);
  }

  @Test(expected = FileNotFoundException.class)
  public void failLocate() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"));
    locator.load(URI.create("notExist"));
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources/mustache/specs"), ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources/mustache/specs/"), ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }

}
