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
package specs;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

/**
 * This test demostrate how the four missing test from the spec works.
 * <ul>
 * <li>6. Standalone Line Endings.
 * <li>7. Standalone Without Previous Line
 * <li>8. Standalone Without Newline
 * <li>9. Standalone Indentation
 * </ul>
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class PartialsNoSpecTest extends SpecTest {

  public PartialsNoSpecTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    List<Integer> skip = Arrays.asList(6, 7, 8, 9);
    return !skip.contains(spec.number());
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data("partialsNoSpec.yml");
  }
}
