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

import static com.github.jknack.handlebars.Literals.$;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

/**
 * Demostrate error reporting. It isn't a test.
 *
 * @author edgar.espina
 * @since 0.2.1
 */
public class HelperErrorTest {

  Map<String, String> source =
      $("/helper.hbs", "\n{{#block}} {{/block}}")
          .$("/embedded.hbs", "\n{{#embedded}} {{/embedded}}")
          .$("/basic.hbs", "\n{{basic}}")
          .$("/notfound.hbs", "\n{{notfound hash=x}}");

  @Test(expected = HandlebarsException.class)
  public void block() throws IOException {
    parse("helper");
  }

  @Test(expected = HandlebarsException.class)
  public void notfound() throws IOException {
    parse("notfound");
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
      Handlebars handlebars = new Handlebars(new MapTemplateLoader(source));
      handlebars.registerHelper("basic", new Helper<Object>() {
        @Override
        public CharSequence apply(final Object context, final Options options)
            throws IOException {
          throw new IllegalArgumentException("missing parameter: '0'.");
        }
      });
      Template compile = handlebars.compile(URI.create(uri));
      compile.apply(null);
      throw new IllegalStateException("An error is expected");
    } catch (HandlebarsException ex) {
      Handlebars.log(ex.getMessage());
      throw ex;
    }
  }
}
