package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Issue527 extends v4Test {

  @Test
  public void precompileShouldNotCompileTemplate() throws Exception {
    shouldCompileTo("{{precompile \"client_side\"}}",
        $("partials", $("client_side", "{{myhelper \"something\"}}")),
        "(function() {\n"
            + "  var template = Handlebars.template({\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    return container.escapeExpression((helpers.myhelper || (depth0 && depth0.myhelper) || helpers.helperMissing).call(depth0 != null ? depth0 : {},\"something\",{\"name\":\"myhelper\",\"hash\":{},\"data\":data}));\n"
            + "},\"useData\":true});\n"
            + "  var templates = Handlebars.templates = Handlebars.templates || {};\n"
            + "  templates['client_side'] = template;\n"
            + "  var partials = Handlebars.partials = Handlebars.partials || {};\n"
            + "  partials['client_side'] = template;\n"
            + "})();");
  }

  @Test
  public void embeddedShouldNotCompileTemplate() throws Exception {
    shouldCompileTo("{{embedded \"client_side\"}}",
        $("partials", $("client_side", "{{myhelper \"something\"}}")),
        "<script id=\"client_side-hbs\" type=\"text/x-handlebars\">\n"
            + "{{myhelper \"something\"}}\n"
            + "</script>");
  }
}
