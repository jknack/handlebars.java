/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i213;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue213 extends AbstractTest {

  @Test
  public void args() throws IOException {
    shouldCompileTo(
        "{{i18nJs bundle=\"args\" wrap=false}}",
        null,
        "  /* English (United States) */\n"
            + "  I18n.translations = I18n.translations || {};\n"
            + "  I18n.translations['en_US'] = {\n"
            + "    \"arg3\": \"{{arg0}}, {{arg1}}, {{arg2}}\",\n"
            + "    \"arg2\": \"{{arg0}}, {{arg1}}\",\n"
            + "    \"arg1\": \"{{arg0}}\"\n"
            + "  };\n");
  }
}
