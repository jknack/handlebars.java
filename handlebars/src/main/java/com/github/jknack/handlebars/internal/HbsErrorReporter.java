/**
 * Copyright (c) 2012-2013 Edgar Espina
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
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
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
    String prevLine = lines[Math.max(0, line - 2)];
    String nextLine = lines[Math.min(lines.length - 1, line + 1)];
    String evidence = prevLine + "\n" + message.substring(evidenceStat) + "\n" + nextLine;
    message.append(stacktrace);
    HandlebarsError error = new HandlebarsError(filename, line, column, reason,
        evidence, message.toString());
    throw new HandlebarsException(error);
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
    String errorLine = lines[line - 1];
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
    String input = stream instanceof CommonTokenStream
        ? ((CommonTokenStream) stream).getTokenSource().getInputStream().toString()
        : stream.toString();
    String[] lines = input.split("\n");
    return lines;
  }

  @Override
  public void reportAmbiguity(final Parser recognizer, final DFA dfa, final int startIndex,
      final int stopIndex, final BitSet ambigAlts, final ATNConfigSet configs) {
  }

  @Override
  public void reportAttemptingFullContext(final Parser recognizer, final DFA dfa,
      final int startIndex, final int stopIndex, final ATNConfigSet configs) {
  }

  @Override
  public void reportContextSensitivity(final Parser recognizer, final DFA dfa,
      final int startIndex, final int stopIndex, final ATNConfigSet configs) {
  }

}
