package com.github.jknack.handlebars;

import java.io.IOException;

import org.junit.Test;

public class I18NHelperTest extends AbstractTest {

  @Test
  public void defaultI18N() throws IOException {
    shouldCompileTo("{{i18n \"hello\"}} Handlebars.java!", $, "Hi Handlebars.java!");
  }

  @Test
  public void customLocale() throws IOException {
    shouldCompileTo("{{i18n \"hello\" locale=\"es_AR\"}} Handlebars.java!", $,
        "Hola Handlebars.java!");
  }

  @Test
  public void formattedMsg() throws IOException {
    shouldCompileTo("{{i18n \"formatted\" \"Handlebars.java\"}}!", null, "Hi Handlebars.java!");
  }

  @Test
  public void escapeQuotes() throws IOException {
    shouldCompileTo("{{i18n \"escaped\" \"Handlebars.java\"}}", null, "Hi, &quot;Handlebars.java&quot;, " +
        "a &lt;tag&gt; &#x60;in backticks&#x60; &amp; other entities");
  }

  @Test(expected = HandlebarsException.class)
  public void missingKeyError() throws IOException {
    shouldCompileTo("{{i18n \"missing\"}}", null, "error");
  }

  @Test
  public void setCustomLocale() throws IOException {
    shouldCompileTo("{{i18n \"hello\" bundle=\"myMessages\" locale=\"es_AR\"}}", null, "Hola");
  }

  @Test(expected = HandlebarsException.class)
  public void missingBundle() throws IOException {
    shouldCompileTo("{{i18n \"key\" bundle=\"missing\"}}!", null, "");
  }

  @Test
  public void defaultI18nJs() throws IOException {
    String expected = "<script type='text/javascript'>\n" +
        "  /* Spanish (Argentina) */\n" +
        "  I18n.translations = I18n.translations || {};\n" +
        "  I18n.translations['es_AR'] = {\n" +
        "    \"hello\": \"Hola\",\n" +
        "    \"formatted\": \"Hi {{arg0}}\",\n" +
        "    \"escaped\": \"Hi, &quot;{{arg0}}&quot;, " +
        "a &lt;tag&gt; &#x60;in backticks&#x60; &amp; other &#x27;entities&#x27;\"\n" +
        "  };\n" +
        "</script>\n";

    shouldCompileTo("{{i18nJs \"es_AR\"}}", null, expected);
  }
}
