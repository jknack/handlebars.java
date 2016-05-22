package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class DecoratorTest extends v4Test {

  @Test
  public void shouldApplyMustacheDecorators() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{*decorator}}{{/helper}}",
        $("helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("run", "success");
          }
        })),
        "success");
  }

  @Test
  public void shouldApplyAllowUndefinedReturn() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{*decorator}}suc{{/helper}}",
        $("helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.fn().toString() + options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("run", "cess");
          }
        })),
        "success");
  }

  @Test
  public void shouldApplyBlockDecorators() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{#*decorator}}success{{/decorator}}{{/helper}}",
        $("helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("run", options.fn());
          }
        })),
        "success");
  }

  @Test
  public void shouldSupportNestedDecorators() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{#*decorator}}{{#*nested}}suc{{/nested}}cess{{/decorator}}{{/helper}}",
        $("helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("run", options.data("nested").toString() + options.fn());
          }
        }, "nested", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("nested", options.fn());
          }
        })),
        "success");
  }

  @Test
  public void shouldApplyMultipleDecorators() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{#*decorator}}suc{{/decorator}}{{#*decorator}}cess{{/decorator}}{{/helper}}",
        $("helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            Object run = options.data("run");
            options.data("run", run == null ? options.fn() : run.toString() + options.fn());
          }
        })),
        "success");
  }

  @Test
  public void shouldAccessParentVariables() throws IOException {
    shouldCompileTo(
        "{{#helper}}{{*decorator foo}}{{/helper}}",
        $("hash", $("foo", "success"), "helpers", $("helper", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            return options.data("run");
          }
        }), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            options.data("run", options.param(0));
          }
        })),
        "success");
  }

  @Test
  public void shouldWorkWithRootProgram() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{*decorator \"success\"}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            assertEquals("success", options.param(0));
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(1, count.get());
  }

  @Test
  public void shouldFailWhenAccessingVariablesFromRoot() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{*decorator foo}}",
        $("hash", $("foo", "fail"), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            assertEquals(null, options.param(0));
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(1, count.get());
  }

  @Test
  public void shouldBlockFailWhenAccessingVariablesFromRoot() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{#*decorator foo}}success{{/decorator}}",
        $("hash", $("foo", "fail"), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            assertEquals(null, options.param(0));
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(1, count.get());
  }

  @Test
  public void shouldBlockFailWhenAccessingVariablesFromRoot2() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{#*decorator foo}}success{{/decorator}}{{#*decorator foo}}success{{/decorator}}",
        $("hash", $("foo", "fail"), "decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            assertEquals(null, options.param(0));
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(2, count.get());
  }

  @Test
  public void controlNumberOfExecutions() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{*decorator}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(1, count.get());
  }

  @Test
  public void controlNumberOfExecutions2() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{*decorator}}{{*decorator}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(2, count.get());
  }

  @Test
  public void controlNumberOfExecutions3() throws Exception {
    final AtomicInteger count = new AtomicInteger(0);
    shouldCompileTo(
        "{{*decorator}}{{*decorator}}{{*decorator}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            count.incrementAndGet();
          }
        })),
        "");
    assertEquals(3, count.get());
  }

  @Test
  public void gridData() throws Exception {
    shouldCompileTo(
        "{{#grid people}}\n" +
            "  {{#*column \"First Name\"}}{{firstName}}{{/column}}\n" +
            "  {{#*column \"Last Name\"}}{{lastName}}{{/column}}\n" +
            "{{/grid}}",
        $("helpers", $("grid", new Helper<List<Hash>>() {
          @Override
          public Object apply(final List<Hash> people, final Options options)
              throws IOException {
            List<Hash> columns = options.data("columns");
            String headers = "";
            for (Hash c : columns) {
              headers += c.get("key") + ", ";
            }
            String output = headers + "\n";
            for (Hash person : people) {
              for (Hash c : columns) {
                output += ((Template) c.get("body")).apply(person) + ", ";
              }
              output += "\n";
            }
            return output;
          }
        }), "decorators", $("column", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
            List<Hash> columns = options.data("columns");
            if (columns == null) {
              columns = new ArrayList<>();
              options.data("columns", columns);
            }
            columns.add($("key", options.param(0), "body", options.fn));
          }
        }), "hash", $("people", Arrays.asList($("firstName", "Pedro", "lastName", "PicaPiedra"),
            $("firstName", "Pablo", "lastName", "Marmol")))),
        "First Name, Last Name, \n" +
        "Pedro, PicaPiedra, \n" +
        "Pablo, Marmol, \n" +
        "");
  }

  @Test
  public void text() throws Exception {
    text("{{*decorator}}{{*decorator}}{{*decorator}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
          }
        })), "{{*decorator}}{{*decorator}}{{*decorator}}");

    text("{{#*decorator}}{{*decorator}}{{/decorator}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
          }
        })), "{{#*decorator}}{{*decorator}}{{/decorator}}");
  }

  @Test
  public void collectVarDecorator() throws IOException {
    assertSetEquals(Arrays.asList("decorator"), compile("{{#hello}}{{*decorator}}{{/hello}}{{k}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
          }
        }))).collect(TagType.STAR_VAR));
  }

  @Test
  public void collectBlockDecorator() throws IOException {
    assertSetEquals(Arrays.asList("decorator"), compile("{{#*decorator}}deco{{/decorator}}{{k}}",
        $("decorators", $("decorator", new Decorator() {
          @Override
          public void apply(final Template fn, final Options options) throws IOException {
          }
        }))).collect(TagType.START_SECTION));
  }

  private void assertSetEquals(final List<String> list, final List<String> list2) {
    assertEquals(new HashSet<String>(list), new HashSet<String>(list2));
  }
}
