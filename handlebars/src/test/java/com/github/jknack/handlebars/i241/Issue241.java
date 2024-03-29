/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i241;

import java.io.IOException;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.StringHelpers;

public class Issue241 extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelper("dateFormat", StringHelpers.dateFormat);
  }

  @Test
  public void formatAsHashInDateFormat() throws IOException {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 1999);
    calendar.set(Calendar.MONTH, 6);
    calendar.set(Calendar.DATE, 16);

    shouldCompileTo(
        "{{dateFormat date format=\"dd-MM-yyyy\"}}", $("date", calendar.getTime()), "16-07-1999");
  }
}
