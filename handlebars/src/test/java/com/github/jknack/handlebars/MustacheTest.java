package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MustacheTest {

  @Test
  public void demo() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.setPrettyPrint(true);
    Map<String, Object> hash = new HashMap<String, Object>();
    hash.put("name", "Chris");
    hash.put("value", 10000);
    hash.put("taxed_value", 10000 - 10000 * 0.4);
    hash.put("in_ca", true);

    String output = handlebars.compile("mustache").apply(hash).replace("\r\n", "\n");

    assertEquals("Hello Chris\nYou have just won $10000!\nWell, $6000.0, after taxes.\n", output);
  }
}
