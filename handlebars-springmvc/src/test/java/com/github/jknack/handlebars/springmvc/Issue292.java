/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlebarsApp.class})
public class Issue292 {

  @Autowired
  @Qualifier("viewResolver") HandlebarsViewResolver viewResolver;

  @Autowired
  @Qualifier("viewResolverWithoutMessageHelper") HandlebarsViewResolver viewResolverWithoutMessageHelper;

  @Test
  public void getTemplate() throws Exception {
    assertNotNull(viewResolver);
    View view = viewResolver.resolveViewName("template", Locale.getDefault());
    assertTrue(view instanceof HandlebarsView);
    assertNotNull(((HandlebarsView) view).getTemplate());
  }
}
