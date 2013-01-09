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

public class EmbeddedHelperTest extends AbstractTest {

  @Test
  public void embedded() throws IOException {
    String expected = "<html>\n";
    expected += "...\n";
    expected += "<script id=\"user-hbs\" type=\"text/x-handlebars\">\n";
    expected += "<tr>\n";
    expected += "  <td>{{firstName}}</td>\n";
    expected += "  <td>{{lastName}}</td>\n";
    expected += "</tr>\n";
    expected += "</script>\n";
    expected += "...\n";
    expected += "</html>";

    String input = "<html>\n";
    input += "...\n";
    input += "{{embedded \"user\"}}\n";
    input += "...\n";
    input += "</html>";

    Hash partials = $("user", "<tr>\n" +
        "  <td>{{firstName}}</td>\n" +
        "  <td>{{lastName}}</td>\n" +
        "</tr>");
    shouldCompileToWithPartials(input, $, partials, expected);
  }

  @Test
  public void embeddedWithId() throws IOException {
    String expected = "<html>\n";
    expected += "...\n";
    expected += "<script id=\"user-tmpl\" type=\"text/x-handlebars\">\n";
    expected += "<tr>\n";
    expected += "  <td>{{firstName}}</td>\n";
    expected += "  <td>{{lastName}}</td>\n";
    expected += "</tr>\n";
    expected += "</script>\n";
    expected += "...\n";
    expected += "</html>";

    String input = "<html>\n";
    input += "...\n";
    input += "{{embedded \"user\" \"user-tmpl\"}}\n";
    input += "...\n";
    input += "</html>";

    Hash partials = $("user", "<tr>\n" +
        "  <td>{{firstName}}</td>\n" +
        "  <td>{{lastName}}</td>\n" +
        "</tr>");
    shouldCompileToWithPartials(input, $, partials, expected);

  }

}
