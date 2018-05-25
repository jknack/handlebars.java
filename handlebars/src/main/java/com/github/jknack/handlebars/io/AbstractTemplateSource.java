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

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Base class for {@link TemplateSource} with default implementation of {@link #equals(Object)} and
 * {@link #hashCode()}.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public abstract class AbstractTemplateSource implements TemplateSource {

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(filename()).build();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TemplateSource) {
      TemplateSource that = (TemplateSource) obj;
      return filename().equals(that.filename());
    }
    return false;
  }

  @Override
  public String toString() {
    return filename();
  }
}
