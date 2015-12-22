package com.github.jknack.handlebars.i430;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;

public class Issue430 extends v4Test {
  @Test
  public void shouldEscapeEqualsSignInHtml() throws IOException {
    assertEquals("foo&#x3D;", Handlebars.Utils.escapeExpression("foo=").toString());
  }
}
