/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
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
