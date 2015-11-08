package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Issue322 extends AbstractTest {

  @Test
  public void defaults() throws IOException {
    assertEquals(
        "{\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n" +
        "    var helper;\n" +
        "\n" +
        "  return \"Hi \"\n" +
        "    + container.escapeExpression(((helper = (helper = helpers[\"var\"] || (depth0 != null ? depth0[\"var\"] : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === \"function\" ? helper.call(depth0 != null ? depth0 : {},{\"name\":\"var\",\"hash\":{},\"data\":data}) : helper)))\n" +
        "    + \"!\";\n" +
        "},\"useData\":true}", compile("Hi {{var}}!").toJavaScript());
  }

  @Test(expected = IllegalArgumentException.class)
  public void notFound() throws IOException {
    new Handlebars().handlebarsJsFile("/handlebars-not-found.js").compileInline("Hi {{var}}!")
        .toJavaScript();
  }
}
