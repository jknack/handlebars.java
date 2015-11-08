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
        "{\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n" +
        "    var helper;\n" +
        "\n" +
        "  return \"Hi \"\n" +
        "    + container.escapeExpression(((helper = (helper = helpers[\"var\"] || (depth0 != null ? depth0[\"var\"] : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === \"function\" ? helper.call(depth0 != null ? depth0 : {},{\"name\":\"var\",\"hash\":{},\"data\":data}) : helper)))\n" +
        "    + \"!\";\n" +
        "},\"useData\":true}", new Handlebars().handlebarsJsFile("/handlebars-v4.0.4.js")
            .compileInline("Hi {{var}}!").toJavaScript());
  }

}
