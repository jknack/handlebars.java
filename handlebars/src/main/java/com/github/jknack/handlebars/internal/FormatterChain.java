package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Iterator;
import java.util.List;

import com.github.jknack.handlebars.Formatter;

/**
 * Default implementation for formatter chain.
 *
 * @author edgar
 * @since 2.1.0
 */
public class FormatterChain implements Formatter.Chain {

  /** Pointer to next formatter. */
  private Iterator<Formatter> chain;

  /**
   * Creates a new {@link FormatterChain}.
   *
   * @param formatter List of available formatters.
   */
  public FormatterChain(final List<Formatter> formatter) {
    this.chain = formatter.iterator();
  }

  @Override
  public Object format(final Object value) {
    Object output;
    if (chain.hasNext()) {
      Formatter formatter = chain.next();
      output = formatter.format(value, this);
      notNull(output, "Formatter " + formatter.getClass() + " returned a null result for " + value);
    } else {
      output = value.toString();
    }
    return output;
  }

}
