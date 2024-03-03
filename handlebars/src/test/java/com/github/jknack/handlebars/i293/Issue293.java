/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.i293;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.helper.I18nHelper;

public class Issue293 extends AbstractTest {

  @BeforeEach
  public void overrideDefaults() {
    I18nHelper.i18n.setDefaultBundle("i293");
    I18nHelper.i18n.setDefaultLocale(LocaleUtils.toLocale("es_AR"));
  }

  @AfterEach
  public void defaults() {
    I18nHelper.i18n.setDefaultBundle("messages");
    I18nHelper.i18n.setDefaultLocale(Locale.getDefault());
  }

  @Test
  public void defaultI18N() throws IOException {
    shouldCompileTo("{{i18n \"hello\"}}", $, "i293 AR");
  }
}
