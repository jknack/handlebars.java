/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.View;

import com.github.jknack.handlebars.Handlebars;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HandlebarsApp.class})
public class HandlebarsViewResolverIntegrationTest {

  @Autowired
  @Qualifier("viewResolver") HandlebarsViewResolver viewResolver;

  @Autowired
  @Qualifier("viewResolverWithoutMessageHelper") HandlebarsViewResolver viewResolverWithoutMessageHelper;

  @Autowired
  @Qualifier("parameterizedHandlebarsViewResolver") HandlebarsViewResolver parameterizedHandlebarsViewResolver;

  @Test
  public void getHandlebars() throws Exception {
    assertNotNull(viewResolver);
    assertNotNull(viewResolver.getHandlebars());
  }

  @Test
  public void resolveView() throws Exception {
    assertNotNull(viewResolver);
    View view = viewResolver.resolveViewName("template", Locale.getDefault());
    assertNotNull(view);
    assertEquals(HandlebarsView.class, view.getClass());
  }

  @Test
  public void resolveViewWithParameterized() throws Exception {
    assertNotNull(parameterizedHandlebarsViewResolver);
    View view =
        parameterizedHandlebarsViewResolver.resolveViewName("template", Locale.getDefault());
    assertNotNull(view);
    assertEquals(HandlebarsView.class, view.getClass());
  }

  @Test
  public void resolveViewWithFallback() throws Exception {
    try {
      assertNotNull(viewResolver);
      viewResolver.setFailOnMissingFile(false);
      View view = viewResolver.resolveViewName("invalidView", Locale.getDefault());
      assertNull(view);
    } finally {
      viewResolver.setFailOnMissingFile(true);
    }
  }

  @Test
  public void resolveViewWithFallbackParameterized() throws Exception {
    try {
      assertNotNull(parameterizedHandlebarsViewResolver);
      parameterizedHandlebarsViewResolver.setFailOnMissingFile(false);
      View view =
          parameterizedHandlebarsViewResolver.resolveViewName("invalidView", Locale.getDefault());
      assertNull(view);
    } finally {
      parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
    }
  }

  @Test
  public void failToResolve() throws Exception {
    assertNotNull(viewResolver);
    try {
      assertThrows(
          IOException.class,
          () -> {
            viewResolver.setFailOnMissingFile(true);
            viewResolver.resolveViewName("invalidView", Locale.getDefault());
          });
    } finally {
      viewResolver.setFailOnMissingFile(true);
    }
  }

  @Test
  public void failToResolveParameterized() throws Exception {
    try {
      assertNotNull(parameterizedHandlebarsViewResolver);
      assertThrows(
          IOException.class,
          () -> {
            parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
            parameterizedHandlebarsViewResolver.resolveViewName("invalidView", Locale.getDefault());
          });
    } finally {
      parameterizedHandlebarsViewResolver.setFailOnMissingFile(true);
    }
  }

  @Test
  public void getHandlebarsFail() throws Exception {
    assertThrows(
        IllegalStateException.class,
        () -> assertNotNull(new HandlebarsViewResolver().getHandlebars()));
  }

  @Test
  public void messageHelper() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals(
        "Handlebars Spring MVC!",
        handlebars.compileInline("{{message \"hello\"}}").apply(new Object()));
    assertEquals(
        "Handlebars Spring MVC!",
        handlebars.compileInline("{{i18n \"hello\"}}").apply(new Object()));
  }

  @Test
  public void messageHelperWithParams() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals(
        "Hello Handlebars!",
        handlebars.compileInline("{{message \"hello.0\" \"Handlebars\"}}").apply(new Object()));
    assertEquals(
        "Hello Handlebars!",
        handlebars.compileInline("{{i18n \"hello.0\" \"Handlebars\"}}").apply(new Object()));

    assertEquals(
        "Hello Spring MVC!",
        handlebars.compileInline("{{message \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
    assertEquals(
        "Hello Spring MVC!",
        handlebars.compileInline("{{i18n \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
  }

  @Test
  public void i18nJs() throws Exception {
    // maven classpath
    String expected =
        "<script type='text/javascript'>\n"
            + "  /* Spanish (Argentina) */\n"
            + "  I18n.translations = I18n.translations || {};\n"
            + "  I18n.translations['es_AR'] = {\n"
            + "    \"hello\": \"Handlebars Spring MVC!\",\n"
            + "    \"hello.0\": \"Hello {{arg0}}!\"\n"
            + "  };\n"
            + "</script>\n";

    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    String output = handlebars.compileInline("{{i18nJs \"es_AR\"}}").apply(new Object());
    try {
      // maven classpath
      assertEquals(expected, output);
    } catch (AssertionFailedError ex) {
      try {
        // eclipse classpath
        assertEquals(
            "<script type='text/javascript'>\n"
                + "  /* Spanish (Argentina) */\n"
                + "  I18n.translations = I18n.translations || {};\n"
                + "  I18n.translations['es_AR'] = {\n"
                + "    \"hello\": \"Hola\",\n"
                + "    \"hello.0\": \"Hello {{arg0}}!\"\n"
                + "  };\n"
                + "</script>\n",
            output);
      } catch (AssertionFailedError java18) {
        // java 1.8
        assertEquals(
            "<script type='text/javascript'>\n"
                + "  /* Spanish (Argentina) */\n"
                + "  I18n.translations = I18n.translations || {};\n"
                + "  I18n.translations['es_AR'] = {\n"
                + "    \"hello.0\": \"Hello {{arg0}}!\",\n"
                + "    \"hello\": \"Handlebars Spring MVC!\"\n"
                + "  };\n"
                + "</script>\n",
            output);
      }
    }
  }

  @Test
  public void messageHelperWithDefaultValue() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals(
        "hey", handlebars.compileInline("{{message \"hi\" default='hey'}}").apply(new Object()));
  }

  @Test
  public void customHelper() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Spring Helper", handlebars.compileInline("{{spring}}").apply(new Object()));
  }

  @Test
  public void setCustomHelper() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Spring Helper", handlebars.compileInline("{{setHelper}}").apply(new Object()));
  }

  @Test
  public void helperSource() throws Exception {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals(
        "helper source!", handlebars.compileInline("{{helperSource}}").apply(new Object()));
  }

  @Test
  public void viewResolverWithMessageHelper() throws Exception {
    assertNotNull(viewResolver);
    assertNotNull(viewResolver.helper("message"));
  }

  @Test
  public void viewResolverWithoutMessageHelper() throws Exception {
    assertNotNull(viewResolverWithoutMessageHelper);
    assertNull(viewResolverWithoutMessageHelper.helper("message"));
  }
}
