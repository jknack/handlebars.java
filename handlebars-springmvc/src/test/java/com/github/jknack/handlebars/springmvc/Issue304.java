/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HandlebarsApp.class})
public class Issue304 {

  @Autowired
  @Qualifier("viewResolver") HandlebarsViewResolver viewResolver;

  @Autowired
  @Qualifier("viewResolverWithoutMessageHelper") HandlebarsViewResolver viewResolverWithoutMessageHelper;

  @Test
  public void forward() throws Exception {
    assertNotNull(viewResolver);
    View view = viewResolver.resolveViewName("forward:/template", Locale.getDefault());
    assertNotNull(view);
    assertEquals(InternalResourceView.class, view.getClass());
  }

  @Test
  public void redirect() throws Exception {
    assertNotNull(viewResolver);
    View view = viewResolver.resolveViewName("redirect:/template", Locale.getDefault());
    assertNotNull(view);
    assertEquals(RedirectView.class, view.getClass());
  }
}
