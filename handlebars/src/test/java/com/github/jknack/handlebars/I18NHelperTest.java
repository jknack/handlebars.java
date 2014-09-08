package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.ComparisonFailure;
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
    shouldCompileTo("{{i18n \"escaped\" \"Handlebars.java\"}}", null,
        "Hi, &quot;Handlebars.java&quot;, " +
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
    String expectedJava17 = "<script type='text/javascript'>\n" +
        "  /* Spanish (Argentina) */\n" +
        "  I18n.translations = I18n.translations || {};\n" +
        "  I18n.translations['es_AR'] = {\n" +
        "    \"hello\": \"Hola\",\n" +
        "    \"formatted\": \"Hi {{arg0}}\",\n" +
        "    \"escaped\": \"Hi, &quot;{{arg0}}&quot;, " +
        "a &lt;tag&gt; &#x60;in backticks&#x60; &amp; other &#x27;entities&#x27;\"\n" +
        "  };\n" +
        "</script>\n";

    String result = compile("{{i18nJs \"es_AR\"}}").apply(null);
    try {
      assertEquals(expectedJava17, result);
    } catch (ComparisonFailure ex) {
      String expectedJava18 = "<script type='text/javascript'>\n" +
          "  /* Spanish (Argentina) */\n" +
          "  I18n.translations = I18n.translations || {};\n" +
          "  I18n.translations['es_AR'] = {\n" +
          "    \"hello\": \"Hola\",\n" +
          "    \"escaped\": \"Hi, &quot;{{arg0}}&quot;, a &lt;tag&gt; &#x60;in backticks&#x60; &amp; other &#x27;entities&#x27;\",\n" +
          "    \"formatted\": \"Hi {{arg0}}\"\n" +
          "  };\n" +
          "</script>\n";
      assertEquals(expectedJava18, result);
    }
  }
}
