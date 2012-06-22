package com.github.edgarespina.handlebars.internal;

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
