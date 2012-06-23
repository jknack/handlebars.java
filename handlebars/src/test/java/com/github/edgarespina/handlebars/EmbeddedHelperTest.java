package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
    input += "{{embedded \"user.hbs\"}}\n";
    input += "...\n";
    input += "</html>";

    Template template = new Handlebars().compile(input);
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
    input += "{{embedded \"user.hbs\" \"user-tmpl\"}}\n";
    input += "...\n";
    input += "</html>";

    Template template = new Handlebars().compile(input);
    assertEquals(expected, template.apply(null));
  }
}
