package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlebarsApp.class })
public class Issue257 {

  @Autowired
  @Qualifier("viewResolver")
  HandlebarsViewResolver viewResolver;

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
