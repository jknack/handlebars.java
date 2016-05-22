package handlebarsjs.spec;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

public class BlockHelperMissingTest extends AbstractTest {

  @Test
  public void ifContextIsNotFoundHelperMissingIsUsed() throws IOException {
    String string = "{{hello}} {{link_to world}}";
    String context = "{ hello: Hello, world: world }";
    Hash helpers = $(HelperRegistry.HELPER_MISSING, new Helper<String>() {
      @Override
      public Object apply(final String context, final Options options) throws IOException {
        return new Handlebars.SafeString("<a>" + context + "</a>");
      }
    });

    shouldCompileTo(string, context, helpers, "Hello <a>world</a>");
  }

  @Test
  public void eachWithHash() throws IOException {
    String string = "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!";
    Object hash = $("goodbyes", $("<b>#1</b>", $("text", "goodbye"), "2", $("text", "GOODBYE")),
        "world", "world");
    shouldCompileTo(string, hash, "&lt;b&gt;#1&lt;/b&gt;. goodbye! 2. GOODBYE! cruel world!");
  }

  @Test
  @SuppressWarnings("unused")
  public void eachWithJavaBean() throws IOException {
    String string = "{{#each goodbyes}}{{@key}}. {{text}}! {{/each}}cruel {{world}}!";
    Object hash = new Object() {
      public Object getGoodbyes() {
        return new Object() {
          public Object getB1() {
            return new Object() {
              public String getText() {
                return "goodbye";
              }
            };
          }

          public Object get2() {
            return new Object() {
              public String getText() {
                return "GOODBYE";
              }
            };
          }
        };
      }

      public String getWorld() {
        return "world";
      }
    };
    try {
      shouldCompileTo(string, hash, "b1. goodbye! 2. GOODBYE! cruel world!");
    } catch (Throwable ex) {
      // on jdk7 property order differ from jdk6
      shouldCompileTo(string, hash, "2. GOODBYE! b1. goodbye! cruel world!");
    }
  }

  @Test
  public void with() throws IOException {
    String string = "{{#with person}}{{first}} {{last}}{{/with}}";
    shouldCompileTo(string, "{person: {first: Alan, last: Johnson}}", "Alan Johnson");
  }

  @Test
  public void ifHelper() throws IOException {
    String string = "{{#if goodbye}}GOODBYE {{/if}}cruel {{world}}!";

    shouldCompileTo(string, "{goodbye: true, world: world}", "GOODBYE cruel world!",
        "if with boolean argument shows the contents when true");

    shouldCompileTo(string, "{goodbye: dummy, world: world}", "GOODBYE cruel world!",
        "if with string argument shows the contents");

    shouldCompileTo(string, "{goodbye: false, world: world}", "cruel world!",
        "if with boolean argument does not show the contents when false");

    shouldCompileTo(string, "{world: world}", "cruel world!",
        "if with undefined does not show the contents");

    shouldCompileTo(string, $("goodbye", new Object[]{"foo" }, "world", "world"),
        "GOODBYE cruel world!",
        "if with non-empty array shows the contents");

    shouldCompileTo(string, $("goodbye", new Object[0], "world", "world"), "cruel world!",
        "if with empty array does not show the contents");
  }

  @Test
  public void dataCanBeLookupViaAnnotation() throws IOException {
    Template template = compile("{{@hello}}");
    String result = template.apply(Context.newContext($).data("hello", "hello"));
    assertEquals("hello", result);
  }

  @Test
  public void deepAnnotationTriggersAutomaticTopLevelData() throws IOException {
    String string = "{{#let world=\"world\"}}{{#if foo}}{{#if foo}}Hello {{@world}}{{/if}}{{/if}}{{/let}}";
    Hash helpers = $("let", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        for (Entry<String, Object> entry : options.hash.entrySet()) {
          options.data(entry.getKey(), entry.getValue());
        }
        return options.fn(context);
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply($("foo", true));
    assertEquals("Hello world", result);
  }

  @Test
  public void parameterCanBeLookupViaAnnotation() throws IOException {
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return "Hello " + options.hash("noun");
      }
    });
    Template template = compile("{{hello noun=@world}}", helpers);
    String result = template.apply(Context.newContext($).data("world", "world"));
    assertEquals("Hello world", result);
  }

  @Test
  public void dataIsInheritedDownstream() throws IOException {
    String string = "{{#let foo=bar.baz}}{{@foo}}{{/let}}";
    Hash helpers = $("let", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        for (Entry<String, Object> entry : options.hash.entrySet()) {
          options.data(entry.getKey(), entry.getValue());
        }
        return options.fn(context);
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply($("bar", $("baz", "hello world")));
    assertEquals("data variables are inherited downstream", "hello world", result);
  }

  @Test
  public void passingInDataWorksWithHelpersInPartials() throws IOException {
    String string = "{{>my_partial}}";
    Hash partials = $("my_partial", "{{hello}}");
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.data("adjective") + " " + options.get("noun");
      }
    });
    Template template = compile(string, helpers, partials);
    String result = template.apply(Context.newContext($("noun", "cat")).data("adjective", "happy"));
    assertEquals("Data output by helper inside partial", "happy cat", result);
  }

  @Test
  public void passingInDataWorksWithBlockHelpers() throws IOException {
    String string = "{{#hello}}{{world}}{{/hello}}";
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.fn();
      }
    }, "world", new Helper<Object>() {
      @Override
      public Object apply(final Object thing, final Options options) throws IOException {
        Boolean exclaim = options.get("exclaim");
        return options.data("adjective") + " world" + (exclaim ? "!" : "");
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply(Context.newContext($("exclaim", true))
        .data("adjective", "happy"));

    assertEquals("happy world!", result);
  }

  @Test
  public void passingInDataWorksWithBlockHelpersThatUsePaths() throws IOException {
    String string = "{{#hello}}{{world ../zomg}}{{/hello}}";
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.fn($("exclaim", "?"));
      }
    }, "world", new Helper<Object>() {
      @Override
      public Object apply(final Object thing, final Options options) throws IOException {
        return options.data("adjective") + " " + thing + options.get("exclaim", "");
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply(Context.newContext($("exclaim", true, "zomg", "world"))
        .data("adjective", "happy"));

    assertEquals("happy world?", result);
  }

  @Test
  public void passingInDataWorksWithBlockHelpersWhereChildrenUsePaths() throws IOException {
    String string = "{{#hello}}{{world ../zomg}}{{/hello}}";
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.data("accessData") + " " + options.fn($("exclaim", "?"));
      }
    }, "world", new Helper<Object>() {
      @Override
      public Object apply(final Object thing, final Options options) throws IOException {
        return options.data("adjective") + " " + thing + options.get("exclaim", "");
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply(Context.newContext($("exclaim", true, "zomg", "world"))
        .data("adjective", "happy").data("accessData", "#win"));

    assertEquals("#win happy world?", result);
  }

  @Test
  public void overrideInheritedDataWhenInvokingHelper() throws IOException {
    String string = "{{#hello}}{{world zomg}}{{/hello}}";
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.fn(Context.newContext($("exclaim", "?", "zomg", "world"))
            .data("adjective", "sad"));
      }
    }, "world", new Helper<Object>() {
      @Override
      public Object apply(final Object thing, final Options options) throws IOException {
        return options.data("adjective") + " " + thing + options.get("exclaim", "");
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply(Context.newContext($("exclaim", true, "zomg", "planet"))
        .data("adjective", "happy").data("accessData", "#win"));

    assertEquals("Overriden data output by helper", "sad world?", result);
  }

  @Test
  public void overrideInheritedDataWhenInvokingHelperWithDepth() throws IOException {
    String string = "{{#hello}}{{world zomg}}{{/hello}}";
    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return options.fn(Context.newContext($("exclaim", "?", "zomg", "world"))
            .data("adjective", "sad"));
      }
    }, "world", new Helper<Object>() {
      @Override
      public Object apply(final Object thing, final Options options) throws IOException {
        return options.data("adjective") + " " + thing + options.get("exclaim", "");
      }
    });
    Template template = compile(string, helpers);
    String result = template.apply(Context.newContext($("exclaim", true, "zomg", "planet"))
        .data("adjective", "happy").data("accessData", "#win"));

    assertEquals("Overriden data output by helper", "sad world?", result);
  }

  @Test
  public void helpersTakePrecedenceOverSameNamedContextProperties() throws IOException {
    Hash helpers = $("goodbye", new Helper<Map<String, Object>>() {
      @Override
      public Object apply(final Map<String, Object> context, final Options options)
          throws IOException {
        return context.get("goodbye").toString().toUpperCase();
      }
    }, "cruel", new Helper<String>() {
      @Override
      public Object apply(final String world, final Options options) throws IOException {
        return "cruel " + world.toUpperCase();
      }
    });
    shouldCompileTo("{{goodbye}} {{cruel world}}", "{goodbye: goodbye, world: world}", helpers,
        "GOODBYE cruel WORLD");
  }

  @Test
  public void blockHelpersTakePrecedenceOverSameNamedContextProperties() throws IOException {
    Hash helpers = $("goodbye", new Helper<Map<String, Object>>() {
      @Override
      public Object apply(final Map<String, Object> context, final Options options)
          throws IOException {
        return context.get("goodbye").toString().toUpperCase() + options.fn(context);
      }
    }, "cruel", new Helper<String>() {
      @Override
      public Object apply(final String world, final Options options) throws IOException {
        return "cruel " + world.toUpperCase();
      }
    });
    shouldCompileTo("{{#goodbye}} {{cruel world}}{{/goodbye}}", "{goodbye: goodbye, world: world}",
        helpers, "GOODBYE cruel WORLD");
  }

  @Test
  public void scopedNamesTakePrecedenceOverHelpers() throws IOException {
    Hash helpers = $("goodbye", new Helper<Map<String, Object>>() {
      @Override
      public Object apply(final Map<String, Object> context, final Options options)
          throws IOException {
        return context.get("goodbye").toString().toUpperCase();
      }
    }, "cruel", new Helper<String>() {
      @Override
      public Object apply(final String world, final Options options) throws IOException {
        return "cruel " + world.toUpperCase();
      }
    });
    shouldCompileTo("{{this.goodbye}} {{cruel world}} {{cruel this.goodbye}}",
        "{goodbye: goodbye, world: world}",
        helpers, "goodbye cruel WORLD cruel GOODBYE");
  }

  @Test
  public void scopedNamesTakePrecedenceOverBlockHelpers() throws IOException {
    Hash helpers = $("goodbye", new Helper<Map<String, Object>>() {
      @Override
      public Object apply(final Map<String, Object> context, final Options options)
          throws IOException {
        return context.get("goodbye").toString().toUpperCase() + options.fn(context);
      }
    }, "cruel", new Helper<String>() {
      @Override
      public Object apply(final String world, final Options options) throws IOException {
        return "cruel " + world.toUpperCase();
      }
    });
    shouldCompileTo("{{#goodbye}} {{cruel world}}{{/goodbye}} {{this.goodbye}}",
        "{goodbye: goodbye, world: world}",
        helpers, "GOODBYE cruel WORLD goodbye");
  }

  @Test
  public void helperCanTakeOptionalHash() throws IOException {
    Hash helpers = $("goodbye", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "GOODBYE " + options.hash("cruel") + " " + options.hash("world") + " "
            + options.hash("times") + " TIMES";
      }
    });
    shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" times=12}}",
        $, helpers, "GOODBYE CRUEL WORLD 12 TIMES");
  }

  @Test
  public void helperCanTakeOptionalHashWithBooleans() throws IOException {
    Hash helpers = $("goodbye", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        Boolean print = options.hash("print");
        if (print) {
          return "GOODBYE " + options.hash("cruel") + " " + options.hash("world");
        } else {
          return "NOT PRINTING";
        }
      }
    });
    shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=true}}",
        $, helpers, "GOODBYE CRUEL WORLD");

    shouldCompileTo("{{goodbye cruel=\"CRUEL\" world=\"WORLD\" print=false}}",
        $, helpers, "NOT PRINTING");
  }

  @Test
  public void blockHelperCanTakeOptionalHash() throws IOException {
    Hash helpers = $("goodbye", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "GOODBYE " + options.hash("cruel") + " " + options.fn(context) + " "
            + options.hash("times") + " TIMES";
      }
    });
    shouldCompileTo("{{#goodbye cruel=\"CRUEL\" times=12}}world{{/goodbye}}",
        $, helpers, "GOODBYE CRUEL world 12 TIMES");
  }

  @Test
  public void blockHelperCanTakeOptionalHashWithSingleQuotedStrings() throws IOException {
    Hash helpers = $("goodbye", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "GOODBYE " + options.hash("cruel") + " " + options.fn(context) + " "
            + options.hash("times") + " TIMES";
      }
    });
    shouldCompileTo("{{#goodbye cruel='CRUEL' times=12}}world{{/goodbye}}",
        $, helpers, "GOODBYE CRUEL world 12 TIMES");
  }

  @Test
  public void blockHelperCanTakeOptionalHashWithBooleans() throws IOException {
    Hash helpers = $("goodbye", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        Boolean print = options.hash("print");
        if (print) {
          return "GOODBYE " + options.hash("cruel") + " " + options.fn(context);
        } else {
          return "NOT PRINTING";
        }
      }
    });
    shouldCompileTo("{{#goodbye cruel=\"CRUEL\" print=true}}world{{/goodbye}}",
        $, helpers, "GOODBYE CRUEL world");

    shouldCompileTo("{{#goodbye cruel=\"CRUEL\" print=false}}world{{/goodbye}}",
        $, helpers, "NOT PRINTING");
  }

  @Test
  public void argumentsToHelpersCanBeRetrievedFromOptionsHashInStringForm() throws IOException {
    Hash helpers = $("wycats", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "HELP ME MY BOSS " + options.param(0) + ' ' + options.param(1);
      }
    });

    assertEquals("HELP ME MY BOSS is.a slave.driver",
        compile("{{wycats this is.a slave.driver}}", helpers, true).apply($));
  }

  @Test
  public void whenUsingBlockFormArgumentsToHelpersCanBeRetrievedFromOptionsHashInStringForm()
      throws IOException {
    Hash helpers = $("wycats", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "HELP ME MY BOSS " + options.param(0) + ' ' + options.param(1) + ": " + options.fn();
      }
    });

    assertEquals("HELP ME MY BOSS is.a slave.driver: help :(",
        compile("{{#wycats this is.a slave.driver}}help :({{/wycats}}", helpers, true).apply($));
  }

  @Test
  public void whenInsideABlockInStringModePassesTheAppropriateContextInTheOptionsHash()
      throws IOException {
    Hash helpers = $("tomdale", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "STOP ME FROM READING HACKER NEWS I " +
            context + " " + options.param(0);
      }
    }, "with", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return options.fn(context);
      }
    });

    assertEquals("STOP ME FROM READING HACKER NEWS I need-a dad.joke",
        compile("{{#with dale}}{{tomdale ../need dad.joke}}{{/with}}", helpers, true).apply(
            $("dale", $, "need", "need-a")));
  }

  @Test
  public void whenInsideABlockInStringModePassesTheAppropriateContextInTheOptionsHashToABlockHelper()
      throws IOException {
    Hash helpers = $("tomdale", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return "STOP ME FROM READING HACKER NEWS I " +
            context + " " + options.param(0) + " " + options.fn(context);
      }
    }, "with", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options)
          throws IOException {
        return options.fn(context);
      }
    });

    assertEquals(
        "STOP ME FROM READING HACKER NEWS I need-a dad.joke wot",
        compile("{{#with dale}}{{#tomdale ../need dad.joke}}wot{{/tomdale}}{{/with}}", helpers,
            true).apply(
            $("dale", $, "need", "need-a")));
  }
}
