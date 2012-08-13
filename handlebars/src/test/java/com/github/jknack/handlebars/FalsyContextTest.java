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
import java.util.Collections;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class FalsyContextTest {

  @Test
  public void nullContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(null));
  }

  @Test
  public void emptyContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(new Object()));
  }

  @Test
  public void emptyMapContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(Collections.emptyMap()));
  }

  @Test
  public void emptyList() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(Collections.emptyList()));
  }

  @Test
  public void anyContext() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("Hello {{world}}!");
    assertEquals("Hello !", template.apply(true));
  }
}
