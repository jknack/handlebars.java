package specs;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class DelimitersTest extends SpecTest {

  public DelimitersTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("delimiters.yml");
  }

}
