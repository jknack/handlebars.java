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

/**
 * A decorator allows a declarative means to both annotate particular blocks with metadata as
 * well as to wrap in behaviors when desired, prior to execution.
 *
 * @author edgar
 * @since 4.0.0
 */
public interface Decorator {

  /**
   * Decorate a template with metadata.
   *
   * @param fn Decorated template.
   * @param options Options object.
   * @throws IOException If something goes wrong.
   */
  void apply(Template fn, Options options) throws IOException;

}
