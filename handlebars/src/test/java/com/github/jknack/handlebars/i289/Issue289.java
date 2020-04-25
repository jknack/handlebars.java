package com.github.jknack.handlebars.i289;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue289 extends AbstractTest {

  private Hash helpers = $("lowercase", new Helper<Object>() {
    @Override
    public Object apply(final Object context, final Options options) throws IOException {
      return context.toString().toLowerCase();
    }
  }, "concat", new Helper<Object>() {
    @Override
    public Object apply(final Object context, final Options options) throws IOException {
      return options.param(0).toString() + options.param(1).toString();
    }
  });

  @Test
  public void subexpression() throws IOException {
    assertEquals("{{lowercase (concat \"string1\" \"string2\")}}",
        compile("{{lowercase (concat \"string1\" \"string2\")}}", helpers).text());
  }

  @Test
  public void subexpressionToJS() throws IOException {
    String js = "{\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
        + "    var lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
        + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
        + "          return parent[propertyName];\n"
        + "        }\n"
        + "        return undefined\n"
        + "    };\n"
        + "\n"
        + "  return container.escapeExpression((lookupProperty(helpers,\"lowercase\")||(depth0 && lookupProperty(depth0,\"lowercase\"))||container.hooks.helperMissing).call(depth0 != null ? depth0 : (container.nullContext || {}),(lookupProperty(helpers,\"concat\")||(depth0 && lookupProperty(depth0,\"concat\"))||container.hooks.helperMissing).call(depth0 != null ? depth0 : (container.nullContext || {}),\"string1\",\"string2\",{\"name\":\"concat\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":12},\"end\":{\"line\":1,\"column\":40}}}),{\"name\":\"lowercase\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":0},\"end\":{\"line\":1,\"column\":42}}}));\n"
        + "},\"useData\":true}";

    assertEquals(js,
        compile("{{lowercase (concat \"string1\" \"string2\")}}", helpers).toJavaScript());
  }

}
