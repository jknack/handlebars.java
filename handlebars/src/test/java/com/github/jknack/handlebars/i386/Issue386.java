package com.github.jknack.handlebars.i386;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue386 extends AbstractTest {

  @Test
  public void blockHelperShouldNotIntroduceANewContext() throws IOException {
    shouldCompileTo("{{#partial \"body\"}}{{&this}}{{/partial}}{{block \"body\"}}", $("foo", "bar"),
        "{foo&#x3D;bar}");
  }

  @Test
  public void partialShouldNotIntroduceANewContext() throws IOException {
    shouldCompileToWithPartials("{{> partial}}", $("foo", "bar"), $("partial", "{{&this}}"),
        "{foo=bar}");
  }

}
