package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.jknack.handlebars.Handlebars.Utils;

/**
 * Unit test for {@link Utils#isEmpty(Object)}
 *
 * @author edgar.espina
 */
@RunWith(Parameterized.class)
public class NoFalsyValueTest {

  /**
   * The value under testing.
   */
  private Object value;

  public NoFalsyValueTest(final Object value) {
    this.value = value;
  }

  @Test
  public void noFalsy() {
    assertEquals(false, Handlebars.Utils.isEmpty(value));
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[] {new Object()},
        new Object[] {true },
        new Object[] {"Hi"},
        new Object[] {Boolean.TRUE },
        new Object[] {Arrays.asList(1)},
        new Object[] {new Object[] {1} },
        // Custom Iterable
        new Object[] {new Iterable<Integer>() {
          @Override
          public Iterator<Integer> iterator() {
            return Arrays.asList(1).iterator();
          }
        } });
  }
}
