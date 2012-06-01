package specs;

public class SectionsTest extends SpecTest {

  @Override
  public String specName() {
    return "sections";
  }

  @Override
  protected boolean enabled(final int number, final String name) {
    return number == 4;
  }
}
