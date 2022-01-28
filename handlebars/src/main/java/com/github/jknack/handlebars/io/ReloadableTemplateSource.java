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
package com.github.jknack.handlebars.io;

import com.github.jknack.handlebars.cache.TemplateCache;

import java.util.Objects;

/**
 * Template source with auto-reload supports. Auto-reload is done via {@link #lastModified()}.
 *
 * See {@link TemplateCache#setReload(boolean)}
 *
 * @author edgar
 * @since 2.3.0
 */
public class ReloadableTemplateSource extends ForwardingTemplateSource {

  /**
   * Wrap a template source and implement {@link #equals(Object)} and {@link #hashCode()} using
   * {@link #lastModified()}.
   *
   * @param source A template source.
   */
  public ReloadableTemplateSource(final TemplateSource source) {
    super(source);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReloadableTemplateSource)) {
      return false;
    }
    ReloadableTemplateSource that = (ReloadableTemplateSource) o;
    return super.equals(that) && lastModified() == that.lastModified();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), lastModified());
  }
}
