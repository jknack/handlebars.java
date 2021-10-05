package com.github.jknack.handlebars.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;

public class SetAccessibleValueResolverTest {

  /*
   * The following tests require JDK 9 or greater.
   * To keep the tests from failing we use junit assume.
   */
  @Test
  public void shouldPrintWarningAndNotThrowExceptionSetAccesibleOnOnJava9Or17() throws Exception {
    assumeTrue(Handlebars.Utils.javaVersion > 8 && Handlebars.Utils.javaVersion <= 14);
    MethodValueResolver mv = new MethodValueResolver() {

      @Override
      protected boolean isUseSetAccessible(Method m) {
        return true;
      }
    };
    Object result = mv.resolve(Collections.emptyMap(), "doesNotMatter");
    assertEquals(ValueResolver.UNRESOLVED, result);
  }

  @Test
  public void shouldNotPrintWarningAndNotThrowExceptionSetAccesibleOnOnJava9Or17()
      throws Exception {
    assumeTrue(Handlebars.Utils.javaVersion > 8 && Handlebars.Utils.javaVersion <= 17);
    MethodValueResolver mv = new MethodValueResolver();
    Object result = mv.resolve(Collections.emptyMap(), "isEmpty");
    assertEquals(Boolean.TRUE, result);
  }

  @Test
  public void shouldThrowExceptionOnJava17orHigher() throws Exception {
    assumeTrue(Handlebars.Utils.javaVersion >= 17);
    MethodValueResolver mv = new MethodValueResolver() {

      @Override
      protected boolean isUseSetAccessible(Method m) {
        return true;
      }
    };
    try {
      mv.resolve(Collections.emptyMap(), "doesNotMatter");
      fail("Expect InaccessibleObjectException");
    } catch (/* InaccessibleObjectException */ Exception e) {
    }
  }

}
