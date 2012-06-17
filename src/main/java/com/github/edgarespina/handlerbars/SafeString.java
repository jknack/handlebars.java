package com.github.edgarespina.handlerbars;

public class SafeString implements CharSequence {

  private String inner;

  public SafeString(final String inner) {
    this.inner = inner;
  }

  @Override
  public int length() {
    return inner.length();
  }

  @Override
  public char charAt(final int index) {
    return inner.charAt(index);
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    return inner.subSequence(start, end);
  }

  @Override
  public String toString() {
    return inner;
  }
}
