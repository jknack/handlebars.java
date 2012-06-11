package specs;

import org.junit.internal.AssumptionViolatedException;

public class SkipTestException extends AssumptionViolatedException {

  public SkipTestException(final String assumption) {
    super(assumption);
  }

  /**
   * Default uid.
   */
  private static final long serialVersionUID = 1L;
}
