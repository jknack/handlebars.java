package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Bugs {

  @Test
  @SuppressWarnings("unused")
  public void bug42() throws IOException {
    Object context = new Object() {
      public Object getFoo() {
        return new Object() {
          public String getTitle() {
            return "foo";
          }

          public Object getBar() {
            return new Object() {
              public String getTitle() {
                return null;
              }

              @Override
              public String toString() {
                return "bar";
              }
            };
          }

          @Override
          public String toString() {
            return "foo";
          }
        };
      }
    };

    Handlebars handlebars = new Handlebars();

    Template template =
        handlebars
            .compile("{{#foo}}{{title}} {{#bar}}{{title}}{{/bar}}{{/foo}}");

    assertEquals("foo ", template.apply(context));
  }

}
