package specs;


public class CommentsTest extends SpecTest {

  @Override
  public String specName() {
    return "comments";
  }

  @Override
  protected boolean enabled(final int number, final String name) {
    return true;
  }
}
