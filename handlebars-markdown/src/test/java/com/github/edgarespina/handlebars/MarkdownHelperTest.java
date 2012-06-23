package com.github.edgarespina.handlebars;

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
  public void markdown() throws IOException {
    String text = "";
    text += "# Header 1\n";
    text += "* Item 1\n";
    text += "* Item 2\n";
    text += "* Item 3\n\n";
    text += "## Header 2\n";

    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("markdown", new MarkdownHelper());
    Template template = handlebars.compile("{{markdown .}}");

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
