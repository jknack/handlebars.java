/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SectionsTest extends SpecTest {

  @ParameterizedTest
  @MethodSource("data")
  public void sections(Spec spec) throws IOException {
    runSpec(spec);
  }

  public static List<Spec> data() throws IOException {
    return data("sections.yml");
  }
}
