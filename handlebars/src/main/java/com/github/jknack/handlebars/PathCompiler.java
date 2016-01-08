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
package com.github.jknack.handlebars;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.internal.path.DataPath;
import com.github.jknack.handlebars.internal.path.IndexedPath;
import com.github.jknack.handlebars.internal.path.ParentPath;
import com.github.jknack.handlebars.internal.path.PropertyPath;
import com.github.jknack.handlebars.internal.path.ResolveParentPath;
import com.github.jknack.handlebars.internal.path.ResolveThisPath;
import com.github.jknack.handlebars.internal.path.ThisPath;

/**
 * Compile mustache/handlebars expressions.
 *
 * @author edgar
 * @since 4.0.1.
 */
public final class PathCompiler {

  /** Initial expression capacity. */
  private static final int SIZE = 5;

  /** Cache with path expressions. */
  private static Map<String, List<PathExpression>> cache = new ConcurrentHashMap<>();

  /** Split pattern. */
  private static Pattern pattern = Pattern
      .compile("((\\[[^\\[\\]]+])|([^" + Pattern.quote("./") + "]+))");

  /**
   * Not allowed.
   */
  private PathCompiler() {
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it.
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  public static List<PathExpression> compile(final String key) {
    List<PathExpression> path = cache.get(key);
    if (path == null) {
      path = parse(key);
      cache.put(key, path);
    }
    return path;
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it.
   *
   * @param path The property's path.
   * @return A path representation of the property (array based).
   */
  private static List<PathExpression> parse(final String path) {
    List<PathExpression> resolvers = new ArrayList<>(SIZE);
    if ("this".equals(path) || "./".equals(path) || ".".equals(path)) {
      resolvers.add(new ResolveThisPath(path));
      return resolvers;
    }
    if ("..".equals(path)) {
      resolvers.add(new ResolveParentPath());
      return resolvers;
    }
    if (path.startsWith("../")) {
      resolvers.add(new ParentPath());
      resolvers.addAll(parse(path.substring("../".length())));
      return resolvers;
    }
    if (path.startsWith("./")) {
      resolvers.add(new ThisPath("./"));
      resolvers.addAll(parse(path.substring("./".length())));
      return resolvers;
    }
    Matcher matcher = pattern.matcher(path);
    while (matcher.find()) {
      String key = matcher.group(1);
      if ("this".equals(key)) {
        resolvers.add(new ThisPath(key));
      } else if (key.charAt(0) == '@') {
        resolvers.add(new DataPath(key));
      } else {
        if (key.charAt(0) == '[' && key.charAt(key.length() - 1) == ']') {
          key = key.substring(1, key.length() - 1);
        }
        try {
          resolvers.add(new IndexedPath(Integer.parseInt(key), key));
        } catch (NumberFormatException ex) {
          resolvers.add(new PropertyPath(key));
        }
      }
    }
    return resolvers;
  }

}
