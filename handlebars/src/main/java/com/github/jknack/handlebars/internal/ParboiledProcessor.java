/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;

/**
 * A simple facade for parser creation. This class was introduced in order to
 * remove dynamic creation of parboiled classes. In restricted environments like
 * Google App Engine, Parboiled isn't able to create a dynamic classes.
 * The hack consist in creating Parboiled classes at build time and replace this
 * class from final jar with one that if found in the resources directory and it
 * is added at build-time.
 *
 * @author edgar.espina
 * @since 0.4.1
 */
public class ParboiledProcessor {

  public static void main(final String[] args) throws IOException {
    System.out.println("Processing parboiled classess...");
    new Handlebars().compile("Static parboiled");
    System.out.println("All the parboiled classess has been processed.");
  }

}
