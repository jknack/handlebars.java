package com.github.jknack.handlebars.i417;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue417 extends AbstractTest {

  @Test
  public void v4_0_0() throws IOException {
    assertEquals(
        "{\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var helper, lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return \"Hi \"\n"
            + "    + container.escapeExpression(((helper = (helper = lookupProperty(helpers,\"var\") || (depth0 != null ? lookupProperty(depth0,\"var\") : depth0)) != null ? helper : container.hooks.helperMissing),(typeof helper === \"function\" ? helper.call(depth0 != null ? depth0 : (container.nullContext || {}),{\"name\":\"var\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":3},\"end\":{\"line\":1,\"column\":10}}}) : helper)))\n"
            + "    + \"!\";\n"
            + "},\"useData\":true}", new Handlebars().handlebarsJsFile("/handlebars-v4.7.7.js")
            .compileInline("Hi {{var}}!").toJavaScript());
  }

}
