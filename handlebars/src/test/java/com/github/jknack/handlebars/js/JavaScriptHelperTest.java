package com.github.jknack.handlebars.js;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;

public class JavaScriptHelperTest extends AbstractTest {

  private static Handlebars handlebars = new Handlebars();

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

  @BeforeClass
  public static void registerHelpers() throws Exception {
    long start = System.currentTimeMillis();
    handlebars
        .registerHelpers(new File("src/test/resources/com/github/jknack/handlebars/js/helpers.js"));
    long end = System.currentTimeMillis();
    System.out.printf("Handlebars.registerHelpers took: %sms\n", end - start);
  }

  @Test
  public void simple() throws Exception {
    eval("{{simple}}", $, "Long live to Js!");
  }

  @Test
  public void mapContext() throws Exception {
    eval("{{context this}}", $("name", "moe"), "moe");
  }

  @Test
  public void beanContext() throws Exception {
    eval("{{context this}}", new Bean("curly"), "curly");
  }

  @Test
  public void publicFieldsContext() throws Exception {
    Context ctx = Context.newBuilder(new ObjectWithPublicFields("curly"))
        .resolver(FieldValueResolver.INSTANCE)
        .build();
    eval("{{context this}}", ctx, "curly");
  }

  @Test
  public void thisContextAsMap() throws Exception {
    eval("{{thisContext}}", $("name", "larry"), "larry");
  }

  @Test
  public void thisContextAsBean() throws Exception {
    eval("{{thisContext}}", new Bean("curly"), "curly");
  }

  @Test
  public void beanWithParam() throws Exception {
    eval("{{param1 this 32}}", new Bean("edgar"), "edgar is 32 years old");
  }

  @Test
  public void params() throws Exception {
    eval("{{params this 1 2 3}}", $, "1, 2, 3");
  }

  @Test
  public void hash() throws Exception {
    eval("{{hash this h1=1 h2='2' h3=true}}", $, "1, 2, true");
  }

  @Test
  public void fn() throws Exception {
    eval("{{#fn this}}I'm {{name}}!{{/fn}}", new Bean("curly"), "I'm curly!");
  }

  @Test
  public void fnWithNewContext() throws Exception {
    eval("{{#fnWithNewContext this}}I'm {{name}}!{{/fnWithNewContext}}", new Bean("curly"),
        "I'm moe!");
  }

  @Test
  public void escapeString() throws Exception {
    eval("{{escapeString}}", $, "&lt;a&gt;&lt;/a&gt;");
  }

  @Test
  public void safeString() throws Exception {
    eval("{{safeString}}", $, "<a></a>");
  }

  @Test
  public void helper_with_complex_lookup$() throws Exception {
    eval("{{#goodbyes}}{{{link ../prefix}}}{{/goodbyes}}",
        $("prefix", "/root", "goodbyes", new Object[]{$("text", "Goodbye", "url", "goodbye") }),
        "<a href='/root/goodbye'>Goodbye</a>");
  }

  @Test
  public void helper_block_with_complex_lookup_expression() throws Exception {
    eval("{{#goodbyes2}}{{../name}}{{/goodbyes2}}", $("name", "Alan"),
        "Goodbye Alan! goodbye Alan! GOODBYE Alan! ");
  }

  @Test
  public void helper_with_complex_lookup_and_nested_template() throws Exception {
    eval("{{#goodbyes}}{{#link2 ../prefix}}{{text}}{{/link2}}{{/goodbyes}}",
        $("prefix", "/root", "goodbyes", new Object[]{$("text", "Goodbye", "url", "goodbye") }),
        "<a href='/root/goodbye'>Goodbye</a>");
  }

  @Test
  public void block_helper() throws Exception {
    eval("{{#goodbyes3}}{{text}}! {{/goodbyes3}}cruel {{world}}!", $("world", "world"),
        "GOODBYE! cruel world!");
  }

  @Test
  public void block_helper_staying_in_the_same_context() throws Exception {
    eval("{{#form}}<p>{{name}}</p>{{/form}}", $("name", "Yehuda"), "<form><p>Yehuda</p></form>");
  }

  @Test
  public void block_helper_should_have_context_in_this() throws Exception {
    eval("<ul>{{#people}}<li>{{#link3}}{{name}}{{/link3}}</li>{{/people}}</ul>",
        $("people", new Object[]{
            $("name", "Alan", "id", 1),
            $("name", "Yehuda", "id", 2)
        }),
        "<ul><li><a href=\"/people/1\">Alan</a></li><li><a href=\"/people/2\">Yehuda</a></li></ul>");
  }

  @Test
  public void block_helper_for_undefined_value() throws Exception {
    eval("{{#empty2}}shouldn't render{{/empty2}}", $, "");
  }

  @Test
  public void block_helper_passing_a_new_context() throws Exception {
    eval("{{#form2 yehuda}}<p>{{name}}</p>{{/form2}}",
        $("yehuda", $("name", "Yehuda")),
        "<form><p>Yehuda</p></form>");
  }

  @Test
  public void block_helper_inverted_sections() throws Exception {
    eval("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}",
        $("people", new Object[]{$("name", "Alan"), $("name", "Yehuda") }),
        "<ul><li>Alan</li><li>Yehuda</li></ul>");

    eval("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}",
        $("people", new Object[0]),
        "<p><em>Nobody's here</em></p>");

    eval("{{#list people}}Hello{{^}}{{message}}{{/list}}",
        $("people", new Object[0], "message", "Nobody's here"),
        "<p>Nobody&#x27;s here</p>");
  }

  public static void eval(final String template, final Context context, final String expected)
      throws Exception {
    Template t = handlebars.compileInline(template);
    assertEquals(expected, t.apply(context));
  }

  public void eval(final String template, final Object context, final String expected)
      throws Exception {
    eval(template, Context.newContext(context), expected);
  }
}
