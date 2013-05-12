package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.jknack.handlebars.Handlebars.Utils;

/**
 * Unit test for {@link Utils}
 *
 * @author edgar.espina
 */
@RunWith(Parameterized.class)
public class FalsyValueTest {

  /**
   * The value under testing.
   */
  private Object value;

  public FalsyValueTest(final Object value) {
    this.value = value;
  }

  @Test
  public void falsy() {
    assertEquals(true, Handlebars.Utils.isEmpty(value));
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
        new Object[]{null },
        new Object[]{false },
        new Object[]{"" },
        new Object[]{Boolean.FALSE },
        new Object[]{0 },
        new Object[]{(short) 0 },
        new Object[]{0L },
        new Object[]{0F },
        new Object[]{0D },
        new Object[]{BigInteger.ZERO },
        new Object[]{BigDecimal.ZERO },
        new Object[]{Collections.emptyList() },
        new Object[]{new Object[0] },
        // Custom Iterable
        new Object[]{new Iterable<Object>() {
          @Override
          public Iterator<Object> iterator() {
            return Collections.emptyList().iterator();
          }
        } });
  }
}
