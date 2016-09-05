package com.github.jknack.handlebars.springmvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Handlebars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

@Configuration
public class HandlebarsApp {

  @Autowired
  ApplicationContext applicationContext;

  @Bean
  public HandlebarsViewResolver viewResolver() {
    HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();

    Helper<Object> helper = new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return "Spring Helper";
      }
    };

    viewResolver.registerHelper("spring", helper);
    viewResolver.setHelperSources(Arrays.asList(HandlebarsApp.class));
    Map<String, Helper<?>> helpers = new HashMap<String, Helper<?>>();
    helpers.put("setHelper", helper);
    viewResolver.setHelpers(helpers);
    // no cache for testing
    viewResolver.setCache(false);

    viewResolver.setBindI18nToMessageSource(true);

    return viewResolver;
  }

  @Bean
  public HandlebarsViewResolver parameterizedHandlebarsViewResolver() {
    HandlebarsViewResolver viewResolver = new HandlebarsViewResolver(new Handlebars(
        new SpringTemplateLoader(applicationContext)));

    // no cache for testing
    viewResolver.setCache(false);

    return viewResolver;
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
  }

  @Bean
  public HandlebarsViewResolver viewResolverWithoutMessageHelper() {
    return new HandlebarsViewResolver().withoutMessageHelper();
  }

  public static CharSequence helperSource() {
    return "helper source!";
  }
}
