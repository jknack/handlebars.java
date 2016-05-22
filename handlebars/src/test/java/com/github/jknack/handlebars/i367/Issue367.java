package com.github.jknack.handlebars.i367;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class Issue367 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelperMissing(new Helper<Object>() {

      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return "nousers";
      }

    });
  }

  @Test
  public void missingHelperOnVariables() throws IOException {
    shouldCompileTo(
        "{{#userss}} <tr> <td>{{fullName}}</td> <td>{{jobTitle}}</td> </tr> {{/userss}}", $, "nousers");
  }

}
