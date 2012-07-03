/**
 * Copyright (c) 2012 Edgar Espina
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
package com.github.edgarespina.handlebars.internal;

import static org.parboiled.common.Preconditions.checkArgNotNull;
import static org.parboiled.common.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Formatter;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.Position;

/**
 * A {@link Formatter} for {@link InvalidInputError}s that automatically creates
 * the correct "expected" text
 * for the error.
 * Similar to {@link ErrorUtils}.
 */
class ErrorFormatter implements Formatter<InvalidInputError> {

  @Override
  public String format(final InvalidInputError error) {
    if (error == null) {
      return "";
    }

    int len = error.getEndIndex() - error.getStartIndex();
    StringBuilder sb = new StringBuilder("found");
    if (len > 0) {
      sb.append(" '")
          .append(
              StringUtils.escape(String.valueOf(error.getInputBuffer().charAt(
                  error.getStartIndex()))));
      if (len > 1) {
        sb.append("...");
      }
      sb.append('\'');
    }
    String expectedString = getExpectedString(error);
    if (StringUtils.isNotEmpty(expectedString)) {
      sb.append(", expected: ").append(expectedString);
    }
    return sb.toString();
  }

  /**
   * Find out the expected value for the given error.
   *
   * @param error The current error.
   * @return The expected value for the given error.
   */
  private String getExpectedString(final InvalidInputError error) {
    // In non recovery-mode there is no complexity in the error and start
    // indices since they are all stable.
    // However, in recovery-mode the RecoveringParseRunner inserts characters
    // into the InputBuffer, which requires
    // for all indices taken before to be shifted. The RecoveringParseRunner
    // does this by changing the indexDelta
    // of the parse runner. All users of the ParseError will then automatically
    // see shifted start and end indices
    // matching the state of the underlying InputBuffer. However, since the
    // failed MatcherPaths still carry the
    // "original" indices we need to unapply the IndexDelta in order to be able
    // to compare with them.
    int pathStartIndex = error.getStartIndex() - error.getIndexDelta();

    List<String> labelList = new ArrayList<String>();
    for (MatcherPath path : error.getFailedMatchers()) {
      Matcher labelMatcher = findProperLabelMatcher(path, pathStartIndex);
      if (labelMatcher == null) {
        continue;
      }
      String[] labels = getLabels(labelMatcher);
      for (String label : labels) {
        if (label != null) {
          for (String l : label.split("::")) {
            if ("ignore".equals(l) || "text".equals(l) || "'{'".equals(l)) {
              continue;
            }
            if (!labelList.contains(l)) {
              labelList.add(l);
            }
          }
        }
      }
    }
    return join(labelList);
  }

  /**
   * Gets the labels corresponding to the given matcher, AnyOfMatchers are
   * treated specially in that their label is constructed as a list of their
   * contents.
   *
   * @param matcher the matcher
   * @return the labels
   */
  private String[] getLabels(final Matcher matcher) {
    if (matcher instanceof AnyOfMatcher
        && ((AnyOfMatcher) matcher).characters.toString().equals(
            matcher.getLabel())) {
      AnyOfMatcher cMatcher = (AnyOfMatcher) matcher;
      if (!cMatcher.characters.isSubtractive()) {
        String[] labels = new String[cMatcher.characters.getChars().length];
        for (int i = 0; i < labels.length; i++) {
          labels[i] =
              '\'' + String.valueOf(cMatcher.characters.getChars()[i]) + '\'';
        }
        return labels;
      }
    }
    return new String[] {matcher.getLabel() };
  }

  /**
   * Join the labels.
   *
   * @param labelList The label list.
   * @return A string version of the labels.
   */
  private String join(final List<String> labelList) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < labelList.size(); i++) {
      if (i > 0) {
        sb.append(i < labelList.size() - 1 ? ", " : " or ");
      }
      String label = labelList.get(i);
      if (label.startsWith("'")) {
        sb.append(labelList.get(i));
      } else {
        sb.append("'").append(labelList.get(i)).append("'");
      }
    }
    return StringUtils.escape(sb.toString());
  }

  /**
   * Finds the Matcher in the given failedMatcherPath whose label is best for
   * presentation in "expected" strings
   * of parse error messages, given the provided lastMatchPath.
   *
   * @param path the path to the failed matcher
   * @param errorIndex the start index of the respective parse error
   * @return the matcher whose label is best for presentation in "expected"
   *         strings
   */
  static Matcher findProperLabelMatcher(final MatcherPath path,
      final int errorIndex) {
    checkArgNotNull(path, "path");
    Matcher found =
        path.parent != null ? findProperLabelMatcher(path.parent, errorIndex)
            : null;
    if (found != null) {
      return found;
    }
    if (path.element.startIndex == errorIndex
        && path.element.matcher.hasCustomLabel()) {
      return path.element.matcher;
    }
    return null;
  }

  /**
   * Pretty prints the given parse error showing its location in the given input
   * buffer.
   *
   * @param filename The file's name.
   * @param error the parse error
   * @param noffset A negative offset for better error reporting.
   * @param stacktrace The stack trace.
   * @return the pretty print text
   */
  public static String printParseError(final String filename,
      final ParseError error, final int noffset,
      final List<Stacktrace> stacktrace) {
    checkArgNotNull(error, "error");
    String message =
        (error.getErrorMessage() != null
            ? error.getErrorMessage()
            : error instanceof InvalidInputError
                ? new ErrorFormatter().format((InvalidInputError) error)
                : "").replace("EOI", "eof");
    return printErrorMessage(filename, filename + ":%2$s:%3$s: %1$s", message,
        error.getStartIndex() - noffset, error.getEndIndex() - noffset,
        error.getInputBuffer(),
        stacktrace);
  }

  /**
   * Prints an error message showing a location in the given InputBuffer.
   *
   * @param filename The file's name.
   * @param format the format string, must include three placeholders for a
   *        string
   *        (the error message) and two integers (the error line / column
   *        respectively)
   * @param errorMessage the error message
   * @param startIndex the start location of the error as an index into the
   *        inputBuffer
   * @param endIndex the end location of the error as an index into the
   *        inputBuffer
   * @param inputBuffer the underlying InputBuffer
   * @param stacktrace The calling stack.
   * @return the error message including the relevant line from the underlying
   *         input plus location indicators
   */
  private static String printErrorMessage(final String filename,
      final String format,
      final String errorMessage,
      final int startIndex, final int endIndex,
      final InputBuffer inputBuffer, final List<Stacktrace> stacktrace) {
    checkArgNotNull(inputBuffer, "inputBuffer");
    checkArgument(startIndex <= endIndex);
    String nl = "\n";
    Position pos = inputBuffer.getPosition(startIndex);
    StringBuilder sb =
        new StringBuilder(String.format(format, errorMessage, pos.line,
            pos.column));
    sb.append(nl);

    String line = inputBuffer.extractLine(pos.line);
    String indent = "    ";
    sb.append(indent).append(line);
    sb.append(nl);

    int charCount =
        Math.max(
            Math.min(endIndex - startIndex, StringUtils.length(line)
                - pos.column + 2), 1);
    for (int i = 0; i < pos.column - 1 + indent.length(); i++) {
      sb.append(' ');
    }
    for (int i = 0; i < charCount; i++) {
      sb.append('^');
    }

    if (stacktrace.size() > 0) {
      boolean includeStack = true;
      Set<Stacktrace> set = new LinkedHashSet<Stacktrace>(stacktrace);
      if (set.size() == 1) {
        includeStack = !set.iterator().next().getFilename().equals(filename);
      }
      if (includeStack) {
        sb.append(nl);
        for (Stacktrace st : set) {
          sb.append(st).append(nl);
        }
        sb.setLength(sb.length() - nl.length());
      }
    }

    return sb.toString();
  }
}
