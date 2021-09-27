package com.github.jknack.handlebars.i275;

import static com.github.jknack.handlebars.IgnoreWindowsLineMatcher.equalsToStringIgnoringWindowsNewLine;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class Issue275 {

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
    Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/issue275"))
        .prettyPrint(true)
        .deletePartialAfterMerge(true);

    handlebars.registerHelper("item", new Helper<Item>() {
      @Override
      public CharSequence apply(final Item item, final Options options) throws IOException {
        Template template = options.handlebars.compile("item" + item.getName());
        return template.apply(options.context);
      }
    });
    Template template = handlebars.compile("temporal-partials");
    assertThat(template.apply(Arrays.asList(new Item("1"), new Item("2"))),
        equalsToStringIgnoringWindowsNewLine("Items:\n" +
            "\n" +
            "Item: Custom\n" +
            "...\n" +
            "Item: 2\n" +
            "...\n"));
  }

  @Test
  public void defaultPartials() throws IOException {
    Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/issue275"))
        .prettyPrint(true)
        .deletePartialAfterMerge(false);

    handlebars.registerHelper("item", new Helper<Item>() {
      @Override
      public CharSequence apply(final Item item, final Options options) throws IOException {
        Template template = options.handlebars.compile("item" + item.getName());
        return template.apply(options.context);
      }
    });
    Template template = handlebars.compile("temporal-partials");
    assertThat(template.apply(Arrays.asList(new Item("1"), new Item("2"))),
        equalsToStringIgnoringWindowsNewLine("Items:\n" +
            "\n" +
            "Item: Custom\n" +
            "...\n" +
            "Item: Custom\n" +
            "...\n"));
  }
}
