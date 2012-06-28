package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
