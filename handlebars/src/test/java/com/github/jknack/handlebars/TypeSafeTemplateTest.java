/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class TypeSafeTemplateTest extends AbstractTest {

  public static class User {
    private String name;

    public User(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static interface UserTemplate extends TypeSafeTemplate<User> {
    public UserTemplate setAge(int age);

    public UserTemplate setRole(String role);

    public void set();

    public void set(int v);
  }

  private static TypeSafeTemplate<User> createTemplate() {
    return null;
  }

  @Test
  public void genericTypeSafe() throws IOException {
    User user = new User("edgar");
    TypeSafeTemplate<User> userTemplate = compile("Hello {{name}}!").as();
    assertEquals("Hello edgar!", userTemplate.apply(user));
  }

  @Test
  public void customTypeSafe() throws IOException {
    User user = new User("Edgar");

    UserTemplate userTemplate =
        compile("{{name}} is {{age}} years old!").as(UserTemplate.class).setAge(32);

    assertEquals("Edgar is 32 years old!", userTemplate.apply(user));
  }

  @Test
  public void customTypeSafe2() throws IOException {
    User user = new User("Edgar");

    UserTemplate userTemplate =
        compile("{{role}}").as(UserTemplate.class).setRole("Software Architect");

    assertEquals("Software Architect", userTemplate.apply(user));
  }

  @Test
  public void testToString() throws IOException {
    UserTemplate userTemplate = compile("Hello {{name}}").as(UserTemplate.class);
    assertThat(userTemplate.toString(), containsString("UserTemplate"));
  }

  @Test
  public void testHashCode() throws IOException {
    Template template = compile("Hello {{name}}");
    UserTemplate userTemplate = template.as(UserTemplate.class);

    assertEquals(userTemplate.hashCode(), userTemplate.hashCode());
    assertNotEquals(userTemplate.hashCode(), template.as(UserTemplate.class));
  }

  @Test
  public void testEquals() throws IOException {
    Template template = compile("Hello {{name}}");
    UserTemplate userTemplate = template.as(UserTemplate.class);

    assertEquals(userTemplate, userTemplate);
    assertNotEquals(userTemplate, template.as(UserTemplate.class));
    assertFalse(userTemplate.equals(null));
  }

  @Test
  public void noHandlerMethod() throws IOException {
    assertThrows(
        UnsupportedOperationException.class,
        () -> {
          UserTemplate userTemplate = compile("{{role}}").as(UserTemplate.class);

          userTemplate.set(6);
        });
  }

  @Test
  public void noHandlerMethod2() throws IOException {
    assertThrows(
        UnsupportedOperationException.class,
        () -> {
          UserTemplate userTemplate = compile("{{role}}").as(UserTemplate.class);

          userTemplate.set();
        });
  }
}
