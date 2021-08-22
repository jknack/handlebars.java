package com.github.jknack.handlebars.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

public class SetAccessibleValueResolverTest {

  /*
   * The following tests require JDK 9 or greater.
   * To keep the tests from failing we use junit assume.
   */

  @Ignore
  public void testSetAccessibleOnJDK9OrGreater() throws Exception {
    assumeTrue(getJavaVersion() >= 9);
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

    mv = new MethodValueResolver();
    mv.resolve(Collections.emptyMap(), "doesNotMatter");
    Object result = mv.resolve(Collections.emptyMap(), "isEmpty");
    assertEquals(Boolean.TRUE, result);
  }

  static int getJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      version = version.substring(2, 3);
    } else {
      int dot = version.indexOf(".");
      if (dot != -1) {
        version = version.substring(0, dot);
      }
    }
    try {
      return Integer.parseInt(version);
    } catch (NumberFormatException e) {
      return 8;
    }
  }

}
