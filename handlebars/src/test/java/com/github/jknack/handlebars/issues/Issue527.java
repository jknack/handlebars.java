package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue527 extends v4Test {

  @Test
  public void precompileShouldNotCompileTemplate() throws Exception {
    shouldCompileTo("{{precompile \"client_side\"}}",
        $("partials", $("client_side", "{{myhelper \"something\"}}")),
        "(function() {\n"
            + "  var template = Handlebars.template({\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return container.escapeExpression((lookupProperty(helpers,\"myhelper\")||(depth0 && lookupProperty(depth0,\"myhelper\"))||container.hooks.helperMissing).call(depth0 != null ? depth0 : (container.nullContext || {}),\"something\",{\"name\":\"myhelper\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":0},\"end\":{\"line\":1,\"column\":24}}}));\n"
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
