package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Test;

public class TypeSafeTemplateDefaultMethodTest {

  private static class User {
    private final String name;

    User(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public interface UserTemplate extends TypeSafeTemplate<User> {
    default String renderUpperCase(User user) throws IOException {
      return apply(user).toUpperCase();
    }
  }

  @Test
  public void testDefaultMethod() throws Exception {
    User user = new User("Smitty");
    UserTemplate template = new Handlebars()
        .compileInline("Hello {{name}}")
        .as(UserTemplate.class);
    String result = template.renderUpperCase(user);
    assertEquals("HELLO SMITTY", result);
  }
}
