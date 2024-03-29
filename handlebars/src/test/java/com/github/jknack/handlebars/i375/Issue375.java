/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i375;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;

public class Issue375 extends AbstractTest {

  @Test
  public void dynamicPartials() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (dude)}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    shouldCompileTo(
        string,
        hash,
        $("dude", "dyndude"),
        $("dyndude", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsWithParam() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (dude) p1=1}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) p{{p1}} ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    shouldCompileTo(
        string,
        hash,
        $("dude", "dyndude"),
        $("dyndude", partial),
        "Dudes: Yehuda (http://yehuda) p1 Alan (http://alan) p1 ",
        "Dynamic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsViaLookupHelper() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup . 'dude')}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            },
            "dude",
            "dyndude");

    shouldCompileToWithPartials(
        string,
        hash,
        $("dyndude", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsViaLookupHelper3() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup dude)}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            },
            "dude",
            "dyndude");

    shouldCompileToWithPartials(
        string,
        hash,
        $("dyndude", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsViaLookupHelper2() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup ../. 'dude')}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            },
            "dude",
            "dyndude");

    shouldCompileToWithPartials(
        string,
        hash,
        $("dyndude", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsViaLookupHelper4() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup this 'name')}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    shouldCompileToWithPartials(
        string,
        hash,
        $("Yehuda", partial, "Alan", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialsViaLookupHelper5() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup name)}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    shouldCompileToWithPartials(
        string,
        hash,
        $("Yehuda", partial, "Alan", partial),
        "Dudes: Yehuda (http://yehuda) Alan (http://alan) ",
        "Basic partials output based on current context.");
  }

  @Test
  public void dynamicPartialNotFound() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup missing 'name')}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    assertThrows(
        HandlebarsException.class,
        () -> shouldCompileToWithPartials(string, hash, $("Yehuda", partial, "Alan", partial), ""));
  }

  @Test
  public void dynamicPartialNotFound2() throws IOException {
    String string = "Dudes: {{#dudes}}{{> (lookup this 'missing')}}{{/dudes}}";
    String partial = "{{name}} ({{url}}) ";
    Object hash =
        $(
            "dudes",
            new Object[] {
              $("name", "Yehuda", "url", "http://yehuda"), $("name", "Alan", "url", "http://alan")
            });

    assertThrows(
        HandlebarsException.class,
        () -> shouldCompileToWithPartials(string, hash, $("Yehuda", partial, "Alan", partial), ""));
  }
}
