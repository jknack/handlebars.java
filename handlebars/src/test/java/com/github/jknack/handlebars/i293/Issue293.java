package com.github.jknack.handlebars.i293;

import java.io.IOException;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.helper.I18nHelper;

public class Issue293 extends AbstractTest {

  @BeforeClass
  public static void overrideBundleName() {
    I18nHelper.i18n.setDefaultBundle("i293");
    I18nHelper.i18n.setDefaultLocale(LocaleUtils.toLocale("es_AR"));
  }

  @Test
  public void defaultI18N() throws IOException {
    shouldCompileTo("{{i18n \"hello\"}}", $, "i293 AR");
  }


}
