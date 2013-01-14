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
package com.github.jknack.handlebars;

import java.io.IOException;

import org.pegdown.PegDownProcessor;

import com.github.jknack.handlebars.internal.AbstractOptions;

/**
 * A markdown helper.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class MarkdownHelper implements Helper<Object> {

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return "";
    }
    String markdown = context.toString();
    PegDownProcessor processor = new PegDownProcessor();
    return new Handlebars.SafeString(processor.markdownToHtml(markdown));
  }

}
