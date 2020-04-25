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
            + "    var stack1, lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return \" \"\n"
            + "    + ((stack1 = container.invokePartial(lookupProperty(partials,\"bar\"),depth0,{\"name\":\"bar\",\"data\":data,\"helpers\":helpers,\"partials\":partials,\"decorators\":container.decorators})) != null ? stack1 : \"\")\n"
            + "    + \" \";\n"
            + "},\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var stack1, lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return \"<div>Some repeated pattern \"\n"
            + "    + ((stack1 = lookupProperty(helpers,\"each\").call(depth0 != null ? depth0 : (container.nullContext || {}),(depth0 != null ? lookupProperty(depth0,\"listItem\") : depth0),{\"name\":\"each\",\"hash\":{},\"fn\":container.program(1, data, 0),\"inverse\":container.noop,\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":27},\"end\":{\"line\":1,\"column\":65}}})) != null ? stack1 : \"\")\n"
            + "    + \" </div>\";\n"
            + "},\"usePartial\":true,\"useData\":true});\n"
            + "  var templates = Handlebars.templates = Handlebars.templates || {};\n"
            + "  templates['foo'] = template;\n"
            + "  var partials = Handlebars.partials = Handlebars.partials || {};\n"
            + "  partials['foo'] = template;\n"
            + "  return template;\n"
            + "});define('bar.hbs', ['handlebars'], function(Handlebars) {\n"
            + "  var template = Handlebars.template({\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data) {\n"
            + "    var helper, lookupProperty = container.lookupProperty || function(parent, propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return \"<div>\"\n"
            + "    + container.escapeExpression(((helper = (helper = lookupProperty(helpers,\"text\") || (depth0 != null ? lookupProperty(depth0,\"text\") : depth0)) != null ? helper : container.hooks.helperMissing),(typeof helper === \"function\" ? helper.call(depth0 != null ? depth0 : (container.nullContext || {}),{\"name\":\"text\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":5},\"end\":{\"line\":1,\"column\":13}}}) : helper)))\n"
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
