package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue500 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    try {
      handlebars
          .registerHelpers("500.js",
              "Handlebars.registerHelper(\"chunk\",function(arr,size,options){\n" +
                  "\n" +
                  "var t = JSON.stringify(arr);\n" +
                  "var temp = JSON.parse(t);\n" +
                  "\n" +
                  "\n" +
                  "var newArr=[]\n" +
                  "\n" +
                  "while(temp.length) {\n" +
                  "    newArr.push(temp.splice(0,size));\n" +
                  "}\n" +
                  "\n" +
                  "return newArr;\n" +
                  "});");
    } catch (Exception ex) {
      Assert.fail(ex.getMessage());
    }
  }

  @Test
  public void shouldEachHelperIterateOverRhinoNativeArrays() throws IOException {
    shouldCompileTo("{{#each (chunk array 2) }}{{this}}{{/each}}",
        $("hash", $("array", new String[]{"a", "b", "c", "d" })), "a,bc,d");
  }

}
