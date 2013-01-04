/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mustache.specs;

import java.util.Map;

public class Spec {
  private String name;

  private String description;

  private String template;

  private String expected;

  private int number;

  private Object data;

  private Map<String, String> partials;

  @SuppressWarnings("unchecked")
  public Spec(final Map<String, Object> spec) {
    number = (Integer) spec.get("number");

    name = (String) spec.get("name");

    description = (String) spec.get("desc");

    template = (String) spec.get("template");

    expected = (String) spec.get("expected");

    data = spec.get("data");

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
  public Object data() {
    return data;
  }

  /**
   * @return the partials
   */
  public Map<String, String> partials() {
    return partials;
  }

  @SuppressWarnings({"unchecked", "rawtypes" })
  public Spec store(final String name, final Object lambda) {
    ((Map)data).put(name, lambda);
    return this;
  }

  @Override
  public String toString() {
    return id() + "\n:  template: " + template + "\n  data: " + data;
  }
}
