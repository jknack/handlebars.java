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

public class EmbeddedHelperTest {

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

    Template template = new Handlebars(new StringTemplateLoader()).compile(input);
    assertEquals(expected, template.apply(null));
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

    Template template = new Handlebars(new StringTemplateLoader()).compile(input);
    assertEquals(expected, template.apply(null));
  }

  static class StringTemplateLoader extends TemplateLoader {
    protected Reader read(String location) throws IOException {
      System.out.println(location);
      if (location.equals("/user.hbs")) {
        return new StringReader(
          "<tr>\n" +
          "  <td>{{firstName}}</td>\n" +
          "  <td>{{lastName}}</td>\n" +
          "</tr>");
       }
       return null;
    }
  }
}
