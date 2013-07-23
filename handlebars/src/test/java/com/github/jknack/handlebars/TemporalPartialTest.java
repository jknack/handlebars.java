package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class TemporalPartialTest {

  public static class Item {
    private String name;

    public Item(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Test
  public void temporalPartials() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.setPrettyPrint(true);

    handlebars.registerHelper("item", new Helper<Item>() {
      @Override
      public CharSequence apply(final Item item, final Options options) throws IOException {
        Template template = options.handlebars.compile("item" + item.getName());
        return template.apply(options.context);
      }
    });
    Template template = handlebars.compile("temporal-partials");
    assertEquals("Items:\n" +
        "\n" +
        "Item: Custom\n" +
        "...\n" +
        "Item: 2\n" +
        "...\n", template.apply(Arrays.asList(new Item("1"), new Item("2"))));
  }

  @Test
  public void defaultPartials() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.setPrettyPrint(true);
    Template template = handlebars.compile("derived");
    assertEquals("\n" +
        "<html>\n" +
        "<head>\n" +
        "  <title>\n" +
        "     Home \n" +
        "  </title>\n" +
        "</head>\n" +
        "<body>\n" +
        "  <h1> Home </h1>\n" +
        "  HOME\n" +
        "</body>\n" +
        "</html>", template.apply(null));
  }
}
