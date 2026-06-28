/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class HandlebarsViewResolverTest {

  private HandlebarsViewResolver viewResolver;
  private Handlebars handlebars;

  @BeforeEach
  public void setUp() {
    handlebars = mock(Handlebars.class);
    viewResolver = new HandlebarsViewResolver(handlebars);
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".hbs");
  }

  @Test
  public void shouldRejectViewNameWithProtocolInjection() {
    HandlebarsView view = new HandlebarsView();
    // Simulate a view name resolving to a file protocol
    view.setUrl("/WEB-INF/views/file:/etc/passwd.hbs");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              viewResolver.buildView("file:/etc/passwd"); // Triggers configure()
            });

    assertTrue(exception.getMessage().contains("Unsafe Spring MVC view name detected"));
  }

  @Test
  public void shouldRejectViewNameWithDirectoryTraversal() {
    HandlebarsView view = new HandlebarsView();
    view.setUrl("/WEB-INF/views/../../etc/passwd.hbs");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              viewResolver.buildView("../../etc/passwd");
            });

    assertTrue(exception.getMessage().contains("Unsafe Spring MVC view name detected"));
  }

  @Test
  public void shouldRejectViewNameWithUrlFragment() {
    HandlebarsView view = new HandlebarsView();
    view.setUrl("/WEB-INF/views/secret.hbs#bypass.hbs");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              viewResolver.buildView("secret.hbs#bypass");
            });

    assertTrue(exception.getMessage().contains("Unsafe Spring MVC view name detected"));
  }

  @Test
  public void shouldAcceptValidViewName() throws Exception {
    Template template = mock(Template.class);
    HandlebarsView view = new HandlebarsView();
    view.setUrl("/WEB-INF/views/home/index.hbs");

    // Mock the compiler to prevent actual file I/O during this test
    when(handlebars.compile("home/index")).thenReturn(template);

    // This should execute successfully without throwing an IllegalArgumentException
    viewResolver.buildView("home/index");
  }
}
