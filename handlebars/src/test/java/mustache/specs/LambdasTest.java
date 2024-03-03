/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import java.io.IOException;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import com.github.jknack.handlebars.Lambda;
import com.github.jknack.handlebars.Template;

public class LambdasTest extends SpecTest {

  public LambdasTest(final Spec spec) {
    super(spec);
  }

  @Override
  protected boolean skip(final Spec spec) {
    return false;
  }

  @Override
  protected Spec alter(final Spec spec) {
    final Lambda<Object, Object> lambda;
    switch (spec.number()) {
      case 0:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return "world";
              }
            };
        break;
      case 1:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return "{{planet}}";
              }
            };
        break;
      case 2:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return "|planet| => {{planet}}";
              }
            };
        break;
      case 3:
        lambda =
            new Lambda<Object, Object>() {
              int calls = 1;

              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return Integer.toString(calls++);
              }
            };
        break;
      case 4:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return ">";
              }
            };
        break;
      case 5:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                String txt = template.text();
                return txt.equals("{{x}}") ? "yes" : "no";
              }
            };
        break;
      case 6:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                String txt = template.text();
                return txt + "{{planet}}" + txt;
              }
            };
        break;
      case 7:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                String txt = template.text();
                return txt + "{{planet}} => |planet|" + txt;
              }
            };
        break;
      case 8:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                String txt = template.text();
                return "__" + txt + "__";
              }
            };
        break;
      case 9:
        lambda =
            new Lambda<Object, Object>() {
              @Override
              public Object apply(final Object scope, final Template template) throws IOException {
                return false;
              }
            };
        break;
      default:
        throw new UnsupportedOperationException(spec.id());
    }
    spec.store("lambda", lambda);
    return spec;
  }

  @Parameters
  public static Collection<Object[]> data() throws IOException {
    return data("~lambdas.yml");
  }
}
