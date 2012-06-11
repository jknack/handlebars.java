package specs;

import java.util.Map;

public class Spec {
  private String name;

  private String description;

  private String template;

  private String expected;

  private int number;

  private Map<String, Object> data;

  private Map<String, String> partials;

  @SuppressWarnings("unchecked")
  public Spec(final Map<String, Object> spec) {
    number = (Integer) spec.get("number");

    name = (String) spec.get("name");

    description = (String) spec.get("desc");

    template = (String) spec.get("template");

    expected = (String) spec.get("expected");

    data = (Map<String, Object>) spec.get("data");

    partials = (Map<String, String>) spec.get("partials");
  }

  /**
   * @return the name
   */
  public String name() {
    return name;
  }

  /**
   * @return the id
   */
  public String id() {
    return number + ". " + name;
  }

  /**
   * @return the description
   */
  public String description() {
    return id() + ": " + description;
  }

  /**
   * @return the template
   */
  public String template() {
    return template;
  }

  /**
   * @return the expected
   */
  public String expected() {
    return expected;
  }

  /**
   * @return the number
   */
  public int number() {
    return number;
  }

  /**
   * @return the data
   */
  public Map<String, Object> data() {
    return data;
  }

  /**
   * @return the partials
   */
  public Map<String, String> partials() {
    return partials;
  }

  public Spec store(final String name, final Object lambda) {
    data.put(name, lambda);
    return this;
  }

  @Override
  public String toString() {
    return id() + "\n:  template: " + template + "\n  data: " + data;
  }
}
