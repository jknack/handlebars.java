package com.github.jknack.handlebars.springmvc;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

@Configuration
public class HandlebarsApp {

  @Bean
  public HandlebarsViewResolver viewResolver() {
    HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();

    viewResolver.registerHelper("spring", new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options) throws IOException {
        return "Spring Helper";
      }
    });
    viewResolver.registerHelpers(HandlebarsApp.class);
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

  public static CharSequence helperSource() {
    return "helper source!";
  }
}
