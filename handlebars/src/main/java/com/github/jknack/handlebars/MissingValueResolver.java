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
package com.github.jknack.handlebars;

/**
 * <p>
 * A strategy for dealing with missing values in <code>{{variable}}</code> expression. Useful for
 * using default values and debugging an object graph.
 * </p>
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 *    MissingValueResolver missingValue = new MissingValueResolver() {
 *       public String resolve(Object context, String name) {
 *          //return a default value or throw an exception
 *          ...;
 *       }
 *    };
 *    Handlebars handlebars = new Handlebars().with(missingValue);
 * </pre>
 *
 * @author edgar.espina
 * @since 0.9.0
 */
public interface MissingValueResolver {

  /**
   * The default missing value resolver.
   */
  MissingValueResolver NULL = new MissingValueResolver() {
    @Override
    public String resolve(final Object context, final String var) {
      return null;
    }
  };

  /**
   * Resolve a missing variable by returning a default value or producing an error.
   *
   * @param context The context object. Might be null.
   * @param var The variable's name. Never null.
   * @return Resolve a missing variable by returning a default value or producing an error.
   */
  String resolve(Object context, String var);
}
