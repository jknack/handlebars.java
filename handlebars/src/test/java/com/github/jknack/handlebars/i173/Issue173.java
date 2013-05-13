package com.github.jknack.handlebars.i173;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class Issue173 extends AbstractTest {

  @Test
  public void issue173() throws IOException {
    String prefix = "/" + Issue173.class.getPackage().getName().replace(".", "/");

    Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader(prefix))
      .prettyPrint(true);

    Template template = handlebars.compile("child");
    assertNotNull(template);

    assertEquals("...", template.apply($("isPage", true)));
  }
}
