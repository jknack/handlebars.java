/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import org.junit.internal.AssumptionViolatedException;

public class SkipTestException extends AssumptionViolatedException {

  public SkipTestException(final String assumption) {
    super(assumption);
  }

  /** Default uid. */
  private static final long serialVersionUID = 1L;
}
