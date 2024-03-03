/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import mustache.specs.Spec;
import mustache.specs.SpecTest;

public class HelpersTest extends SpecTest {

  @Override
  protected HelperRegistry configure(final Handlebars handlebars) {
    handlebars.registerHelper(
        "list",
        new Helper<List<Object>>() {
          @Override
          public Object apply(final List<Object> list, final Options options) throws IOException {
            String text = "";
            if (options.isFalsy(list)) {
              return text;
            }

            text += "<ul>\n";
            for (Object element : list) {
              text += "  <li>\n    ";
              text += options.fn(element);
              text += "  </li>\n";
            }
            text += "</ul>\n";
            return text;
          }
        });
    handlebars.registerHelper(
        "fullName",
        new Helper<Map<String, Object>>() {
          @Override
          public Object apply(final Map<String, Object> context, final Options options)
              throws IOException {
            return context.get("firstName") + " " + context.get("lastName");
          }
        });
    handlebars.registerHelper(
        "agree_button",
        new Helper<Map<String, Object>>() {
          @Override
          public Object apply(final Map<String, Object> context, final Options options)
              throws IOException {
            String text =
                "<button>I agree. I "
                    + context.get("emotion")
                    + " "
                    + context.get("name")
                    + "</button>";
            return new Handlebars.SafeString(text);
          }
        });
    handlebars.registerHelper(
        "link",
        new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return "<a href='" + options.param(0) + "'>" + context + "</a>";
          }
        });
    handlebars.registerHelper(
        "link-hash",
        new Helper<String>() {
          @Override
          public Object apply(final String text, final Options options) throws IOException {
            StringBuilder classes = new StringBuilder();
            String sep = " ";
            for (Entry<String, Object> entry : options.hash.entrySet()) {
              classes
                  .append(entry.getKey())
                  .append("=\"")
                  .append(entry.getValue())
                  .append("\"")
                  .append(sep);
            }
            classes.setLength(classes.length() - sep.length());
            return new Handlebars.SafeString("<a " + classes + ">" + text + "</a>");
          }
        });
    return super.configure(handlebars);
  }

  @ParameterizedTest
  @MethodSource("data")
  public void helpers(Spec spec) throws IOException {
    runSpec(spec);
  }

  public static List<Spec> data() throws IOException {
    return data(HelpersTest.class, "helpers.yml");
  }
}
