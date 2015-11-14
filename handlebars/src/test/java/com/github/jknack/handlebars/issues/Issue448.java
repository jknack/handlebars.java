package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue448 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars.registerHelpers("helpers.js", "Handlebars.registerHelper('is', function(value, test, options) {\n" +
          "                if (value == test) {\n" +
          "                    return options.fn(this);\n" +
          "                } else {\n" +
          "                    return options.inverse(this);\n" +
          "                }});\n"
          + "Handlebars.registerHelper('remainder', function (value, divisor) {\n" +
          "                return value % divisor;\n" +
          "            });\n");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void shouldUseContext() throws IOException {
    shouldCompileTo("{{#each letter}}" +
        "{{#if this}}{{@index}}={{this}}{{/if}}\n" +
        "{{#is 0 0}}{{@index}}={{this}}{{/is}}\n" +
        "{{/each}}",
        $("hash", $("letter", new Object[]{'a', 'b', 'c', 'd' })),
        "0=a\n" +
            "0=a\n" +
            "1=b\n" +
            "1=b\n" +
            "2=c\n" +
            "2=c\n" +
            "3=d\n" +
            "3=d\n");
  }

  @Test
  public void shouldApplySubOnBlockParams() throws IOException {
    shouldCompileTo(
        "{{#each this as |c|}} {{c}} - {{#is 0 (remainder c 2)}}even{{else}} odd{{/is}} \n" +
            "{{/each}}",
        $("hash", new Object[]{4, 5, 6, 7, 8 }),
        " 4 - even \n" +
        " 5 -  odd \n" +
        " 6 - even \n" +
        " 7 -  odd \n" +
        " 8 - even \n");
  }

  @Test
  public void shouldApplySubOnThis() throws IOException {
    shouldCompileTo(
        "{{#each this}} {{this}} - {{#is 0 (remainder this 2)}}even{{else}} odd{{/is}} \n" +
            "{{/each}}",
        $("hash", new Object[]{4, 5, 6, 7, 8 }),
        " 4 - even \n" +
        " 5 -  odd \n" +
        " 6 - even \n" +
        " 7 -  odd \n" +
        " 8 - even \n");
  }

}
