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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jknack.handlebars.internal.PathExpressionList;
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

  /** right parenthesis. */
  private static final char RP = ']';

  /** left parenthesis. */
  private static final char LP = '[';

  /** at symbol. */
  private static final char AT = '@';

  /** Parent traversal. */
  private static final String PARENT_PATH = "../";

  /** Parent. */
  private static final String PARENT = "..";

  /** This mustache. */
  private static final String DOT = ".";

  /** This handlebars. */
  private static final String DOT_PATH = "./";

  /** This handlebars. */
  private static final String THIS = "this";

  /** Cache with path expressions. */
  private static Map<String, List<PathExpression>> cache = new ConcurrentHashMap<>();

  /** Split pattern. */
  private static Pattern pattern = Pattern
      .compile("((\\[[^\\[\\]]+])|"
              + "(" + Pattern.quote(PARENT_PATH) + ")|([^" + Pattern.quote(DOT_PATH) + "]+))");

  /**
   * Not allowed.
   */
  private PathCompiler() {
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it. The compiled expression will extend lookup to parent.
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  public static List<PathExpression> compile(final String key) {
    return compile(key, true);
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it.
   *
   * @param key The property's name.
   * @param parentScopeResolution False, if we want to restrict lookup to current scope.
   * @return A path representation of the property (array based).
   */
  public static List<PathExpression> compile(final String key,
      final boolean parentScopeResolution) {
    boolean local = !parentScopeResolution;
    String ukey = key + local;
    List<PathExpression> path = cache.get(ukey);
    if (path == null) {
      path = parse(key, local);
      cache.put(ukey, path);
    }
    return path;
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked)
   * and create an array of it.
   *
   * @param path The property's path.
   * @param local True, if we want to restrict lookup to current scope.
   * @return A path representation of the property (array based).
   */
  private static List<PathExpression> parse(final String path, final boolean local) {
    List<PathExpression> resolvers = new PathExpressionList(path);

    if (THIS.equals(path) || DOT_PATH.equals(path) || DOT.equals(path)) {
      resolvers.add(new ResolveThisPath(path));
      return resolvers;
    }
    if (PARENT.equals(path)) {
      resolvers.add(new ResolveParentPath());
      return resolvers;
    }
    if (path.startsWith(PARENT_PATH)) {
      resolvers.add(new ParentPath());
      resolvers.addAll(parse(path.substring(PARENT_PATH.length()), local));
      return resolvers;
    }
    if (path.startsWith(DOT_PATH)) {
      resolvers.add(new ThisPath(DOT_PATH));
      resolvers.addAll(parse(path.substring(DOT_PATH.length()), local));
      return resolvers;
    }
    Matcher matcher = pattern.matcher(path);
    boolean data = false;
    while (matcher.find()) {
      String key = matcher.group(1);
      if (THIS.equals(key)) {
        resolvers.add(new ThisPath(key));
      } else if (PARENT_PATH.equals(key)) {
        resolvers.add(new ParentPath());
      } else if (key.charAt(0) == AT) {
        if (key.length() == 1) {
          data = true;
        } else {
          resolvers.add(new DataPath(key));
        }
      } else {
        if (key.charAt(0) == LP && key.charAt(key.length() - 1) == RP) {
          key = key.substring(1, key.length() - 1);
        }
        try {
          resolvers.add(new IndexedPath(Integer.parseInt(key), key, local));
        } catch (NumberFormatException ex) {
          if (data) {
            resolvers.add(new DataPath(AT + key));
          } else {
            resolvers.add(new PropertyPath(key, local));
          }
        }
      }
    }
    return resolvers;
  }

}
