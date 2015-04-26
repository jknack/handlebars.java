package com.github.jknack.handlebars.i289;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue289 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.handlebarsJsFile("/handlebars-v1.3.0.js");
  }


  private Hash helpers = $("lowercase", new Helper<Object>() {
    @Override
    public CharSequence apply(final Object context, final Options options) throws IOException {
      return context.toString().toLowerCase();
    }
  }, "concat", new Helper<Object>() {
    @Override
    public CharSequence apply(final Object context, final Options options) throws IOException {
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
    String js = "function (Handlebars,depth0,helpers,partials,data) {\n"
        + "  this.compilerInfo = [4,'>= 1.0.0'];\n"
        + "helpers = this.merge(helpers, Handlebars.helpers); data = data || {};\n"
        + "  var stack1, helper, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;\n"
        + "\n"
        + "\n"
        + "  stack1 = (helper = helpers.concat || (depth0 && depth0.concat),options={hash:{},data:data},helper ? helper.call(depth0, \"string1\", \"string2\", options) : helperMissing.call(depth0, \"concat\", \"string1\", \"string2\", options));\n"
        + "  return escapeExpression((helper = helpers.lowercase || (depth0 && depth0.lowercase),options={hash:{},data:data},helper ? helper.call(depth0, stack1, options) : helperMissing.call(depth0, \"lowercase\", stack1, options)));\n"
        + "  }";

    assertEquals(js,
        compile("{{lowercase (concat \"string1\" \"string2\")}}", helpers).toJavaScript());
  }

}
