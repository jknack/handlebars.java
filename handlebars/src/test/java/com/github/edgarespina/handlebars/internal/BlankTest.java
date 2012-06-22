package com.github.edgarespina.handlebars.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.edgarespina.handlebars.internal.Blank;

/**
 * Unit test for {@link Blank}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class BlankTest {

  @Test
  public void newBlank() {
    assertEquals(" ", new Blank(" ").text());
  }

  @Test
  public void newBlankSequence() {
    assertEquals(" \t", new Blank(" \t").text());
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
