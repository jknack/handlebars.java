package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlebarsApp.class })
public class Issue304 {

  @Autowired
  @Qualifier("viewResolver")
  HandlebarsViewResolver viewResolver;

  @Autowired
  @Qualifier("viewResolverWithoutMessageHelper")
  HandlebarsViewResolver viewResolverWithoutMessageHelper;

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
