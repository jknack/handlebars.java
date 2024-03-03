/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue42 extends AbstractTest {
  @Test
  @SuppressWarnings("unused")
  public void issue42() throws IOException {
    Object context =
        new Object() {
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
    shouldCompileTo("{{#foo}}{{title}} {{#bar}}{{title}}{{/bar}}{{/foo}}", context, "foo ");
  }
}
