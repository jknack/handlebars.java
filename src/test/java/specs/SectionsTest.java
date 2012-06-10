package specs;

import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

public class SectionsTest extends SpecTest {

  public SectionsTest(final Map<String, Object> data) {
    super(data);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("sections.yml");
  }
}
