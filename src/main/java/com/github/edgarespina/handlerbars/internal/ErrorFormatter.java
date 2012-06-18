package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkArgNotNull;
import static org.parboiled.common.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.Formatter;
import org.parboiled.common.StringUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.matchers.AnyOfMatcher;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
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
      sb.append(", expected ").append(expectedString);
    }
    return sb.toString();
  }

  /**
   * Find out the expected value for the given error.
   *
   * @param error The current error.
   * @return The expected value for the given error.
   */
  public String getExpectedString(final InvalidInputError error) {
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
        if (label != null && !labelList.contains(label)) {
          labelList.add(label);
        }
      }
    }
    return join(labelList);
  }

  /**
   * Gets the labels corresponding to the given matcher, AnyOfMatchers are
   * treated specially in that their
   * label is constructed as a list of their contents
   *
   * @param matcher the matcher
   * @return the labels
   */
  public String[] getLabels(final Matcher matcher) {
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
  public String join(final List<String> labelList) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < labelList.size(); i++) {
      if (i > 0) {
        sb.append(i < labelList.size() - 1 ? ", " : " or ");
      }
      sb.append(labelList.get(i));
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
   * @param error the parse error
   * @return the pretty print text
   */
  public static String printResult(final ParsingResult<?> result) {
    List<ParseError> errors = result.parseErrors;
    StringBuilder buffer = new StringBuilder();
    for (ParseError error : errors) {
      buffer.append(printParseError(error)).append("\n");
    }
    return buffer.toString().trim();
  }

  /**
   * Pretty prints the given parse error showing its location in the given input
   * buffer.
   *
   * @param error the parse error
   * @return the pretty print text
   */
  public static String printParseError(final ParseError error) {
    checkArgNotNull(error, "error");
    String message =
        error.getErrorMessage() != null ? error.getErrorMessage() :
            error instanceof InvalidInputError ?
                new ErrorFormatter().format((InvalidInputError) error) : "";
    return printErrorMessage("line %2$s:%3$s: %1$s", message,
        error.getStartIndex(), error.getEndIndex(), error.getInputBuffer())
        .trim();
  }

  /**
   * Prints an error message showing a location in the given InputBuffer.
   *
   * @param format the format string, must include three placeholders for a
   *        string
   *        (the error message) and two integers (the error line / column
   *        respectively)
   * @param errorMessage the error message
   * @param errorIndex the error location as an index into the inputBuffer
   * @param inputBuffer the underlying InputBuffer
   * @return the error message including the relevant line from the underlying
   *         input plus location indicator
   */
  public static String printErrorMessage(final String format,
      final String errorMessage,
      final int errorIndex,
      final InputBuffer inputBuffer) {
    checkArgNotNull(inputBuffer, "inputBuffer");
    return printErrorMessage(format, errorMessage, errorIndex, errorIndex + 1,
        inputBuffer);
  }

  /**
   * Prints an error message showing a location in the given InputBuffer.
   *
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
   * @return the error message including the relevant line from the underlying
   *         input plus location indicators
   */
  public static String printErrorMessage(final String format,
      final String errorMessage,
      final int startIndex, final int endIndex,
      final InputBuffer inputBuffer) {
    checkArgNotNull(inputBuffer, "inputBuffer");
    checkArgument(startIndex <= endIndex);
    Position pos = inputBuffer.getPosition(startIndex);
    StringBuilder sb =
        new StringBuilder(String.format(format, errorMessage, pos.line,
            pos.column));
    sb.append('\n');

    String line = inputBuffer.extractLine(pos.line);
    sb.append(line);
    sb.append('\n');

    int charCount =
        Math.max(
            Math.min(endIndex - startIndex, StringUtils.length(line)
                - pos.column + 2), 1);
    for (int i = 0; i < pos.column - 1; i++) {
      sb.append(' ');
    }
    for (int i = 0; i < charCount; i++) {
      sb.append('^');
    }
    sb.append("\n");

    return sb.toString();
  }
}
