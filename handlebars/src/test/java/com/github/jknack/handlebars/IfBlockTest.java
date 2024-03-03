/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class IfBlockTest extends AbstractTest {

  @Test
  public void truthy() throws IOException {
    // string
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", "x"), "true");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", "x"), "true");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", "x"), "");

    // object value
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", $), "true");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", $), "true");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", $), "");

    // true
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", true), "true");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", true), "true");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", true), "");

    // empty list
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", asList("0")), "true");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", asList("0")), "true");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", asList(0)), "");
  }

  @Test
  public void falsy() throws IOException {
    // empty string
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", ""), "false");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", ""), "false");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", ""), "false");

    // null value
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", null), "false");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", null), "false");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", null), "false");

    // false
    shouldCompileTo("{{#if value}}true{{else}}false{{/if}}", $("value", false), "false");
    shouldCompileTo("{{#value}}true{{^}}false{{/value}}", $("value", false), "false");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", false), "false");

    // empty list
    shouldCompileTo(
        "{{#if value}}true{{else}}false{{/if}}", $("value", Collections.emptyList()), "false");
    shouldCompileTo(
        "{{#value}}true{{^}}false{{/value}}", $("value", Collections.emptyList()), "false");
    shouldCompileTo("{{^value}}false{{/value}}", $("value", Collections.emptyList()), "false");
  }
}
