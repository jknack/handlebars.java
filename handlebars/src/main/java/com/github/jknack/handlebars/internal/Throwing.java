/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

/**
 * Utility class for handling exceptions.
 *
 * @author edgar
 */
public final class Throwing {

  /** Throwing runnable. */
  public interface Runnable {

    /**
     * Run action.
     *
     * @throws Throwable If something goes wrong.
     */
    void run() throws Throwable;
  }

  /**
   * Run action.
   *
   * @param <R> Result type.
   */
  public interface Supplier<R> {

    /**
     * Run action.
     *
     * @return Value.
     * @throws Throwable If something goes wrong.
     */
    R get() throws Throwable;
  }

  /** Not used. */
  private Throwing() {}

  /**
   * Run action.
   *
   * @param task Action.
   */
  public static void run(final Runnable task) {
    try {
      task.run();
    } catch (Throwable x) {
      throw sneakyThrow(x);
    }
  }

  /**
   * Run action and returns a value.
   *
   * @param task Action.
   * @param <T> Action type.
   * @return A value.
   */
  public static <T> T get(final Supplier<T> task) {
    try {
      return task.get();
    } catch (Throwable x) {
      throw sneakyThrow(x);
    }
  }

  /**
   * Rethrow a checked exception.
   *
   * @param x Exception.
   * @param <T> Exception wrapper.
   * @return Nothing.
   * @throws T Exception.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> RuntimeException sneakyThrow(final Throwable x) throws T {
    throw (T) x;
  }
}
