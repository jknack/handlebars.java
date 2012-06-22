package specs;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class SectionsTest extends SpecTest {

  public SectionsTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("sections.yml");
  }
}
