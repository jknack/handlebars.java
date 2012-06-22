package specs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

public class PartialsTest extends SpecTest {

  public PartialsTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    List<Integer> skip = Arrays.asList(6, 7, 8, 9);
    return skip.contains(spec.number());
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("partials.yml");
  }
}
