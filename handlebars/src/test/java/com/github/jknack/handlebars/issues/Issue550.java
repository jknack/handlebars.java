package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue550 extends v4Test {
  @Test
  public void precompileAMDShouldNotAddSuffixToTemplatePartialHash() throws IOException {
    shouldCompileTo("{{precompile 'foo' wrapper='amd'}}{{precompile 'bar' wrapper='amd'}}",
        $("partials", $(
            "foo", "<div>Some repeated pattern {{#each listItem}} {{> bar}} {{/each}} </div>",
            "bar", "<div>{{text}}</div>")),
        "define('foo.hbs', ['handlebars'], function(Handlebars) {\n"
            + "  var template = Handlebars.template({\"1\":function(container,depth0,helpers,partials,data) {\n"
            + "    var stack1;\n"
            + "\n"
            + "  return \" \"\n"
            + "    + ((stack1 = container.invokePartial(partials.bar,depth0,{\"name\":\"bar\",\"data\":data,\"helpers\":helpers,\"partials\":partials,\"decorators\":container.decorators})) != null ? stack1 : \"\")\n"
            + "    + \" \";\n"
            + "},\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var stack1;\n"
            + "\n"
            + "  return \"<div>Some repeated pattern \"\n"
            + "    + ((stack1 = helpers.each.call(depth0 != null ? depth0 : {},(depth0 != null ? depth0.listItem : depth0),{\"name\":\"each\",\"hash\":{},\"fn\":container.program(1, data, 0),\"inverse\":container.noop,\"data\":data})) != null ? stack1 : \"\")\n"
            + "    + \" </div>\";\n"
            + "},\"usePartial\":true,\"useData\":true});\n"
            + "  var templates = Handlebars.templates = Handlebars.templates || {};\n"
            + "  templates['foo'] = template;\n"
            + "  var partials = Handlebars.partials = Handlebars.partials || {};\n"
            + "  partials['foo'] = template;\n"
            + "  return template;\n"
            + "});define('bar.hbs', ['handlebars'], function(Handlebars) {\n"
            + "  var template = Handlebars.template({\"compiler\":[7,\">= 4.0.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var helper;\n"
            + "\n"
            + "  return \"<div>\"\n"
            + "    + container.escapeExpression(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === \"function\" ? helper.call(depth0 != null ? depth0 : {},{\"name\":\"text\",\"hash\":{},\"data\":data}) : helper)))\n"
            + "    + \"</div>\";\n"
            + "},\"useData\":true});\n"
            + "  var templates = Handlebars.templates = Handlebars.templates || {};\n"
            + "  templates['bar'] = template;\n"
            + "  var partials = Handlebars.partials = Handlebars.partials || {};\n"
            + "  partials['bar'] = template;\n"
            + "  return template;\n"
            + "});");
  }
}
