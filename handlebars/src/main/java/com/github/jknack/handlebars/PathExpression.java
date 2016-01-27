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

/**
 * Compiled version of path expression, like: <code>this</code>, <code>foo</code>,
 * <code>foo.bar</code>.
 *
 * @author edgar
 * @since 4.0.1
 * @see PathCompiler#compile(String)
 */
public interface PathExpression {

  /**
   * Call the next expression in the chain and/or finalize the process if this was the tail.
   *
   * @author edgar
   * @since 4.0.1
   */
  interface Chain {

    /**
     * Call the next resolver in the chain or finish the call.
     *
     * @param resolver Value resolver.
     * @param context Context object.
     * @param data Data object.
     * @return A resolved value or <code>null</code>.
     */
    Object next(ValueResolver resolver, Context context, Object data);

    /**
     * @return The current path to evaluate.
     */
    List<PathExpression> path();
  }

  /**
   * Eval the expression and resolve it to a value.
   *
   * @param resolver Value resolver
   * @param context Context object.
   * @param data Data object.
   * @param chain Expression chain.
   * @return A resolved value or <code>null</code>.
   */
  Object eval(ValueResolver resolver, Context context, Object data, Chain chain);

  /**
   * @return True if this expression is local. That's lookup won't be propagate to parent (or any
   *         other). Example of these expressions are: <code>this.name</code> <code>this</code>,
   *         etc...
   */
  boolean local();
}
