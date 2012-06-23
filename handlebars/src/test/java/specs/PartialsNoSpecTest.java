package specs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

/**
 * This test demostrate how the four missing test from the spec works.
 * <ul>
 * <li>6. Standalone Line Endings.
 * <li>7. Standalone Without Previous Line
 * <li>8. Standalone Without Newline
 * <li>9. Standalone Indentation
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class PartialsNoSpecTest extends SpecTest {

  public PartialsNoSpecTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    List<Integer> skip = Arrays.asList(6, 7, 8, 9);
    return !skip.contains(spec.number());
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("partialsNoSpec.yml");
  }
}
