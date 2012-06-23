package specs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import com.github.edgarespina.handlebars.TemplateNotFoundTest;

/**
 * There are 4 tests what don't work as the spec says:
 * <ul>
 * <li>1. Failed Lookup. This tests look for a partial named: 'text', the
 * partial isn't defined and cannot be loaded. The spec says it should default
 * to an empty string. Handlebars.java throw an exception if a template cannot
 * be loaed. See {@link TemplateNotFoundTest}.
 * <li>6. Standalone Line Endings. See {@link PartialsNoSpecTest}.
 * <li>7. Standalone Without Previous Line. See {@link PartialsNoSpecTest}.
 * <li>8. Standalone Without Newline. See {@link PartialsNoSpecTest}.
 * <li>9. Standalone Indentation. See {@link PartialsNoSpecTest}.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class PartialsTest extends SpecTest {

  public PartialsTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    List<Integer> skip = Arrays.asList(1, 6, 7, 8, 9);
    return skip.contains(spec.number());
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("partials.yml");
  }
}
