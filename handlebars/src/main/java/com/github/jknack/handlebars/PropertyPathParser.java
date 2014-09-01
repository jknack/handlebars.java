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
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for tokenizing path expressions.
 */
class PropertyPathParser {

  /**
   * The path pattern.
   */
  private final Pattern pattern;

  /**
   * Construct parser using path separators.
   * @param pathSeparators characters that are path separators.
   */
  public PropertyPathParser(final String pathSeparators) {
    pattern = Pattern.compile("((\\[[^\\[\\]]+])|([^" + Pattern.quote(pathSeparators) + "]+))");
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it.
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  String[] parsePath(final String key) {
    Matcher matcher = pattern.matcher(key);
    List<String> tags = new ArrayList<String>();
    while (matcher.find()) {
      tags.add(matcher.group(1));
    }
    return tags.toArray(new String[tags.size()]);
  }

}
