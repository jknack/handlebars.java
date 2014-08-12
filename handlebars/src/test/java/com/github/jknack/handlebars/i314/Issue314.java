package com.github.jknack.handlebars.i314;

import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;
import java.io.IOException;

public class Issue314 extends AbstractTest {

  @Test
  public void withHelperSpec() throws IOException {
    String context = "{ obj: { context: { one: 1, two: 2 } } }";

    shouldCompileTo("{{#with obj}}{{context.one}}{{/with}}",     context, "1");
    shouldCompileTo("{{#with obj}}{{obj.context.one}}{{/with}}", context, "");
 }
}
