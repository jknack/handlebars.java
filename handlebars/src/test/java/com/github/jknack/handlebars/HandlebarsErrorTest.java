/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HandlebarsErrorTest {

  @Test
  public void properties() {
    HandlebarsError hbsError =
        new HandlebarsError("filename", 1, 3, "reason", "evidence", "message");
    assertEquals("filename", hbsError.filename);
    assertEquals(1, hbsError.line);
    assertEquals(3, hbsError.column);
    assertEquals("reason", hbsError.reason);
    assertEquals("evidence", hbsError.evidence);
    assertEquals("message", hbsError.message);
  }
}
