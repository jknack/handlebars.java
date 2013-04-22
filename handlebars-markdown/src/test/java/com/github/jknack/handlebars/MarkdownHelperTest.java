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
 * Unit test for {@link MarkdownHelper}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class MarkdownHelperTest {

  @Test
  public void markdownFalsy() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("markdown", new MarkdownHelper());
    Template template = handlebars.compileInline("{{markdown this}}");

    assertEquals("", template.apply(null));
  }

  @Test
  public void markdown() throws IOException {
    String text = "";
    text += "# Header 1\n";
    text += "* Item 1\n";
    text += "* Item 2\n";
    text += "* Item 3\n\n";
    text += "## Header 2\n";

    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("markdown", new MarkdownHelper());
    Template template = handlebars.compileInline("{{markdown .}}");

    String expected = "<h1>Header 1</h1>\n";
    expected += "<ul>\n";
    expected += "  <li>Item 1</li>\n";
    expected += "  <li>Item 2</li>\n";
    expected += "  <li>Item 3</li>\n";
    expected += "</ul><h2>Header 2</h2>";
    System.out.println(template.apply(text));
    assertEquals(expected, template.apply(text));
  }

}
