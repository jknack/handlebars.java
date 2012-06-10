package specs;

import java.util.Collection;
import java.util.Map;

import org.junit.runners.Parameterized.Parameters;

public class CommentsTest extends SpecTest {

  public CommentsTest(final Map<String, Object> data) {
    super(data);
  }

  @Override
  protected boolean enabled(final int number, final String name) {
    return true;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("comments.yml");
  }
}
