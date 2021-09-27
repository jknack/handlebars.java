package com.github.jknack.handlebars;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IgnoreWindowsLineMatcher extends TypeSafeMatcher<String> {

  private String value;

  private IgnoreWindowsLineMatcher(String value) {
    this.value = value;
  }

  public static IgnoreWindowsLineMatcher equalsToStringIgnoringWindowsNewLine(String value) {
    return new IgnoreWindowsLineMatcher(value);
  }

  @Override protected boolean matchesSafely(String item) {
    return item.replace("\r\n", "\n").equals(value);
  }

  @Override public void describeTo(Description description) {
    description.appendText("a string ")
        .appendText("ignoring \\r")
        .appendText(" ")
        .appendValue(value);
  }
}
