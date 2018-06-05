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

/**
 * Utility class for handling exceptions.
 *
 * @author edgar
 */
public final class Throwing {

  /**
   * Throwing runnable.
   */
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

  /**
   * Not used.
   */
  private Throwing() {
  }

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
