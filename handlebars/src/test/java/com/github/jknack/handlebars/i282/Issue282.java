package com.github.jknack.handlebars.i282;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue282 extends AbstractTest {

  @Test(expected = HandlebarsException.class)
  public void missingSubexpression() throws Exception {
    Hash helpers = $(
        "vowels", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return context.toString();
          }
        });
    shouldCompileTo("{{vowels (a)}}", $, helpers, "");
  }

  @Test
  public void subexpression() throws Exception {
    Hash helpers = $("inner-helper", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.helperName + "-" + context;
      }
    }, "outer-helper", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return context.toString() + options.params[0];
      }
    });
    shouldCompileTo("{{outer-helper (inner-helper 'abc') 'def'}}", $, helpers,
        "inner-helper-abcdef");
  }

  @Test
  public void vowels() throws Exception {
    Hash helpers = $(
        "a", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + context;
          }
        },
        "e", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + context;
          }
        },
        "i", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + context;
          }
        },
        "o", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + context;
          }
        },
        "u", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName;
          }
        },
        "vowels", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return context.toString();
          }
        });

    shouldCompileTo("{{vowels (a (e (i (o (u)))))}}", $, helpers, "aeiou");
  }

  @Test
  public void vowelsWithParams() throws Exception {
    Hash helpers = $(
        "a", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + ":" + options.params[0] + context;
          }
        },
        "e", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + ":" + options.params[0] + context;
          }
        },
        "i", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + ":" + options.params[0] + context;
          }
        },
        "o", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + ":" + options.params[0] + context;
          }
        },
        "u", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return options.helperName + ":" + context;
          }
        },
        "vowels", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options) throws IOException {
            return context.toString();
          }
        });

    shouldCompileTo("{{vowels (a (e (i (o (u 5) 4) 3) 2) 1)}}", $, helpers, "a:1e:2i:3o:4u:5");
  }
}
