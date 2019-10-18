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

/**
 * Utility methods for removing whitespace according to Mustache Spec.
 *
 * @author Michael Minetti
 * @since 4.1.3
 */
public final class MustacheStringUtils {

  /**
   * Constructor.
   */
  private MustacheStringUtils() {
  }

  /**
   * Get the index of the start of the second line.
   * If a non-whitespace character is found first, null is returned.
   *
   * @param str The string.
   * @return The index of the start of the second line.
   */
  public static Integer indexOfSecondLine(final String str) {
    if (str == null) {
      return -1;
    }

    int end = str.length();
    if (end == 0) {
      return -1;
    }

    int i = 0;
    while (i < end) {
      char c = str.charAt(i);
      if ('\r' == c || '\n' == c) {
        i++;
        if ('\r' == c && i < end) {
          char next = str.charAt(i);
          if ('\n' == next) {
            i++;
          }
        }
        return i;
      }

      if (!Character.isWhitespace(c)) {
        return null;
      }

      i++;
    }

    return -1;
  }

  /**
   * Remove the last line if it contains only whitespace.
   *
   * @param str The string.
   * @return The string with it's last line removed.
   */
  public static String removeLastWhitespaceLine(final String str) {
    if (str == null) {
      return "";
    }

    int end = str.length();
    if (end == 0) {
      return "";
    }

    while (end != 0) {
      char c = str.charAt(end - 1);
      if (!Character.isWhitespace(c)) {
        return str;
      }

      if ('\r' == c || '\n' == c) {
        return str.substring(0, end);
      }

      end--;
    }

    return "";
  }
}
