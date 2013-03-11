package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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

  @Test
  public void genericTypeSafe() throws IOException {
    User user = new User("edgar");
    TypeSafeTemplate<User> userTemplate = compile("Hello {{name}}!").as();
    assertEquals("Hello edgar!", userTemplate.apply(user));

  }

  @Test
  public void customTypeSafe() throws IOException {
    User user = new User("Edgar");

    UserTemplate userTemplate = compile("{{name}} is {{age}} years old!")
        .as(UserTemplate.class)
        .setAge(32);

    assertEquals("Edgar is 32 years old!", userTemplate.apply(user));
  }

  @Test
  public void customTypeSafe2() throws IOException {
    User user = new User("Edgar");

    UserTemplate userTemplate = compile("{{role}}")
        .as(UserTemplate.class)
        .setRole("Software Architect");

    assertEquals("Software Architect", userTemplate.apply(user));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void noHandlerMethod() throws IOException {
    UserTemplate userTemplate = compile("{{role}}")
        .as(UserTemplate.class);

    userTemplate.set(6);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void noHandlerMethod2() throws IOException {
    UserTemplate userTemplate = compile("{{role}}")
        .as(UserTemplate.class);

    userTemplate.set();
  }
}
