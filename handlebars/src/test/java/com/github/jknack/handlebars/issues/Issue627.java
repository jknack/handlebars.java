package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue627 extends v4Test {

  @Override protected void configure(Handlebars handlebars) {
    handlebars.registerHelpers(ConditionalHelpers.class);
  }

  @Test
  public void eq() throws Exception {
    shouldCompileTo("{{#eq 1 1}}yes{{/eq}}", $, "yes");
    shouldCompileTo("{{#eq 1 2}}yes{{else}}no{{/eq}}", $, "no");
    shouldCompileTo("{{#eq a b}}yes{{/eq}}", $("hash", $("a", "a", "b", "a")), "yes");

    // as expression
    shouldCompileTo("{{eq 1 1}}", $, "true");
    shouldCompileTo("{{eq 1 0}}", $, "false");

    shouldCompileTo("{{eq 1 1 yes='yes' no='no'}}", $, "yes");
    shouldCompileTo("{{eq 1 0 yes='yes' no='no'}}", $, "no");

    // as subexpression
    shouldCompileTo("{{#if (eq 1 1)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void neq() throws Exception {
    shouldCompileTo("{{#neq 1 1}}yes{{/neq}}", $, "");
    shouldCompileTo("{{#neq 1 2}}yes{{else}}no{{/neq}}", $, "yes");
    shouldCompileTo("{{#neq a b}}yes{{/neq}}", $("hash", $("a", "a", "b", "b")), "yes");

    // as expression
    shouldCompileTo("{{neq 1 1}}", $, "false");
    shouldCompileTo("{{neq 1 0}}", $, "true");
    shouldCompileTo("{{neq 1 1 yes='yes' no='no'}}", $, "no");
    shouldCompileTo("{{neq 1 0 yes='yes' no='no'}}", $, "yes");

    // as subexpression
    shouldCompileTo("{{#if (neq 1 2)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void gt() throws Exception {
    shouldCompileTo("{{#gt 2 1}}yes{{else}}no{{/gt}}", $, "yes");
    shouldCompileTo("{{#gt 2 3}}yes{{else}}no{{/gt}}", $, "no");
    shouldCompileTo("{{#gt 2 2}}yes{{else}}no{{/gt}}", $, "no");
    shouldCompileTo("{{#gte 2 2}}yes{{else}}no{{/gte}}", $, "yes");

    // as expression
    shouldCompileTo("{{gt 2 1}}", $, "true");
    shouldCompileTo("{{gt 1 12}}", $, "false");
    shouldCompileTo("{{gt 2 1 yes='y' no='n'}}", $, "y");
    shouldCompileTo("{{gt 1 12 yes='y' no='n'}}", $, "n");
    shouldCompileTo("{{gte 2 1 yes='y' no='n'}}", $, "y");
    shouldCompileTo("{{gte 2 2 yes='y' no='n'}}", $, "y");

    // as subexpression
    shouldCompileTo("{{#if (gte 2 1)}}yes{{/if}}", $, "yes");
    shouldCompileTo("{{#if (gt 2 2)}}yes{{/if}}", $, "");
    shouldCompileTo("{{#if (gte 2 2)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void lt() throws Exception {
    shouldCompileTo("{{#lt 2 1}}yes{{else}}no{{/lt}}", $, "no");
    shouldCompileTo("{{#lt 2 3}}yes{{else}}no{{/lt}}", $, "yes");
    shouldCompileTo("{{#lt 2 2}}yes{{else}}no{{/lt}}", $, "no");
    shouldCompileTo("{{#lte 2 2}}yes{{else}}no{{/lte}}", $, "yes");

    // as expression
    shouldCompileTo("{{lt 2 1}}", $, "false");
    shouldCompileTo("{{lt 1 12}}", $, "true");
    shouldCompileTo("{{lt 1 12 yes='y' no='n'}}", $, "y");
    shouldCompileTo("{{lt 14 12 yes='y' no='n'}}", $, "n");
    shouldCompileTo("{{lte 14 14 yes='y' no='n'}}", $, "y");
    shouldCompileTo("{{lte 14 12 yes='y' no='n'}}", $, "n");

    // as subexpression
    shouldCompileTo("{{#if (lte 2 1)}}yes{{/if}}", $, "");
    shouldCompileTo("{{#if (lt 2 2)}}yes{{/if}}", $, "");
    shouldCompileTo("{{#if (lte 2 2)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void and() throws Exception {
    shouldCompileTo("{{#and 2 1}}yes{{else}}no{{/and}}", $, "yes");
    shouldCompileTo("{{#and 0 1}}yes{{else}}no{{/and}}", $, "no");
    shouldCompileTo("{{#and '' ''}}yes{{else}}no{{/and}}", $, "no");
    shouldCompileTo("{{#and 'a' 'b'}}yes{{else}}no{{/and}}", $, "yes");
    shouldCompileTo("{{#and 'a' ''}}yes{{else}}no{{/and}}", $, "no");

    // N args
    shouldCompileTo("{{#and 2 1 4 5 6}}yes{{else}}no{{/and}}", $, "yes");
    shouldCompileTo("{{#and 0 1 4 5 6}}yes{{else}}no{{/and}}", $, "no");
    shouldCompileTo("{{#and 1 0 4 5 6}}yes{{else}}no{{/and}}", $, "no");

    // as expression
    shouldCompileTo("{{and 2 1}}", $, "true");
    shouldCompileTo("{{and 2 1 yes='y'}}", $, "y");
    shouldCompileTo("{{and 0 1 no='n'}}", $, "n");

    // as subexpression
    shouldCompileTo("{{#if (and 2 1)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void or() throws Exception {
    shouldCompileTo("{{#or 2 1}}yes{{else}}no{{/or}}", $, "yes");
    shouldCompileTo("{{#or 0 1}}yes{{else}}no{{/or}}", $, "yes");
    shouldCompileTo("{{#or '' ''}}yes{{else}}no{{/or}}", $, "no");
    shouldCompileTo("{{#or 'a' 'b'}}yes{{else}}no{{/or}}", $, "yes");
    shouldCompileTo("{{#or 'a' ''}}yes{{else}}no{{/or}}", $, "yes");

    // N args
    shouldCompileTo("{{#or 0 1 0}}yes{{else}}no{{/or}}", $, "yes");

    shouldCompileTo("{{#or 0 0 1}}yes{{else}}no{{/or}}", $, "yes");

    shouldCompileTo("{{#or 0 0 0 1}}yes{{else}}no{{/or}}", $, "yes");

    shouldCompileTo("{{#or 0 0 0 0}}yes{{else}}no{{/or}}", $, "no");

    // as expression
    shouldCompileTo("{{or 2 1}}", $, "true");
    shouldCompileTo("{{or 0 1}}", $, "true");
    shouldCompileTo("{{or 2 1 yes='y'}}", $, "y");
    shouldCompileTo("{{or 0 0 no='n'}}", $, "n");

    // as subexpression
    shouldCompileTo("{{#if (or 2 1)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void not() throws Exception {
    shouldCompileTo("{{#not true}}yes{{else}}no{{/not}}", $, "no");
    shouldCompileTo("{{#not 1}}yes{{else}}no{{/not}}", $, "no");
    shouldCompileTo("{{#not false}}yes{{else}}no{{/not}}", $, "yes");
    shouldCompileTo("{{#not 0}}yes{{else}}no{{/not}}", $, "yes");
    shouldCompileTo("{{#not list}}yes{{else}}no{{/not}}", $("hash", $("list", new Object[0])),
        "yes");

    // as expression
    shouldCompileTo("{{not false}}", $, "true");
    shouldCompileTo("{{not false yes='y'}}", $, "y");
    shouldCompileTo("{{not true no='n'}}", $, "n");

    // as subexpression
    shouldCompileTo("{{#if (not false)}}yes{{/if}}", $, "yes");
  }

  @Test
  public void complex() throws Exception {
    shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", $("hash", $("a", true, "b", false, "c", true)), "yes");
    shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", $("hash", $("a", true, "b", true, "c", false)), "yes");
    shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", $("hash", $("a", true, "b", true, "c", true)), "yes");
    shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", $("hash", $("a", true, "b", false, "c", false)), "no");
    shouldCompileTo("{{#if (and a (or b c))}}yes{{else}}no{{/if}}", $("hash", $("a", false, "b", false, "c", true)), "no");
  }

}
