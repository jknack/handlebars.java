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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class SpecRunner extends Parameterized {
  private List<String> labels;
  private Description labelledDescription;

  public SpecRunner(final Class<?> cl) throws Throwable {
    super(cl);
    initialiseLabels();
    generateLabelledDescription();
  }

  private void initialiseLabels() throws Throwable {
    Collection<Object[]> parameterArrays = getParameterArrays4_4();
    labels = new ArrayList<String>();
    for (Object[] parameterArray : parameterArrays) {
      Spec spec = (Spec) parameterArray[0];
      labels.add(spec.name());
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<Object[]> getParameterArrays4_4() throws Throwable {
    TestClass testClass = this.getTestClass();
    List<FrameworkMethod> annotatedMethods = testClass.getAnnotatedMethods(Parameters.class);
    FrameworkMethod frameworkMethod = annotatedMethods.get(0);
    return (Collection<Object[]>)frameworkMethod.invokeExplosively(testClass);
  }

  private void generateLabelledDescription() throws Exception {
    Description originalDescription = super.getDescription();
    labelledDescription = Description
        .createSuiteDescription(originalDescription.getDisplayName());
    List<Description> childDescriptions = originalDescription
        .getChildren();
    int childCount = childDescriptions.size();
    if (childCount != labels.size()) {
      throw new Exception(
          "Number of labels and number of parameters must match.");
    }

    for (int i = 0; i < childDescriptions.size(); i++) {
      Description childDescription = childDescriptions.get(i);
      String label = labels.get(i);
      Description newDescription = Description
          .createSuiteDescription(label);
      List<Description> grandChildren = childDescription
          .getChildren();
      for (Description grandChild : grandChildren) {
        newDescription.addChild(grandChild);
      }
      labelledDescription.addChild(newDescription);
    }
  }

  @Override
  public Description getDescription() {
    return labelledDescription;
  }
}
