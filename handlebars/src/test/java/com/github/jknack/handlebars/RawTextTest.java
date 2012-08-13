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
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TemplateLoader;

/**
 * Unit test for {@link Template#text()}
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class RawTextTest {

  @Test
  public void plainText() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Plain Text!");
    assertEquals("Plain Text!", template.text());
  }

  @Test
  public void var() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{var}}!");
    assertEquals("hello {{var}}!", template.text());
  }

  @Test
  public void varAmp() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{& var}}!");
    assertEquals("hello {{&var}}!", template.text());
  }

  @Test
  public void var3() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{{ var }}}!");
    assertEquals("hello {{{var}}}!", template.text());
  }

  @Test
  public void emptySection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{#section}} {{/section}}!");
    assertEquals("hello {{#section}} {{/section}}!", template.text());
  }

  @Test
  public void section() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{#section}} hello {{var}}! {{/section}}!");
    assertEquals("hello {{#section}} hello {{var}}! {{/section}}!",
        template.text());
  }

  @Test
  public void invertedEmptySection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{^section}} {{/section}}!");
    assertEquals("hello {{^section}} {{/section}}!", template.text());
  }

  @Test
  public void invertedSection() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{^section}} hello {{var}}! {{/section}}!");
    assertEquals("hello {{^section}} hello {{var}}! {{/section}}!",
        template.text());
  }

  @Test
  public void partial() throws IOException {
    Handlebars handlebars = new Handlebars(new TemplateLoader() {
      @Override
      protected Reader read(final String location) throws IOException {
        return new StringReader("user.hbs");
      }
    });
    Template template = handlebars.compile("hello {{> user }}!");
    assertEquals("hello {{>user}}!", template.text());
  }

  @Test
  public void helper() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("hello {{dateFormat context arg0 hash=hash0}}!");
    assertEquals("hello {{dateFormat context arg0 hash=hash0}}!", template.text());
  }

  @Test
  public void blockHelper() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template =
        handlebars.compile("hello {{#with context arg0 hash=hash}}hah{{/with}}!");
    assertEquals("hello {{#with context arg0 hash=hash}}hah{{/with}}!", template.text());
  }
}
