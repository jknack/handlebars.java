package com.github.jknack.handlebars;

import com.github.jknack.handlebars.js.JavaScriptHelperTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NashornTest {

  public static class ObjectWithPublicFields {
    public String name;

    public ObjectWithPublicFields(final String name) {
      this.name = name;
    }
  }

  public static class Bean {
    private String name;

    public Bean(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Test
  public void bootstrap() throws Exception {
    Handlebars hbs = new Handlebars();
    ScriptEngine nashorn =
        new ScriptEngineManager().getEngineByName("nashorn");
    SimpleBindings bindings = new SimpleBindings();
    bindings.put("Handlebars_java", hbs);
    nashorn.eval(new FileReader(Paths.get("src/main/resources/helpers.nashorn.js").toFile()),
        bindings);

    nashorn.eval(new FileReader(
            Paths.get("src/test/resources/com/github/jknack/handlebars/js/helpers.js").toFile()),
        bindings);

    assertEquals("Long live to Js!", hbs.compileInline("{{simple}}").apply(null));

    assertEquals("JS1",
        hbs.compileInline("{{context this}}").apply(hash("name", "JS1")));

    assertEquals("JS2",
        hbs.compileInline("{{context this}}").apply(new Bean("JS2")));

    assertEquals("JS3",
        hbs.compileInline("{{context this}}").apply(new ObjectWithPublicFields("JS3")));

    assertEquals("JS4",
        hbs.compileInline("{{thisContext}}").apply(hash("name", "JS4")));

    assertEquals("JS5",
        hbs.compileInline("{{thisContext}}").apply(new Bean("JS5")));

    assertEquals("edgar is 32 years old",
        hbs.compileInline("{{param1 this 32}}").apply(new Bean("edgar")));

    assertEquals("1, 2, 3",
        hbs.compileInline("{{params this 1 2 3}}").apply(null));

    assertEquals("1, 2, true",
        hbs.compileInline("{{hash this h1=1 h2='2' h3=true}}").apply(null));

    assertEquals("I'm curly!",
        hbs.compileInline("{{#fn this}}I'm {{name}}!{{/fn}}")
            .apply(new JavaScriptHelperTest.Bean("curly")));

    assertEquals("I'm moe!",
        hbs.compileInline("{{#fnWithNewContext this}}I'm {{name}}!{{/fnWithNewContext}}")
            .apply(new JavaScriptHelperTest.Bean("curly")));

    assertEquals("&lt;a&gt;&lt;/a&gt;",
        hbs.compileInline("{{escapeString}}").apply(null));

    assertEquals("<a></a>",
        hbs.compileInline("{{safeString}}").apply(null));

    assertEquals("<a href='/root/goodbye'>Goodbye</a>",
        hbs.compileInline("{{#goodbyes}}{{{link ../prefix}}}{{/goodbyes}}").apply(
            hash("prefix", "/root", "goodbyes",
                new Object[]{hash("text", "Goodbye", "url", "goodbye")})));

    assertEquals("Goodbye Alan! goodbye Alan! GOODBYE Alan! ",
        hbs.compileInline("{{#goodbyes2}}{{name}}{{/goodbyes2}}").apply(hash("name", "Alan")));

    assertEquals("Goodbye Alan! goodbye Alan! GOODBYE Alan! ",
        hbs.compileInline("{{#goodbyes4}}{{../name}}{{/goodbyes4}}").apply(hash("name", "Alan")));

    assertEquals("<a href='/root/goodbye'>Goodbye</a>",
        hbs.compileInline("{{#goodbyes}}{{#link2 ../prefix}}{{text}}{{/link2}}{{/goodbyes}}").apply(
            hash("prefix", "/root", "goodbyes",
                new Object[]{hash("text", "Goodbye", "url", "goodbye")})));

    assertEquals("GOODBYE! cruel world!",
        hbs.compileInline("{{#goodbyes3}}{{text}}! {{/goodbyes3}}cruel {{world}}!")
            .apply(hash("world", "world")));

    assertEquals("<form><p>Yehuda</p></form>",
        hbs.compileInline("{{#form}}<p>{{name}}</p>{{/form}}").apply(hash("name", "Yehuda")));

    assertEquals(
        "<ul><li><a href=\"/people/1\">Alan</a></li><li><a href=\"/people/2\">Yehuda</a></li></ul>",
        hbs.compileInline("<ul>{{#people}}<li>{{#link3}}{{name}}{{/link3}}</li>{{/people}}</ul>")
            .apply(hash("people", new Object[]{
                hash("name", "Alan", "id", 1),
                hash("name", "Yehuda", "id", 2)
            })));

    assertEquals("", hbs.compileInline("{{#empty2}}shouldn't render{{/empty2}}").apply(null));

    assertEquals("<form><p>Yehuda</p></form>",
        hbs.compileInline("{{#form2 yehuda}}<p>{{name}}</p>{{/form2}}")
            .apply(hash("yehuda", hash("name", "Yehuda"))));

    assertEquals("<ul><li>Alan</li><li>Yehuda</li></ul>",
        hbs.compileInline("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}")
            .apply(hash("people", new Object[]{hash("name", "Alan"), hash("name", "Yehuda") })));

    assertEquals("<p><em>Nobody's here</em></p>",
        hbs.compileInline("{{#list people}}{{name}}{{^}}<em>Nobody's here</em>{{/list}}")
            .apply(hash("people", new Object[0])));

    assertEquals("<p>Nobody&#x27;s here</p>",
        hbs.compileInline("{{#list people}}Hello{{^}}{{message}}{{/list}}")
            .apply(hash("people", new Object[0], "message", "Nobody's here")));
  }

  private Map<String, Object> hash(Object... values) {
    Map<String, Object> hash = new HashMap<>();
    for (int i = 0; i < values.length; i += 2) {
      hash.put(values[i].toString(), values[i + 1]);
    }
    return hash;
  }
}
