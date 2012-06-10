package specs;


public class InvertedTest extends SpecTest {

  @Override
  public String specName() {
    return "inverted";
  }

  @Override
  protected boolean enabled(final int number, final String name) {
    return true;
  }
}
