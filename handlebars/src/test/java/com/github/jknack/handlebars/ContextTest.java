/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test for {@link Context}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ContextTest {

  static class Base {
    public String getBaseProperty() {
      return "baseProperty";
    }

    public String getChildProperty() {
      return "baseProperty";
    }
  }

  static class Child extends Base {
    @Override
    public String getChildProperty() {
      return "childProperty";
    }
  }

  @Test
  public void newContext() {
    Context context = Context.newContext("String");
    assertNotNull(context);
    assertEquals("String", context.model());
  }

  @Test
  public void parentContext() {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("name", "Handlebars");
    Context parent = Context.newContext(model);
    assertNotNull(parent);
    assertEquals("Handlebars", parent.get("name"));

    Map<String, Object> extended = new HashMap<String, Object>();
    extended.put("n", "Extended");
    Context child = Context.newContext(parent, extended);
    assertEquals("Extended", child.get("n"));
    assertEquals("Handlebars", child.get("name"));
  }

  @Test(expected = NullPointerException.class)
  public void nullParent() {
    Context.newContext(null, new Object());
  }

  @Test
  public void dotLookup() {
    Context context = Context.newContext("String");
    assertNotNull(context);
    assertEquals("String", context.get("."));
  }

  @Test
  public void thisLookup() {
    Context context = Context.newContext("String");
    assertNotNull(context);
    assertEquals("String", context.get("this"));
  }

  @Test
  public void singleMapLookup() {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("simple", "value");
    Context context = Context.newContext(model);
    assertNotNull(context);
    assertEquals("value", context.get("simple"));
  }

  @Test
  public void nestedMapLookup() {
    Map<String, Object> model = new HashMap<String, Object>();
    Map<String, Object> nested = new HashMap<String, Object>();
    model.put("nested", nested);
    nested.put("simple", "value");
    Context context = Context.newContext(model);
    assertNotNull(context);
    assertEquals("value", context.get("nested.simple"));
  }

  @Test
  public void singleObjectLookup() {
    Object model = new Object() {
      @SuppressWarnings("unused")
      public String getSimple() {
        return "value";
      }

      @Override
      public String toString() {
        return "Model Object";
      }
    };
    Context context = Context.newContext(model);
    assertNotNull(context);
    assertEquals("value", context.get("simple"));
  }

  @Test
  public void nestedObjectLookup() {
    Object model = new Object() {
      @SuppressWarnings("unused")
      public Object getNested() {
        return new Object() {
          public String getSimple() {
            return "value";
          }
        };
      }
    };
    Context context = Context.newContext(model);
    assertNotNull(context);
    assertEquals("value", context.get("nested.simple"));
  }

  @Test
  public void customLookup() {
    Context context = Context.newContext(new Base());
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
    assertEquals("baseProperty", context.get("childProperty"));
  }

  @Test
  public void customLookupOnChildClass() {
    Context context = Context.newContext(new Child());
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
    assertEquals("childProperty", context.get("childProperty"));
  }

  @Test
  public void combine() {
    Context context = Context
        .newBuilder(new Base())
        .combine("expanded", "value")
        .build();
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
    assertEquals("value", context.get("expanded"));
  }

  @Test
  public void contextResolutionInCombine() {
    Context context = Context
        .newBuilder(new Base())
        .combine("baseProperty", "value")
        .build();
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
  }

  @Test
  public void combineNested() {
    Map<String, Object> expanded = new HashMap<String, Object>();
    expanded.put("a", "a");
    expanded.put("b", true);
    Context context = Context
        .newBuilder(new Base())
        .combine("expanded", expanded)
        .build();
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
    assertEquals(expanded, context.get("expanded"));
    assertEquals("a", context.get("expanded.a"));
    assertEquals(true, context.get("expanded.b"));
  }

  @Test
  public void expanded() {
    Map<String, Object> expanded = new HashMap<String, Object>();
    expanded.put("a", "a");
    expanded.put("b", true);
    Context context = Context
        .newBuilder(new Base())
        .combine(expanded)
        .build();
    assertNotNull(context);
    assertEquals("baseProperty", context.get("baseProperty"));
    assertEquals(null, context.get("expanded"));
    assertEquals("a", context.get("a"));
    assertEquals(true, context.get("b"));
  }

  @Test
  public void issue28() {
    Context root = Context.newBuilder("root").build();
    assertEquals("root", root.get("this"));
    Context child1 = Context.newBuilder(root, "child1").build();
    assertEquals("child1", child1.get("this"));
    Context child2 = Context.newBuilder(root, "child2")
        .combine(new HashMap<String, Object>()).build();
    assertEquals("child2", child2.get("this"));
  }

  public void testCombineGenerics() {

    Context.newBuilder("blah").combine(new HashMap<String, String>());
    Context.newBuilder("blah").combine(new HashMap<String, Object>());
    Context.newBuilder("blah").combine(new HashMap<String, Map<String, Object>>());
  }

}
