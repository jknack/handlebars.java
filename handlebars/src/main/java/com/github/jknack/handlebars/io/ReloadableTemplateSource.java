package com.github.jknack.handlebars.io;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.jknack.handlebars.cache.TemplateCache;

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
  public int hashCode() {
    return new HashCodeBuilder().append(filename()).append(lastModified()).build();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TemplateSource) {
      TemplateSource that = (TemplateSource) obj;
      return filename().equals(that.filename()) && lastModified() == that.lastModified();
    }
    return false;
  }

}
