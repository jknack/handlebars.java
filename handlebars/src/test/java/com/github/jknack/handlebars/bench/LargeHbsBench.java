package com.github.jknack.handlebars.bench;

import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.bench.Bench.Unit;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class LargeHbsBench {

  private Map<String, Object> context;

  private Template template;

  @Before
  public void setup() throws IOException {
    Assume.assumeTrue(Boolean.valueOf(System.getProperty("run.bench")));

    template = new com.github.jknack.handlebars.Handlebars(
        new ClassPathTemplateLoader("/", ".html"))
            .registerHelper("minus", new Helper<Stock>() {
              @Override
              public Object apply(final Stock stock, final Options options)
                  throws IOException {
                return stock.getChange() < 0 ? new SafeString("class=\"minus\"") : null;
              }
            }).compile("com/github/jknack/handlebars/bench/large.hbs");
    this.context = new HashMap<>();
    this.context.put("items", IntStream.range(100000, 200000).boxed().collect(toList()));
  }

  @Test
  public void single() throws IOException {
    template.apply(context);
  }

  @Test
  public void benchmark() throws IOException {
    new Bench(1000, 5, 30).run(new Unit() {

      @Override
      public void run() throws IOException {
        template.apply(context);
      }

      @Override
      public String toString() {
        return "large.hbs";
      }
    });

  }
}
