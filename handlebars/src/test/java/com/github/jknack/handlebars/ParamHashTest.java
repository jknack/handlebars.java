/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ParamHashTest extends AbstractTest {

  @Test
  public void truthParam() throws IOException {
    Hash helpers =
        $(
            "helper",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                assertEquals(true, options.param(0));
                return "ok";
              }
            });
    shouldCompileTo("{{helper . true}}", new Object(), helpers, "ok");
  }

  @Test
  public void falsyParam() throws IOException {
    Hash helpers =
        $(
            "helper",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                assertEquals(false, options.param(0));
                return "ok";
              }
            });
    shouldCompileTo("{{helper . false}}", new Object(), helpers, "ok");
  }

  @Test
  public void truthHash() throws IOException {
    Hash helpers =
        $(
            "helper",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                assertEquals(true, options.hash("b"));
                return "ok";
              }
            });
    shouldCompileTo("{{helper . b=true}}", new Object(), helpers, "ok");
  }

  @Test
  public void falsyHash() throws IOException {
    Hash helpers =
        $(
            "helper",
            new Helper<Object>() {
              @Override
              public Object apply(final Object context, final Options options) throws IOException {
                assertEquals(false, options.hash("b"));
                return "ok";
              }
            });
    shouldCompileTo("{{helper . b=false}}", new Object(), helpers, "ok");
  }

  @Test
  public void intHash() throws IOException {
    shouldCompileTo("{{var h=9}}", $, "Integer:9");
  }

  @Test
  public void intParam() throws IOException {
    shouldCompileTo("{{varp . 9}}", $, "Integer:9");
  }

  @Test
  public void stringHash() throws IOException {
    shouldCompileTo("{{var h=\"Hey!\"}}", $, "String:Hey!");
  }

  @Test
  public void stringParam() throws IOException {
    shouldCompileTo("{{varp . \"Hey!\"}}", $, "String:Hey!");
  }

  @Test
  public void charsHash() throws IOException {
    shouldCompileTo("{{var h='Hey!' }}", $, "String:Hey!");
  }

  @Test
  public void boolHash() throws IOException {
    shouldCompileTo("{{var h=true}}", $, "Boolean:true");
    shouldCompileTo("{{var h=false}}", $, "Boolean:false");
  }

  @Test
  public void boolParam() throws IOException {
    shouldCompileTo("{{varp . true}}", $, "Boolean:true");
    shouldCompileTo("{{varp . false}}", $, "Boolean:false");
  }

  @Test
  public void referenceHash() throws IOException {
    shouldCompileTo("{{var h=ref}}", $("ref", "."), "String:.");
  }

  @Test
  public void referenceParam() throws IOException {
    shouldCompileTo("{{varp . ref}}", $("ref", "."), "String:.");
  }

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = super.newHandlebars();
    handlebars.registerHelpers(this);
    return handlebars;
  }

  public CharSequence var(final Options options) {
    Object hash = options.hash("h");
    return hash.getClass().getSimpleName() + ":" + hash;
  }

  public CharSequence varp(final Object context, final Object arg) {
    return arg.getClass().getSimpleName() + ":" + arg;
  }
}
