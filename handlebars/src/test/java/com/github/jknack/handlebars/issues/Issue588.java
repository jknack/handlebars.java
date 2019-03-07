package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue588 extends v4Test {
  @Override protected void configure(Handlebars handlebars) {
    super.configure(handlebars);
    handlebars.setPreEvaluatePartialBlocks(false);
    handlebars.setInfiniteLoops(true);
  }

  @Test
  public void shouldNotDefineInlinePartialsInPartialBlockCallWithoutPreEvaluation()
      throws IOException {
    shouldCompileTo("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $("hash", $(), "partials", $("dude", "{{#> myPartial }}{{/myPartial}}")), "");

  }

  @Test
  public void shouldDefineInlinePartialsInPartialBlockCall() throws IOException {
    shouldCompileTo("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $("hash", $(), "partials",
            $("dude", "{{> @partial-block}}{{#> myPartial }}{{/myPartial}}")), "success");
  }

  @Test
  public void shouldOverrideBlockParams() throws IOException {
    shouldCompileTo("{{#> dude x=23}}{{#> dude x=12}}{{/dude}}{{/dude}}",
        $("hash", $(), "partials",
            $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>")),
        "<div x=23><div x=12></div></div>");
  }

  @Test
  public void shouldOverrideBlockParamsWithoutPreEvaluation() throws IOException {
    shouldCompileTo("{{#> dude x=23}}{{#> dude x=12}}{{/dude}}{{/dude}}",
        $("hash", $(), "partials",
            $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>")),
        "<div x=23><div x=12></div></div>");
  }

  @Test
  public void shouldOverrideBlockParamsWithFalse() throws IOException {
    shouldCompileTo("{{#> dude x=23}}{{#> dude x=false}}{{/dude}}{{/dude}}",
        $("hash", $(), "partials",
            $("dude", "<div {{#if x}}x={{x}}{{/if}}>{{> @partial-block}}</div>")),
        "<div x=23><div ></div></div>");
  }

  @Test
  public void shouldDefineInlinePartialsInPartialCall() throws IOException {
    shouldCompileTo("{{#> dude}}{{#*inline \"myPartial\"}}success{{/inline}}{{/dude}}",
        $("hash", $(), "partials",
            $("dude", "{{> @partial-block}}{{> myPartial }}")),
        "success");
  }
}
