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

import java.io.IOException;
import java.io.Writer;

/**
 * Make handlebars templates type-safe. Users can extend the {@link TypeSafeTemplate} and add new
 * methods.
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 *  public interface UserTemplate extends TypeSafeTemplate&lt;User&gt; {
 *    UserTemplate setAge(int age);
 *
 *    UserTemplate setRole(String role);
 *
 *    ...
 *  }
 *
 *  UserTemplate template = new Handlebars().compileInline("{{name}} is {{age}} years old!")
 *    .as(UserTemplate.class);
 *
 *  template.setAge(32);
 *
 *  assertEquals("Edgar is 32 years old!", template.apply(new User("Edgar")));
 * </pre>
 *
 * @author edgar.espina
 * @since 0.10.0
 * @see Template#as(Class)
 * @see Template#as()
 * @param <T> The root object type.
 */
public interface TypeSafeTemplate<T> {

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @param writer The writer object. Required.
   * @throws IOException If a resource cannot be loaded.
   */
  void apply(T context, Writer writer) throws IOException;

  /**
   * Merge the template tree using the given context.
   *
   * @param context The context object. May be null.
   * @return The resulting template.
   * @throws IOException If a resource cannot be loaded.
   */
  String apply(T context) throws IOException;

}
