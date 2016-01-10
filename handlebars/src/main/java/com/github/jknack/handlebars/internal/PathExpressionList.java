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

import java.util.ArrayList;

import com.github.jknack.handlebars.PathExpression;

/**
 * A compiled {@link PathExpression}.
 *
 * @author edgar
 * @since 4.0.3
 */
@SuppressWarnings("serial")
public class PathExpressionList extends ArrayList<PathExpression> {

  /** Default expression size. */
  private static final int SIZE = 3;

  /** Expression path. */
  private final String path;

  /**
   * Creates a new compiled expression.
   *
   * @param path Expression path.
   */
  public PathExpressionList(final String path) {
    super(SIZE);
    this.path = path;
  }

  @Override
  public String toString() {
    return path;
  }

}
