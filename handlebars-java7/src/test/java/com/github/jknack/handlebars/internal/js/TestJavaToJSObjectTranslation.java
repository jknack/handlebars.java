package com.github.jknack.handlebars.internal.js;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test whether JS access to translated object works as expected.
 * @author jfrantzius
 *
 */
public class TestJavaToJSObjectTranslation {

  /**
   * The Javascript engine.
   */
  private static ScriptEngine engine;

  /**
   * Test array and property access in JS.
   * @throws ScriptException if we have an error in our JS script
   */
  @Test
  public void testArrayAndPropertyAccess() throws ScriptException {
    // verify both array element and object property access work
    assertEquals("bar", (String) engine.eval(
        "translated[0].foo", createFooBarBinding()));
  }

  /**
   * Obtain Javascript engine. Do only once because is expensive.
   */
  @BeforeClass
  public static void initializeEngine() {
    engine = new ScriptEngineManager().getEngineByName("JavaScript");
  }

  /**
   * Create bindings for "var translated = [{"foo", bar}]".
   * @return bindings
   */
  private Bindings createFooBarBinding() {
    List<Map<String, String>> list = new ArrayList<>();
    Map<String, String> map = new HashMap<>();
    map.put("foo", "bar");
    list.add(map);
    Object translated = JavaObjectToJSTranslation.translateIfNecessary(list);
    Bindings bindings = engine.createBindings();
    bindings.put("translated", translated);

    return bindings;
  }

}
