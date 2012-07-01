package com.github.edgarespina.handlebars;

import java.util.HashMap;

public class Literals {
  public static <S, T> MapBuilder<S, T> $(final S key, final T value) {
    return new MapBuilder<S, T>().$(key, value);
  }

  @SuppressWarnings("serial")
  public static class MapBuilder<S, T> extends HashMap<S, T> {
    public MapBuilder() {
    }

    public MapBuilder<S, T> $(final S key, final T value) {
      put(key, value);
      return this;
    }
  }
}
