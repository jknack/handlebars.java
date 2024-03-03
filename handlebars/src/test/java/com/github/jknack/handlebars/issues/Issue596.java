/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue596 extends v4Test {

  @Test
  public void shouldSupportNoneCharSequenceReturnsTypeFromHelperClass() throws Exception {
    String text = compile("{{> partial root=this name=\"Han\"}}").text();
    assertEquals("{{>partial root=this name=\"Han\"}}", text);
  }
}
