package com.github.edgarespina.handlerbars.internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

enum Transformer {
  NONE,

  ARRAY {
    @Override
    protected boolean apply(final Object candidate) {
      return candidate != null && candidate.getClass().isArray();
    }

    @Override
    public Object transform(final Object candidate) {
      int size = Array.getLength(candidate);
      List<Object> list = new ArrayList<Object>(size);
      for (int i = 0; i < size; i++) {
        list.add(Array.get(candidate, i));
      }
      return list;
    }
  };

  protected boolean apply(final Object candidate) {
    return false;
  }

  public Object transform(final Object candidate) {
    return candidate;
  }

  public static Transformer get(final Object candidate) {
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