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
package com.github.edgarespina.handlebars;

import static com.github.edgarespina.handlebars.Literals.$;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.Test;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class ParsingErrorTest {

  Map<String, String> source =
      $("/inbox/inbox.hbs", "{{value")
          .$("/block.hbs", "{{#block}}{{/nan}}")
          .$("/default.hbs", "{{> missingPartial}}")
          .$("/partial.hbs", "{{#value}}")
          .$("/invalidChar.hbs", "\n{{tag message.from \\\"user\\\"}}\n")
          .$("/root.hbs", "{{> p1}}")
          .$("/p1.hbs", "{{value")
          .$("/deep.hbs", "{{> deep1}}")
          .$("/deep1.hbs", "{{> deep2")
          .$("/unbalancedDelim.hbs", "{{=<% >=}}")
          .$("/paramOrder.hbs", "{{f param hash=1 param}}");

  @Test(expected = HandlebarsException.class)
  public void correctPath() throws IOException {
    parse("inbox/inbox");
  }

  @Test(expected = HandlebarsException.class)
  public void missingPartial() throws IOException {
    parse("default");
  }

  @Test(expected = HandlebarsException.class)
  public void invalidChar() throws IOException {
    parse("invalidChar");
  }

  @Test(expected = HandlebarsException.class)
  public void level1() throws IOException {
    parse("root");
  }

  @Test(expected = HandlebarsException.class)
  public void level2() throws IOException {
    parse("deep");
  }

  @Test(expected = HandlebarsException.class)
  public void block() throws IOException {
    parse("block");
  }

  @Test(expected = HandlebarsException.class)
  public void unbalancedDelim() throws IOException {
    parse("unbalancedDelim");
  }

  @Test(expected = HandlebarsException.class)
  public void paramOutOfOrder() throws IOException {
    parse("paramOrder");
  }

  private Object parse(final String uri) throws IOException {
    try {
      Handlebars handlebars = new Handlebars(new MapTemplateLoader(source));
      Template compile = handlebars.compile(URI.create(uri));
      System.out.println(compile);
      throw new IllegalStateException("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
