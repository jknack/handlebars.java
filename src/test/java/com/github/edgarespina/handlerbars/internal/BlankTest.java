package com.github.edgarespina.handlerbars.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * Unit test for {@link Blank}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class BlankTest {

  @Test
  public void newBlank() {
    assertEquals(" ", new Blank(" ").rawText());
  }

  @Test
  public void newBlankSequence() {
    assertEquals(" \t", new Blank(" \t").rawText());
  }

  @Test(expected = NullPointerException.class)
  public void newBlankFail() {
    new Blank(null);
  }

  @Test
  public void apply() throws IOException {
    Blank blank = new Blank("   ");
    assertEquals("   ", blank.apply(null));
  }
}
