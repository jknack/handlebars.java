package com.github.jknack.handlebars.js;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.FieldValueResolver;

public class JavaScriptHelperTest extends AbstractTest {

  public static class Bean {
    private String name;

    public Bean(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static class ObjectWithPublicFields {
    public String name;

    public ObjectWithPublicFields(final String name) {
      this.name = name;
    }
  }

  @Test
  public void simple() throws Exception {
    eval("{{simple}}", $, "simple", "function() {return 'Long live to Js!';}", "Long live to Js!");
  }

  @Test
  public void mapContext() throws Exception {
    eval("{{map}}", $("name", "moe"), "map", "function(ctx) {return ctx.name;}", "moe");
  }

  @Test
  public void beanContext() throws Exception {
    eval("{{bean}}", new Bean("curly"), "bean", "function() {return this.name;}", "curly");
  }

  @Test
  public void publicFieldsContext() throws Exception {
    Context ctx = Context.newBuilder(new ObjectWithPublicFields("curly"))
        .resolver(FieldValueResolver.INSTANCE)
        .build();
    eval("{{bean}}", ctx, "bean", "function() {return this.name;}", "curly");
  }

  @Test
  public void thisContextAsMap() throws Exception {
    eval("{{map}}", $("name", "larry"), "map", "function() {return this.name;}", "larry");
  }

  @Test
  public void thisContextAsBean() throws Exception {
    eval("{{bean}}", new Bean("curly"), "bean", "function() {return this.name;}", "curly");
  }

  @Test
  public void beanWithParam() throws Exception {
    eval("{{bean this 43}}", new Bean("curly"), "bean",
        "function(curly, age) {return curly.name + ' is ' + age + ' years old';}",
        "curly is 43 years old");
  }

  @Test
  public void params() throws Exception {
    eval("{{params this 1 2 3}}", $, "params",
        "function(context, p1, p2, p3) {return p1 + ', ' + p2 + ', ' + p3;}", "1, 2, 3");
  }

  @Test
  public void hash() throws Exception {
    eval(
        "{{hash this h1=1 h2='2' h3=true}}",
        $,
        "hash",
        "function(context, options) {return options.hash.h1 + ', ' + options.hash.h2 + ', ' + options.hash.h3;}",
        "1, 2, true");
  }

  @Test
  public void fn() throws Exception {
    eval("{{#fn this}}I'm {{name}}!{{/fn}}", new Bean("curly"), "fn",
        "function(context, options) {return options.fn(this);}", "I'm curly!");
  }

  @Test
  public void fnWithNewContext() throws Exception {
    eval("{{#fn this}}I'm {{name}}!{{/fn}}", new Bean("curly"), "fn",
        "function(context, options) {return options.fn({name: 'moe'});}", "I'm moe!");
  }

  @Test
  public void escapeString() throws Exception {
    eval("{{anchor}}", $, "anchor", "function() {return '<a></a>';}", "&lt;a&gt;&lt;/a&gt;");
  }

  @Test
  public void safeString() throws Exception {
    eval("{{anchor}}", $, "anchor", "function() {return new Handlebars.SafeString('<a></a>');}",
        "<a></a>");
  }

  public void eval(final String template, final Context context, final String helper,
      final String helperBody, final String expected) throws Exception {
    Handlebars handlebars = new Handlebars();
    String js = String.format("Handlebars.registerHelper('%s', %s)", helper, helperBody);

    long start = System.currentTimeMillis();
    handlebars.registerHelpers(helper + ".js", js);
    long end = System.currentTimeMillis();
    System.out.printf("execution of:\n  %s\ntook: %sms\n\n", helperBody, end - start);

    assertEquals(expected, handlebars.compileInline(template).apply(context));
  }

  public void eval(final String template, final Object context, final String helper,
      final String helperBody, final String expected) throws Exception {
    eval(template, Context.newContext(context), helper, helperBody, expected);
  }
}
