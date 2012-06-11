package specs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

public class PartialsTest extends SpecTest {

  public PartialsTest(final Map<String, Object> data) {
    super(data);
  }

  @Override
  protected boolean enabled(final int number, final String name) {
    Collection<Integer> off = Arrays.asList(6, 7, 8, 9);
    return !off.contains(number);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("partials.yml");
  }
}
