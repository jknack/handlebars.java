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

import java.io.IOException;

import org.junit.Test;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class HelperErrorTest extends AbstractTest {

  Hash source = $(
      "helper", "\n{{#block}} {{/block}}",
      "embedded", "\n{{#embedded}} {{/embedded}}",
      "basic", "\n{{basic}}",
      "notfoundblock", "\n{{#notfound hash=x}}{{/notfound}}",
      "notfound", "\n{{notfound hash=x}}"
      );

  @Test(expected = HandlebarsException.class)
  public void block() throws IOException {
    parse("helper");
  }

  @Test(expected = HandlebarsException.class)
  public void notfound() throws IOException {
    parse("notfound");
  }

  @Test(expected = HandlebarsException.class)
  public void notfoundblock() throws IOException {
    parse("notfoundblock");
  }

  @Test(expected = HandlebarsException.class)
  public void basic() throws IOException {
    parse("basic");
  }

  @Test(expected = HandlebarsException.class)
  public void embedded() throws IOException {
    parse("embedded");
  }

  private Object parse(final String uri) throws IOException {
    try {
      Hash helpers = $("basic", new Helper<Object>() {
        @Override
        public Object apply(final Object context, final Options options)
            throws IOException {
          throw new IllegalArgumentException("missing parameter: '0'.");
        }
      });
      shouldCompileTo((String) source.get(uri), $, helpers, "must fail");
      throw new IllegalStateException("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
