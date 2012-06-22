package specs;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

public class CommentsTest extends SpecTest {

  public CommentsTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("comments.yml");
  }
}
