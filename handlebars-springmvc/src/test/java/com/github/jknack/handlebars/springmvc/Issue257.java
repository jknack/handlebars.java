/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HandlebarsApp.class})
public class Issue257 {

  @Autowired
  @Qualifier("viewResolver") HandlebarsViewResolver viewResolver;

  @Test
  public void viewResolverShouldHaveBuiltInHelpers() {
    assertNotNull(viewResolver);
    assertNotNull(viewResolver.helper("with"));
    assertNotNull(viewResolver.helper("if"));
    assertNotNull(viewResolver.helper("unless"));
    assertNotNull(viewResolver.helper("each"));
    assertNotNull(viewResolver.helper("embedded"));
    assertNotNull(viewResolver.helper("block"));
    assertNotNull(viewResolver.helper("partial"));
    assertNotNull(viewResolver.helper("precompile"));
    assertNotNull(viewResolver.helper("i18n"));
    assertNotNull(viewResolver.helper("i18nJs"));
  }
}
