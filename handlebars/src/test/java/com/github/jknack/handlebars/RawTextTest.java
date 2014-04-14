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

import org.junit.Test;

/**
 * Unit test for {@link Template#text()}
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class RawTextTest extends AbstractTest {

  @Test
  public void plainText() throws IOException {
    assertEquals("Plain Text!", compile("Plain Text!").text());
  }

  @Test
  public void var() throws IOException {
    assertEquals("hello {{var}}!", compile("hello {{var}}!").text());
  }

  @Test
  public void varAmp() throws IOException {
    assertEquals("hello {{&var}}!", compile("hello {{& var}}!").text());
  }

  @Test
  public void var3() throws IOException {
    assertEquals("hello {{{var}}}!", compile("hello {{{ var }}}!").text());
  }

  @Test
  public void emptySection() throws IOException {
    assertEquals("hello {{#section}} {{/section}}!", compile("hello {{#section}} {{/section}}!")
        .text());
  }

  @Test
  public void section() throws IOException {
    assertEquals("hello {{#section}} hello {{/section}}!",
        compile("hello {{#section}} hello {{/section}}!").text());
  }

  @Test
  public void invertedEmptySection() throws IOException {
    assertEquals("hello {{^section}} {{/section}}!", compile("hello {{^section}} {{/section}}!")
        .text());
  }

  @Test
  public void invertedSection() throws IOException {
    assertEquals("hello {{^section}} hello {{var}}! {{/section}}!",
        compile("hello {{^section}} hello {{var}}! {{/section}}!")
            .text());
  }

  @Test
  public void partial() throws IOException {
    assertEquals("hello {{>user}}!", compile("hello {{>user}}!", $(), $("user", "{{user}}"))
        .text());
  }

  @Test
  public void partialWithContext() throws IOException {
    assertEquals("hello {{>user context}}!",
        compile("hello {{>user context}}!", $(), $("user", "{{user}}")).text());
  }

  @Test
  public void partialWithThisContext() throws IOException {
    assertEquals("hello {{>user this}}!",
        compile("hello {{>user this}}!", $(), $("user", "{{user}}")).text());
  }

  @Test
  public void helper() throws IOException {
    assertEquals("hello {{with context arg0 hash=hash0}}!",
        compile("hello {{with context arg0 hash=hash0}}!")
            .text());
  }

  @Test
  public void blockHelper() throws IOException {
    assertEquals("hello {{#with context arg0 hash=hash0}}hah{{/with}}!",
        compile("hello {{#with context arg0 hash=hash0}}hah{{/with}}!")
            .text());
  }

}
