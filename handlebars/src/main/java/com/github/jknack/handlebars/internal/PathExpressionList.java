/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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
