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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.TemplateNotFoundTest;

/**
 * There are 4 tests what don't work as the spec says:
 *
 * <ul>
 *   <li>1. Failed Lookup. This tests look for a partial named: 'text', the partial isn't defined
 *       and cannot be loaded. The spec says it should default to an empty string. Handlebars.java
 *       throw an exception if a template cannot be loaed. See {@link TemplateNotFoundTest}.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class PartialsTest extends SpecTest {

  @Override
  protected boolean skip(final Spec spec) {
    return spec.number() == 1;
  }

  @Override
  protected HelperRegistry configure(final Handlebars handlebars) {
    handlebars.setInfiniteLoops(true);
    return super.configure(handlebars);
  }

  @ParameterizedTest
  @MethodSource("data")
  public void partials(Spec spec) throws IOException {
    runSpec(spec);
  }

  public static List<Spec> data() throws IOException {
    return data("partials.yml");
  }
}
