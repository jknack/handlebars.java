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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.cache.NullTemplateCache;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

/**
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
@Configuration
public class ReactiveHandlebarsApp {
  public static CharSequence helperSource() {
    return "helper source!";
  }

  @Bean
  public ReactiveHandlebarsViewResolver viewResolver() {
    ReactiveHandlebarsViewResolver resolver = new ReactiveHandlebarsViewResolver();

    Helper<Object> helper = (context, options) -> "Spring Helper";
    resolver.registerHelper("spring", helper);

    resolver.setHelperSources(Collections.singletonList(ReactiveHandlebarsApp.class));

    Map<String, Helper<?>> helpers = new HashMap<>();
    helpers.put("setHelper", helper);
    resolver.setHelpers(helpers);

    resolver.setTemplateCache(NullTemplateCache.INSTANCE);
    resolver.setBindI18nToMessageSource(true);

    return resolver;
  }

  @Bean
  public ReactiveHandlebarsViewResolver parameterizedViewResolver(
      final ApplicationContext context) {
    ReactiveHandlebarsViewResolver resolver = new ReactiveHandlebarsViewResolver(new Handlebars(
        new SpringTemplateLoader(context)));

    // no cache for testing
    resolver.setTemplateCache(NullTemplateCache.INSTANCE);

    return resolver;
  }

  @Bean
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    return messageSource;
  }

  @Bean
  public ReactiveHandlebarsViewResolver viewResolverWithoutMessageHelper() {
    return new ReactiveHandlebarsViewResolver().withoutMessageHelper();
  }
}
