package specs;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class InterpolationTest extends SpecTest {

  public InterpolationTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("interpolation.yml");
  }
}
