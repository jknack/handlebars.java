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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.edgarespina.handlebars.io.ClassTemplateLoader;

@RunWith(Parameterized.class)
public class InheritanceTest {

  static Handlebars handlebars =
      new Handlebars(new ClassTemplateLoader("/inheritance"));

  private String name;

  public InheritanceTest(final String name) {
    this.name = name;
  }

  @Test
  public void inheritance() throws IOException {
    try {

      Template template = handlebars.compile(URI.create(name));
      CharSequence result = template.apply(new Object());
      String expected =
          toString(getClass().getResourceAsStream(
              "/inheritance/" + name + ".expected"));
      assertEquals(expected, result);
    } catch (HandlebarsException ex) {
      Handlebars.error(ex.getMessage());
      throw ex;
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    Collection<Object[]> data =
        Arrays.asList(new Object[] {"home" }, new Object[] {"about" },
            new Object[] {"base" });
    return data;
  }

  static String toString(final InputStream input)
      throws IOException {
    StringBuilder buffer = new StringBuilder(1024 * 4);
    int ch;
    while ((ch = input.read()) != -1) {
      buffer.append((char) ch);
    }
    buffer.trimToSize();
    input.close();
    return buffer.toString();
  }
}
