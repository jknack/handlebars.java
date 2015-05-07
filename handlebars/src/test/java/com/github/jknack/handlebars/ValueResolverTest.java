/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test for {@link Context}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ValueResolverTest {

  static class Base {

    String base;

    String child;

    public Base(final String base, final String child) {
      this.base = base;
      this.child = child;
    }

    public String getBaseProperty() {
      return base;
    }

    public String getChildProperty() {
      return child;
    }

    public String base() {
      return base;
    }

    public String child() {
      return child;
    }
  }

  @Test
  public void javaBeanResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(JavaBeanValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("baseProperty"));
    assertEquals("b", context.get("childProperty"));
  }

  @Test
  public void methodResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(MethodValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("getBaseProperty"));
    assertEquals("b", context.get("getChildProperty"));
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
  }

  @Test
  public void fieldResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(FieldValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
  }

  @Test
  public void mapResolver() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("base", "a");
    map.put("child", "b");

    Context context = Context
        .newBuilder(map)
        .resolver(MapValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
  }

  @Test
  public void multipleValueResolvers() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("base", "a");
    map.put("child", "b");

    Context context =
        Context
            .newBuilder(new Base("a", "b"))
            .combine("map", map)
            .resolver(
                MapValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE,
                MethodValueResolver.INSTANCE,
                FieldValueResolver.INSTANCE)
            .build();
    assertNotNull(context);
    // by field
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
    // by javaBean
    assertEquals("a", context.get("baseProperty"));
    assertEquals("b", context.get("childProperty"));
    // by method name
    assertEquals("a", context.get("getBaseProperty"));
    assertEquals("b", context.get("getChildProperty"));
    // by map
    assertEquals("a", context.get("map.base"));
    assertEquals("b", context.get("map.child"));
  }

  @Test
  public void propagateValueResolverToChild() throws IOException {
    final Object userFiledAccess = new Object() {
      @SuppressWarnings("unused")
      private String name = "User A";
    };

    final Object userMethodAccess = new Object() {
      @SuppressWarnings("unused")
      public String getName() {
        return "User B";
      }
    };

    Object users = new Object() {
      @SuppressWarnings("unused")
      public List<Object> getUsers() {
        return Arrays.asList(userFiledAccess, userMethodAccess);
      }
    };

    Template template =
        new Handlebars().compileInline("{{#each users}}{{name}}, {{/each}}");

    Context context = Context.newBuilder(users)
        .resolver(
            FieldValueResolver.INSTANCE,
            JavaBeanValueResolver.INSTANCE
        )
        .build();

    assertEquals("User A, User B, ", template.apply(context));
  }

  @Test
  public void propagateValueResolverToChildAndExtended() throws IOException {
    final Object userFiledAccess = new Object() {
      @SuppressWarnings("unused")
      private String name = "User A";
    };

    final Object extended = new Object() {
      @SuppressWarnings("unused")
      private String role = "role";
    };

    final Object userMethodAccess = new Object() {
      @SuppressWarnings("unused")
      public String getName() {
        return "User B";
      }
    };

    Object users = new Object() {
      @SuppressWarnings("unused")
      public List<Object> getUsers() {
        return Arrays.asList(userFiledAccess, userMethodAccess);
      }
    };

    Template template =
        new Handlebars().compileInline("{{#each users}}{{name}}-{{extended.role}}, {{/each}}");

    Context context = Context.newBuilder(users)
        .combine("extended", extended)
        .resolver(
            MapValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE,
            JavaBeanValueResolver.INSTANCE
        )
        .build();

    assertEquals("User A-role, User B-role, ", template.apply(context));
  }
}
