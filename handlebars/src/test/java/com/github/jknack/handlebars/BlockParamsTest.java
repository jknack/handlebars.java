package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class BlockParamsTest extends AbstractTest {

  @Test
  public void eachWithNamedIndex() throws IOException {
    shouldCompileTo("{{#each users as |user userId|}}\n" +
        "  Id: {{userId}} Name: {{user.name}}\n" +
        "{{/each}}", $("users", new Object[]{$("name", "Pedro"), $("name", "Pablo") }), "\n" +
            "  Id: 0 Name: Pedro\n" +
            "\n" +
            "  Id: 1 Name: Pablo\n" +
            "");
  }

  @Test
  public void eachWithNamedKey() throws IOException {
    shouldCompileTo("{{#each users as |user userId|}}\n" +
        "  Id: {{userId}} Name: {{user.name}}\n" +
        "{{/each}}", $("users", Arrays.asList($("name", "Pedro"))), "\n" +
            "  Id: 0 Name: Pedro\n");
  }

  @Test
  public void shouldTakePrecedenceOverContextValues() throws IOException {
    shouldCompileTo("{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{value}}",
        $("value", "foo"), $("goodbyes", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            assertEquals(1, options.blockParams.size());
            return options.apply(options.fn, $("value", "bar"), Arrays.<Object> asList(1, 2));
          }
        }), "1foo");
  }

  @Test
  public void shouldTakePrecedenceOverHelperValues() throws IOException {
    shouldCompileTo("{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{value}}",
        $, $("goodbyes", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            assertEquals(1, options.blockParams.size());
            return options.apply(options.fn, $, Arrays.<Object> asList(1, 2));
          }
        }, "value", "foo"), "1foo");
  }

  @Test
  public void shouldNotTakePrecedenceOverPathedValues() throws IOException {
    shouldCompileTo("{{#goodbyes as |value|}}{{./value}}{{/goodbyes}}{{value}}",
        $("value", "bar"), $("goodbyes", new Helper<Object>() {
          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            assertEquals(1, options.blockParams.size());
            return options.apply(options.fn, $, Arrays.<Object> asList(1, 2));
          }
        }, "value", "foo"), "barfoo");
  }

  @Test
  public void shouldTakePrecedenceOverParentBlocParams() throws IOException {
    shouldCompileTo(
        "{{#goodbyes as |value|}}{{#goodbyes}}{{value}}{{#goodbyes as |value|}}{{value}}{{/goodbyes}}{{/goodbyes}}{{/goodbyes}}{{value}}",
        $("value", "foo"), $("goodbyes", new Helper<Object>() {
          int value = 1;

          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            if (options.blockParams.size() > 0) {
              return options.apply(options.fn, $("value", "bar"),
                  Arrays.<Object> asList(value++, value++));
            }
            return options.fn($("value", "bar"));
          }
        }), "13foo");
  }

  @Test
  public void shouldAllowBlockParamsOnChainedHelpers() throws IOException {
    shouldCompileTo(
        "{{#if bar}}{{else goodbyes as |value|}}{{value}}{{/if}}{{value}}",
        $("value", "foo"), $("goodbyes", new Helper<Object>() {
          int value = 1;

          @Override
          public Object apply(final Object context, final Options options)
              throws IOException {
            if (options.blockParams.size() > 0) {
              return options.apply(options.fn, $("value", "bar"),
                  Arrays.<Object> asList(value++, value++));
            }
            return options.fn($("value", "bar"));
          }
        }), "1foo");
  }

  @Test
  public void with() throws IOException {
    shouldCompileTo("{{#with title as |t|}}{{t}}{{/with}}", $("title", "Block Param"),
        "Block Param");
  }

  @Test
  public void blockParamText() throws IOException {
    assertEquals("{{#each users as |user userId|}}{{/each}}",
        compile("{{#each users as |user userId|}}{{/each}}").text());
  }

}
