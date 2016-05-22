package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;

public class Issue502 {

  @Test
  public void supportForIteratingMapsWithNonStringKeys() throws IOException {
    Map<Object, Collection<String>> data = new TreeMap<>();
    Collection<String> d1 = new ArrayList<>();
    d1.add("1");
    d1.add("2");
    d1.add("3");

    Collection<String> d2 = new ArrayList<>();
    d2.add("4");
    d2.add("5");
    d2.add("6");

    data.put(123, d1);
    data.put(456, d2);

    Map<String, Object> params = new HashMap<>();
    params.put("data", data);

    String result = new Handlebars()
        .compileInline("{{#each data}}{{ @key }} - {{#each . }}Val:{{.}}{{/each}}{{/each}}")
        .apply(params);

    assertEquals("123 - Val:1Val:2Val:3456 - Val:4Val:5Val:6", result);
  }

}
