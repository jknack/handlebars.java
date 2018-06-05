package com.github.jknack.handlebars;

import com.github.jknack.handlebars.helper.I18nHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

public class Issue488 extends AbstractTest {

  @Before
  public void overrideDefaults() {
    I18nHelper.i18n.setDefaultBundle("i488");
    I18nHelper.i18n.setDefaultLocale(Locale.ENGLISH);
  }

  @After
  public void defaults() {
    I18nHelper.i18n.setDefaultBundle("messages");
    I18nHelper.i18n.setDefaultLocale(Locale.getDefault());
  }

  @Test
  public void utf8() throws IOException {
    shouldCompileTo("{{i18n \"utf8\"}}", $, "Bonjour à tous.");
  }


}
