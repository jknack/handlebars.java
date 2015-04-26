package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class Issue322 extends AbstractTest {

  @Test
  public void defaults() throws IOException {
    assertEquals(
        "{\"compiler\":[6,\">= 2.0.0-beta.1\"],\"main\":function(depth0,helpers,partials,data) {\n"
            +
            "  var helper, functionType=\"function\", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;\n"
            +
            "  return \"Hi \"\n"
            +
            "    + escapeExpression(((helper = (helper = helpers['var'] || (depth0 != null ? depth0['var'] : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {\"name\":\"var\",\"hash\":{},\"data\":data}) : helper)))\n"
            +
            "    + \"!\";\n" +
            "},\"useData\":true}", compile("Hi {{var}}!").toJavaScript());
  }

  @Test
  public void v_1_3_0() throws IOException {
    assertEquals(
        "function (Handlebars,depth0,helpers,partials,data) {\n"
            +
            "  this.compilerInfo = [4,'>= 1.0.0'];\n"
            +
            "helpers = this.merge(helpers, Handlebars.helpers); data = data || {};\n"
            +
            "  var buffer = \"\", stack1, helper, functionType=\"function\", escapeExpression=this.escapeExpression;\n"
            +
            "\n"
            +
            "\n"
            +
            "  buffer += \"Hi \";\n"
            +
            "  if (helper = helpers['var']) { stack1 = helper.call(depth0, {hash:{},data:data}); }\n"
            +
            "  else { helper = (depth0 && depth0['var']); stack1 = typeof helper === functionType ? helper.call(depth0, {hash:{},data:data}) : helper; }\n"
            +
            "  buffer += escapeExpression(stack1)\n" +
            "    + \"!\";\n" +
            "  return buffer;\n" +
            "  }", new Handlebars().handlebarsJsFile("/handlebars-v1.3.0.js").compileInline("Hi {{var}}!").toJavaScript());
  }

  @Test
  public void v_2_0_0() throws IOException {
    assertEquals(
        "{\"compiler\":[6,\">= 2.0.0-beta.1\"],\"main\":function(depth0,helpers,partials,data) {\n"
            +
            "  var helper, functionType=\"function\", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;\n"
            +
            "  return \"Hi \"\n"
            +
            "    + escapeExpression(((helper = (helper = helpers['var'] || (depth0 != null ? depth0['var'] : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {\"name\":\"var\",\"hash\":{},\"data\":data}) : helper)))\n"
            +
            "    + \"!\";\n" +
            "},\"useData\":true}", new Handlebars().handlebarsJsFile("/handlebars-v2.0.0.js")
            .compileInline("Hi {{var}}!").toJavaScript());
  }

  @Test(expected = IllegalArgumentException.class)
  public void notFound() throws IOException {
    new Handlebars().handlebarsJsFile("/handlebars-not-found.js").compileInline("Hi {{var}}!")
        .toJavaScript();
  }
}
