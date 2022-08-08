package com.github.jknack.handlebars.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@link MustacheStringUtils}.
 */
public class MustacheStringUtilsTest {

  @Test
  public void testIndexOfFirstNewline() {
    testIndexOfFirstNewline(null, -1);
    testIndexOfFirstNewline("", -1);
    testIndexOfFirstNewline("\n", 1);
    testIndexOfFirstNewline("\na", 1);
    testIndexOfFirstNewline(" \n", 2);
    testIndexOfFirstNewline(" \r", 2);
    testIndexOfFirstNewline(" \r\n", 3);
    testIndexOfFirstNewline(" \n\n", 2);
    testIndexOfFirstNewline("a\n\n", null);
    testIndexOfFirstNewline(" a\n", null);
    testIndexOfFirstNewline(" \na", 2);
  }
  
  private void testIndexOfFirstNewline(String str, Integer expected) {
    Integer result = MustacheStringUtils.indexOfSecondLine(str);
    assertEquals(expected, result);
  } 
  
  @Test
  public void testRemoveLastWhitespaceLine() {
    testRemoveLastWhitespaceLine(null, "");
    testRemoveLastWhitespaceLine("", "");
    testRemoveLastWhitespaceLine("\n ", "\n");
    testRemoveLastWhitespaceLine("\n      ", "\n");
    testRemoveLastWhitespaceLine("\n\n", "\n\n");
    testRemoveLastWhitespaceLine("\r\n", "\r\n");
    testRemoveLastWhitespaceLine("\r", "\r");
    testRemoveLastWhitespaceLine("\r\r", "\r\r");
    testRemoveLastWhitespaceLine("a\n", "a\n");
    testRemoveLastWhitespaceLine("a", "a");
    testRemoveLastWhitespaceLine(" ", "");
    testRemoveLastWhitespaceLine("\na ", "\na ");
    testRemoveLastWhitespaceLine("\n\na", "\n\na");
    testRemoveLastWhitespaceLine("\na\nb", "\na\nb");
    testRemoveLastWhitespaceLine("\na\n ", "\na\n");
  }
  
  private void testRemoveLastWhitespaceLine(String str, String expected) {
    String result = MustacheStringUtils.removeLastWhitespaceLine(str);
    assertEquals(expected, result);
  } 
}
