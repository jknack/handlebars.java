/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class Issue322 extends AbstractTest {

  @Test
  public void defaults() throws IOException {
    assertEquals(
        "{\"compiler\":[8,\">= 4.3.0\"],\"main\":function(container,depth0,helpers,partials,data)"
            + " {\n"
            + "    var helper, lookupProperty = container.lookupProperty || function(parent,"
            + " propertyName) {\n"
            + "        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {\n"
            + "          return parent[propertyName];\n"
            + "        }\n"
            + "        return undefined\n"
            + "    };\n"
            + "\n"
            + "  return \"Hi \"\n"
            + "    + container.escapeExpression(((helper = (helper ="
            + " lookupProperty(helpers,\"var\") || (depth0 != null ? lookupProperty(depth0,\"var\")"
            + " : depth0)) != null ? helper : container.hooks.helperMissing),(typeof helper ==="
            + " \"function\" ? helper.call(depth0 != null ? depth0 : (container.nullContext ||"
            + " {}),{\"name\":\"var\",\"hash\":{},\"data\":data,\"loc\":{\"start\":{\"line\":1,\"column\":3},\"end\":{\"line\":1,\"column\":10}}})"
            + " : helper)))\n"
            + "    + \"!\";\n"
            + "},\"useData\":true}",
        compile("Hi {{var}}!").toJavaScript());
  }

  @Test
  public void notFound() throws IOException {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Handlebars()
                .handlebarsJsFile("/handlebars-not-found.js")
                .compileInline("Hi {{var}}!")
                .toJavaScript());
  }
}
