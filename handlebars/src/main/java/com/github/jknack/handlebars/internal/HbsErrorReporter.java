/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;

/**
 * The Handlebars error reporter.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class HbsErrorReporter implements ANTLRErrorListener {

  /**
   * A file's name.
   */
  private String filename;

  /**
   * Creates a new {@link HbsErrorReporter}.
   *
   * @param filename The file's name. Required.
   */
  public HbsErrorReporter(final String filename) {
    this.filename = notNull(filename, "A filename is required.");
  }

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
      final int line, final int charPositionInLine, final String msg,
      final RecognitionException e) {
    int column = Math.max(1, charPositionInLine);
    CommonToken offendingToken = (CommonToken) offendingSymbol;
    StringBuilder message = new StringBuilder();
    message.append(filename).append(":").append(line).append(":").append(column)
        .append(": ");
    String stacktrace = "";
    int reasonStart = message.length();
    if (offendingToken == null) {
      String[] parts = StringUtils.split(msg, "\n");
      message.append(parts[0]);
      stacktrace = "\n" + join(parts, "\n", 1, parts.length);
    } else {
      message.append("found: '").append(offendingToken.getText()).append("', ");
      message.append("expected: '").append(msg).append("'");
    }
    String reason = message.substring(reasonStart);
    message.append("\n");
    int evidenceStat = message.length();
    String[] lines = lines(recognizer);
    underline(message, lines, line, column);
    String prevLine = lineAt(lines, line > lines.length ? lines.length : line - 2);
    String nextLine = lineAt(lines, line);
    String evidence = prevLine + "\n" + message.substring(evidenceStat) + "\n" + nextLine;
    message.append(stacktrace);
    HandlebarsError error = new HandlebarsError(filename, line, column, reason
        .replace("<EOF>", "EOF"), evidence, message.toString());
    throw new HandlebarsException(error);
  }

  /**
   * Get a line at the specified number (if possible).
   *
   * @param lines The lines.
   * @param number The line number to extract.
   * @return The line or an empty string.
   */
  private String lineAt(final String[] lines, final int number) {
    if (number >= 0 && number < lines.length) {
      return lines[number];
    }
    return "";
  }

  /**
   * Build an underline mark and make the error message pretty.
   *
   * @param message The message.
   * @param lines The source lines.
   * @param line The offending's line.
   * @param charPositionInLine The offenfing's column.
   */
  private void underline(final StringBuilder message, final String[] lines, final int line,
      final int charPositionInLine) {
    String errorLine = lines[Math.min(line - 1, lines.length - 1)];
    message.append(errorLine).append("\n");
    for (int i = 0; i < charPositionInLine; i++) {
      message.append(" ");
    }
    message.append("^");
  }

  /**
   * Extract lines.
   *
   * @param recognizer A lexer/parser.
   * @return Source lines
   */
  private String[] lines(final Recognizer<?, ?> recognizer) {
    IntStream stream = recognizer.getInputStream();
    if (stream instanceof CommonTokenStream) {
      stream = ((CommonTokenStream) stream).getTokenSource().getInputStream();
    }
    final String input;
    if (stream instanceof CharStream) {
      input = ((CharStream) stream).getText(new Interval(0, stream.size()));
    } else {
      input = stream.toString();
    }
    String[] lines = input.split("\n");
    return lines;
  }

  @Override
  public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex, final boolean exact, final BitSet ambigAlts,
      final ATNConfigSet configs) {
  }

  @Override
  public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa,
      final int startIndex, final int stopIndex, final BitSet conflictingAlts,
      final ATNConfigSet configs) {
  }

  @Override
  public void reportContextSensitivity(final Parser recognizer, final DFA dfa,
      final int startIndex, final int stopIndex, final int prediction, final ATNConfigSet configs) {
  }

}
