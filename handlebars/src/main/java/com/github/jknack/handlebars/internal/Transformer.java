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
package com.github.jknack.handlebars.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Transform a value and produce a new value if applies. For example, arrays are
 * converted to list in order to iterate over them using the Iterable interface.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
enum Transformer {
  /**
   * No transformation at all.
   */
  NONE,

  /**
   * Convert arrays into list.
   */
  ARRAY {
    @Override
    boolean apply(final Object candidate) {
      return candidate != null && candidate.getClass().isArray();
    }

    @Override
    public Object doTransform(final Object candidate) {
      int size = Array.getLength(candidate);
      List<Object> list = new ArrayList<Object>(size);
      for (int i = 0; i < size; i++) {
        list.add(Array.get(candidate, i));
      }
      return list;
    }
  };

  /**
   * Return true if the strategy applies for the candidate value.
   *
   * @param candidate The candidate value may be null.
   * @return True if the strategy applies for the candidate value.
   */
  boolean apply(final Object candidate) {
    return false;
  }

  /**
   * Transform the given value into something different or leave it as it is.
   *
   * @param candidate The candidate value, may be null.
   * @return A new value or the original value.
   */
  Object doTransform(final Object candidate) {
    return candidate;
  }

  /**
   * Transform the given value into something different or leave it as it is.
   *
   * @param canidate The candidate value. May be null.
   * @return A transformed value or the original value.
   */
  public static Object transform(final Object canidate) {
    return get(canidate).doTransform(canidate);
  }

  /**
   * Find the best transformer for the given value.
   *
   * @param candidate The candidate value.
   * @return The best transformer for the given value. Not null.
   */
  private static Transformer get(final Object candidate) {
    EnumSet<Transformer> transoformers = EnumSet.allOf(Transformer.class);
    transoformers.remove(NONE);
    for (Transformer transformer : transoformers) {
      if (transformer.apply(candidate)) {
        return transformer;
      }
    }
    return NONE;
  }
}
