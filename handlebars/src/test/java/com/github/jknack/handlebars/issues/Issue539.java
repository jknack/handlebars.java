package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

public class Issue539 extends v4Test {

  @Override protected void configure(Handlebars handlebars)  {
    super.configure(handlebars);
    handlebars.setPreEvaluatePartialBlocks(false);
  }

  @Test
  public void inlinePartialsLeak() throws Exception {
    shouldCompileTo("main has partials:<br>\n"
            + "-------------<br>\n"
            + "{{>inherit1}}\n"
            + "-------------<br>\n"
            + "{{>inherit2}}",
        $("hash", $,
            "partials",
            $(
                "base", "text from base partial<br>\n"
                    + "{{#>inlinePartial}}{{/inlinePartial}}<br>\n"
                    + "{{#>inlinePartial2}}{{/inlinePartial2}}<br>",
                "inherit1", "inherit1<br>\n"
                    + "{{#>base}}\n"
                    + "{{#*inline \"inlinePartial\"}}\n"
                    + "    inline partial defined by inherit1, called from base\n"
                    + "{{/inline}}\n"
                    + "    {{#*inline \"inlinePartial2\"}}\n"
                    + "        {{>some-other-template}}\n"
                    + "    {{/inline}}\n"
                    + "{{/base}}",
                "inherit2", "inherit2<br>\n"
                    + "{{#>base}}\n"
                    + "{{/base}}",
                "some-other-template", "template called from second inline partial of inherit 1")
        ),
        "main has partials:<br>\n"
            + "-------------<br>\n"
            + "inherit1<br>\n"
            + "text from base partial<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "-------------<br>\n"
            + "inherit2<br>\n"
            + "text from base partial<br>\n"
            + "<br>\n"
            + "<br>");
  }
}
