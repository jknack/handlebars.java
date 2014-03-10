package com.github.jknack.handlebars.i284;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class Issue284 extends AbstractTest {

  @Test
  public void compileWithoutRhino() throws Exception {
    assertNotNull(compile("{{var}}"));
  }

  @Test(expected = NoClassDefFoundError.class)
  public void mustFailWhenCallToJavaScript() throws Exception {
    compile("{{var}}").toJavaScript();
  }

  @Test(expected = IllegalStateException.class)
  public void mustFailWhenUsingJsHelpers() throws Exception {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelpers("demo.js",
        "Handlebars.registerHelper('hey', function() {return 'xx';});");
  }
}
