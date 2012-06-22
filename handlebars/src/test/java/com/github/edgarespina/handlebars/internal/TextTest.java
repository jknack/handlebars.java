package com.github.edgarespina.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.edgarespina.handlebars.internal.Text;

/**
 * Unit test for {@link Text}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class TextTest {

  @Test
  public void newText() {
    assertEquals("a", new Text("a").text());
  }

  @Test
  public void newTextSequence() {
    assertEquals("abc", new Text("abc").text());
  }

  @Test(expected = NullPointerException.class)
  public void newTextFail() {
    new Text(null);
  }

}
