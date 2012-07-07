package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for pseudo-vars.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class PseudoVarsTests {

  @Test
  public void list() throws IOException {
    String input =
        "{{#list}}i={{@index}}\nfirst={{@first}}\nlast={{@last}}\n{{/list}}";
    Handlebars handlebars = new Handlebars()
        .setExposePseudoVariables(true);

    assertEquals("i=0\n" +
        "first=first\n" +
        "last=\n" +
        "i=1\n" +
        "first=\n" +
        "last=\n" +
        "i=2\n" +
        "first=\n" +
        "last=last\n",
        handlebars.compile(input).apply(new Object() {
          @SuppressWarnings("unused")
          public List<String> getList() {
            return Arrays.asList("a", "b", "c");
          }
        }));
  }
}
