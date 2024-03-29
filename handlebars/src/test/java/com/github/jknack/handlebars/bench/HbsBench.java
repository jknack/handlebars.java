/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.bench;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.bench.Bench.Unit;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class HbsBench {

  private Map<String, Object> context;

  private Template template;

  @BeforeEach
  public void setup() throws IOException {
    assumeTrue(Boolean.valueOf(System.getProperty("run.bench")));

    template =
        new com.github.jknack.handlebars.Handlebars(new ClassPathTemplateLoader("/", ".html"))
            .registerHelper(
                "minus",
                new Helper<Stock>() {
                  @Override
                  public Object apply(final Stock stock, final Options options) throws IOException {
                    return stock.getChange() < 0 ? new SafeString("class=\"minus\"") : null;
                  }
                })
            .compile("com/github/jknack/handlebars/bench/stocks.hbs");
    this.context = new HashMap<>();
    this.context.put("items", Stock.dummyItems());
  }

  @Test
  public void single() throws IOException {
    template.apply(context);
  }

  @Test
  public void benchmark() throws IOException {
    new Bench(1000, 5, 30)
        .run(
            new Unit() {

              @Override
              public void run() throws IOException {
                template.apply(context);
              }

              @Override
              public String toString() {
                return "stocks.hbs";
              }
            });
  }
}
