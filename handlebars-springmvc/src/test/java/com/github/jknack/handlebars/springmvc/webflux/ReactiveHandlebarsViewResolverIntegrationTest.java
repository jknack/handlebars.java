/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc.webflux;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.result.view.View;

import com.github.jknack.handlebars.Handlebars;

/**
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ReactiveHandlebarsApp.class)
public class ReactiveHandlebarsViewResolverIntegrationTest {
  @Autowired
  @Qualifier("viewResolver")
  private ReactiveHandlebarsViewResolver viewResolver;

  @Autowired
  @Qualifier("parameterizedViewResolver")
  private ReactiveHandlebarsViewResolver parameterizedViewResolver;

  @Autowired
  @Qualifier("viewResolverWithoutMessageHelper")
  private ReactiveHandlebarsViewResolver viewResolverWithoutMessageHelper;

  @Test
  public void getHandlebars() {
    assertNotNull(viewResolver);
    assertNotNull(viewResolver.getHandlebars());
  }

  @Test
  public void resolveView() {
    assertNotNull(viewResolver);
    View view = viewResolver.resolveViewName("template", Locale.getDefault())
        .block(Duration.ofSeconds(10));
    assertNotNull(view);
    assertEquals(ReactiveHandlebarsView.class, view.getClass());
  }

  @Test
  public void resolveViewWithParameterized() {
    assertNotNull(parameterizedViewResolver);
    View view = parameterizedViewResolver.resolveViewName("template", Locale.getDefault())
        .block(Duration.ofSeconds(10));
    assertNotNull(view);
    assertEquals(ReactiveHandlebarsView.class, view.getClass());
  }

  @Test
  public void resolveViewWithFallback() {
    try {
      assertNotNull(viewResolver);
      viewResolver.setFailOnMissingFile(false);
      View view = viewResolver.resolveViewName("invalidView", Locale.getDefault())
          .block(Duration.ofSeconds(10));
      assertNull(view);
    } finally {
      viewResolver.setFailOnMissingFile(true);
    }
  }

  @Test
  public void resolveViewWithFallbackParameterized() {
    try {
      assertNotNull(parameterizedViewResolver);
      parameterizedViewResolver.setFailOnMissingFile(false);
      View view = parameterizedViewResolver.resolveViewName("invalidView", Locale.getDefault())
          .block(Duration.ofSeconds(10));
      assertNull(view);
    } finally {
      parameterizedViewResolver.setFailOnMissingFile(true);
    }
  }

  @Test(expected = UncheckedIOException.class)
  public void failToResolve() {
    try {
      assertNotNull(viewResolver);
      viewResolver.setFailOnMissingFile(true);
      viewResolver.resolveViewName("invalidView", Locale.getDefault());
    } finally {
      viewResolver.setFailOnMissingFile(true);
    }
  }

  @Test(expected = UncheckedIOException.class)
  public void failToResolveParameterized() {
    try {
      assertNotNull(parameterizedViewResolver);
      parameterizedViewResolver.setFailOnMissingFile(true);
      parameterizedViewResolver.resolveViewName("invalidView", Locale.getDefault());
    } finally {
      parameterizedViewResolver.setFailOnMissingFile(true);
    }
  }

  @Test(expected = IllegalStateException.class)
  public void getHandlebarsFail() {
    assertNotNull(new ReactiveHandlebarsViewResolver().getHandlebars());
  }

  @Test
  public void messageHelper() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Handlebars Spring MVC!",
        handlebars.compileInline("{{message \"hello\"}}").apply(new Object()));
    assertEquals("Handlebars Spring MVC!",
        handlebars.compileInline("{{i18n \"hello\"}}").apply(new Object()));
  }

  @Test
  public void messageHelperWithParams() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Hello Handlebars!",
        handlebars.compileInline("{{message \"hello.0\" \"Handlebars\"}}").apply(new Object()));
    assertEquals("Hello Handlebars!",
        handlebars.compileInline("{{i18n \"hello.0\" \"Handlebars\"}}").apply(new Object()));

    assertEquals("Hello Spring MVC!",
        handlebars.compileInline("{{message \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
    assertEquals("Hello Spring MVC!",
        handlebars.compileInline("{{i18n \"hello.0\" \"Spring MVC\"}}").apply(new Object()));
  }

  @Test
  public void i18nJs() throws IOException {
    // maven classpath
    String expected = "<script type='text/javascript'>\n" +
        "  /* Spanish (Argentina) */\n" +
        "  I18n.translations = I18n.translations || {};\n" +
        "  I18n.translations['es_AR'] = {\n" +
        "    \"hello\": \"Handlebars Spring MVC!\",\n" +
        "    \"hello.0\": \"Hello {{arg0}}!\"\n" +
        "  };\n" +
        "</script>\n";

    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    String output = handlebars.compileInline("{{i18nJs \"es_AR\"}}").apply(new Object());
    try {
      // maven classpath
      assertEquals(expected, output);
    } catch (ComparisonFailure ex) {
      try {
        // eclipse classpath
        assertEquals("<script type='text/javascript'>\n" +
            "  /* Spanish (Argentina) */\n" +
            "  I18n.translations = I18n.translations || {};\n" +
            "  I18n.translations['es_AR'] = {\n" +
            "    \"hello\": \"Hola\",\n" +
            "    \"hello.0\": \"Hello {{arg0}}!\"\n" +
            "  };\n" +
            "</script>\n", output);
      } catch (ComparisonFailure java18) {
        // java 1.8
        assertEquals("<script type='text/javascript'>\n" +
            "  /* Spanish (Argentina) */\n" +
            "  I18n.translations = I18n.translations || {};\n" +
            "  I18n.translations['es_AR'] = {\n" +
            "    \"hello.0\": \"Hello {{arg0}}!\",\n" +
            "    \"hello\": \"Handlebars Spring MVC!\"\n" +
            "  };\n" +
            "</script>\n", output);
      }
    }
  }

  @Test
  public void messageHelperWithDefaultValue() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("hey",
        handlebars.compileInline("{{message \"hi\" default='hey'}}").apply(new Object()));
  }

  @Test
  public void customHelper() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Spring Helper", handlebars.compileInline("{{spring}}").apply(new Object()));
  }

  @Test
  public void setCustomHelper() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("Spring Helper", handlebars.compileInline("{{setHelper}}").apply(new Object()));
  }

  @Test
  public void helperSource() throws IOException {
    assertNotNull(viewResolver);
    Handlebars handlebars = viewResolver.getHandlebars();
    assertEquals("helper source!", handlebars.compileInline("{{helperSource}}").apply(new Object()));
  }

  @Test
  public void viewResolverWithMessageHelper() {
    assertNotNull(viewResolver);
    assertNotNull(viewResolver.helper("message"));
  }

  @Test
  public void viewResolverWithoutMessageHelper() {
    assertNotNull(viewResolverWithoutMessageHelper);
    Assert.assertNull(viewResolverWithoutMessageHelper.helper("message"));
  }
}
