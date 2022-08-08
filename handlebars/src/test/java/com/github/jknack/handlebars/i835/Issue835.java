package com.github.jknack.handlebars.i835;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.v4Test;

public class Issue835 extends v4Test {

  @Override protected void configure(Handlebars handlebars) {
    handlebars.with(
        new ClassPathTemplateLoader("/" + getClass().getPackage().getName().replace(".", "/")));
  }

  @Test
  public void shouldIgnoreZeroSizeFileTemplate() throws IOException {
    assertEquals(0, Files.size(
        Paths.get("src", "test", "resources", "com", "github", "jknack", "handlebars", "i835",
            "i835.hbs")));
    shouldCompileTo("-{{> i835}}-", $(), "--");
  }

  @Test
  public void shouldIgnoreEmptyTemplate() throws IOException {
    shouldCompileTo("", $(), "");
  }

}
