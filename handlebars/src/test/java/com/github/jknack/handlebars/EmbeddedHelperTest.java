/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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

    Hash partials =
        $("user", "<tr>\n" + "  <td>{{firstName}}</td>\n" + "  <td>{{lastName}}</td>\n" + "</tr>");
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

    Hash partials =
        $("user", "<tr>\n" + "  <td>{{firstName}}</td>\n" + "  <td>{{lastName}}</td>\n" + "</tr>");
    shouldCompileToWithPartials(input, $, partials, expected);
  }
}
