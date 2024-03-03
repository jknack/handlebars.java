/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;

public class EachKeyTest extends AbstractTest {

  public static class Blog {

    private String title;

    private String body;

    public Blog(final String title, final String body) {
      this.title = title;
      this.body = body;
    }

    public String getTitle() {
      return title;
    }

    public String getBody() {
      return body;
    }
  }

  @Override
  protected Object configureContext(Object context) {
    return Context.newBuilder(context)
        .resolver(MapValueResolver.INSTANCE, JavaBeanValueResolver.INSTANCE)
        .build();
  }

  @Test
  public void eachKeyWithString() throws IOException {
    Set<String> result =
        Stream.of(
                StringUtils.split(
                    compile("{{#each this}}{{@key}} {{/each}}").apply(configureContext("String")),
                    " "))
            .collect(Collectors.toSet());

    Set<String> expected = Stream.of("empty", "bytes").collect(Collectors.toSet());
    assertTrue(result.containsAll(expected));
  }

  @Test
  public void eachKeyWithInt() throws IOException {
    shouldCompileTo("{{#each this}}{{@key}} {{/each}}", configureContext(7), "");
  }

  @Test
  public void eachKeyWithJavaBean() throws IOException {
    Blog blog = new Blog("Handlebars.java", "...");
    Set<String> result =
        Stream.of(
                StringUtils.split(
                    compile("{{#each this}}{{@key}}:{{this}} {{/each}}")
                        .apply(configureContext(blog)),
                    " "))
            .collect(Collectors.toSet());

    Set<String> expected =
        Stream.of("body:...", "title:Handlebars.java").collect(Collectors.toSet());
    assertTrue(result.containsAll(expected));
  }

  @Test
  public void eachKeyWithHash() throws IOException {
    Map<String, Object> hash = new LinkedHashMap<>();
    hash.put("body", "...");
    hash.put("title", "Handlebars.java");
    shouldCompileTo(
        "{{#each this}}{{@key}}: {{this}} {{/each}}", hash, "body: ... title: Handlebars.java ");
  }
}
