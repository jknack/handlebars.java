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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.TemplateNotFoundTest;

/**
 * There are 4 tests what don't work as the spec says:
 * <ul>
 * <li>1. Failed Lookup. This tests look for a partial named: 'text', the
 * partial isn't defined and cannot be loaded. The spec says it should default
 * to an empty string. Handlebars.java throw an exception if a template cannot
 * be loaed. See {@link TemplateNotFoundTest}.
 * <li>6. Standalone Line Endings. See {@link PartialsNoSpecTest}.
 * <li>7. Standalone Without Previous Line. See {@link PartialsNoSpecTest}.
 * <li>8. Standalone Without Newline. See {@link PartialsNoSpecTest}.
 * <li>9. Standalone Indentation. See {@link PartialsNoSpecTest}.
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class PartialsTest extends SpecTest {

  public PartialsTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    List<Integer> skip = Arrays.asList(1);
    return skip.contains(spec.number());
  }

  @Override
  protected HelperRegistry configure(final Handlebars handlebars) {
    handlebars.setInfiniteLoops(true);
    return super.configure(handlebars);
  }

  @Parameters
  public static Collection<Object[]> data() throws IOException {
    return data("partials.yml");
  }
}
