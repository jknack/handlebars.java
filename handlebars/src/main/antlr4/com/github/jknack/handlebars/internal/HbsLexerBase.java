/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import java.util.Objects;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.Interval;

public abstract class HbsLexerBase extends Lexer {

  protected String start = "{{";
  protected String end = "}}";
  protected boolean whiteSpaceControl;

  private String tempStartDelim;
  private String tempEndDelim;
  protected int currentTagType;

  protected HbsLexerBase(final CharStream input) {
    super(input);
  }

  public void setDelimiters(final String start, final String end) {
    if (Objects.requireNonNull(start, "Start delimiter").isEmpty()
        || Objects.requireNonNull(end, "End delimiter").isEmpty()) {
      throw new IllegalArgumentException("Delimiters must not be empty");
    }
    this.start = start;
    this.end = end;
  }

  protected void captureDelimiter(final String delimiter) {
    if (tempStartDelim == null) {
      tempStartDelim = delimiter;
    } else if (tempEndDelim == null) {
      tempEndDelim = delimiter;
    }
  }

  protected void applyDelimiters() {
    if (tempStartDelim != null && tempEndDelim != null) {
      setDelimiters(tempStartDelim, tempEndDelim);
    }
    tempStartDelim = null;
    tempEndDelim = null;
  }

  public void handleEscapeSequence() {
    String slashes = getText();
    int count = slashes.length();
    StringBuilder text = new StringBuilder(count + start.length());
    text.append(slashes);
    if ((count & 1) != 0) {
      int startIndex = _input.index();
      if (peekStart(start)) {
        advanceStart(start);
      } else if (peekStart("{")) {
        advanceStart("{");
      } else if (peekStart("!--")) {
        advanceStart("!--");
      } else if (peekStart("!")) {
        advanceStart("!");
      } else if (peekStart("=")) {
        advanceStart("=");
      } else if (peekStart("<")) {
        advanceStart("<");
      } else if (peekStart("$")) {
        advanceStart("$");
      } else if (start.length() == 1) {
        // a single char delimiter is fully consumed by the escape
        advance(1);
      } else {
        advanceStart("");
      }
      text.append(_input.getText(Interval.of(startIndex, _input.index() - 1)));
    }
    setText(text.toString());
  }

  protected boolean peekStart(final String suffix) {
    if ("!--".equals(suffix) || "!".equals(suffix)) {
      return peekAhead(start + suffix) || peekAhead(start + "~" + suffix);
    }
    if ("{".equals(suffix)) {
      return peekAhead(start + "~" + suffix) || peekAhead(start + suffix);
    }
    return peekAhead(start + suffix);
  }

  protected boolean peekEnd(final String prefix) {
    if ("--".equals(prefix)
        || ("".equals(prefix) && "COMMENT_MODE".equals(getModeNames()[_mode]))) {
      return peekAhead(prefix + end) || peekAhead(prefix + "~" + end);
    }
    if ("}".equals(prefix)) {
      return peekAhead(prefix + "~" + end) || peekAhead(prefix + end);
    }
    return peekAhead(prefix + end);
  }

  protected void advanceStart(final String suffix) {
    currentTagType = "{{".equals(suffix) ? 2 : "{".equals(suffix) ? 1 : 0;
    if ("!--".equals(suffix) || "!".equals(suffix) || "{".equals(suffix)) {
      advanceWithOptionalControl(start, suffix);
    } else {
      advance(start.length() + suffix.length() - 1);
    }
  }

  protected void advanceEnd(final String prefix) {
    boolean commentBoundary =
        "--".equals(prefix)
            || ("".equals(prefix) && "COMMENT_MODE".equals(getModeNames()[_mode]));
    if (commentBoundary || "}".equals(prefix)) {
      advanceWithOptionalControl(prefix, end);
    } else {
      advance(prefix.length() + end.length() - 1);
    }
  }

  private void advanceWithOptionalControl(final String prefix, final String suffix) {
    String withoutControl = prefix + suffix;
    String withControl = prefix + "~" + suffix;
    String remainingWithoutControl = withoutControl.substring(1);
    String remainingWithControl = withControl.substring(1);
    if (peekAhead(remainingWithControl)) {
      advance(remainingWithControl.length());
      whiteSpaceControl = true;
    } else if (peekAhead(remainingWithoutControl)) {
      advance(remainingWithoutControl.length());
    }
  }

  private void advance(final int count) {
    if (count > 0) {
      String skipped =
          _input.getText(Interval.of(_input.index(), _input.index() + count - 1));
      int line = getInterpreter().getLine();
      int column = getInterpreter().getCharPositionInLine();
      for (int i = 0; i < skipped.length(); i++) {
        if (skipped.charAt(i) == '\n') {
          line++;
          column = 0;
        } else {
          column++;
        }
      }
      _input.seek(_input.index() + count);
      getInterpreter().setLine(line);
      getInterpreter().setCharPositionInLine(column);
    }
  }

  private boolean peekAhead(final String text) {
    for (int i = 0; i < text.length(); i++) {
      if (_input.LA(i + 1) != text.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
