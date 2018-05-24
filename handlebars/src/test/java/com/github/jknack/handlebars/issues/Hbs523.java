package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;

public class Hbs523 extends v4Test {

  @Override
  protected void configure(final Handlebars handlebars) {
    super.configure(handlebars);
    handlebars.deletePartialAfterMerge(true);
  }

  @Test
  public void shouldNotCallPartial() throws IOException {
    String base = "text from base partial<br>\n" + "{{#> inlinePartial}}{{/inlinePartial}}<br>";
    String inherit1 =
        "inherit1<br>\n"
            + "{{#>base}}\n"
            + "{{#*inline \"inlinePartial\"}}\n"
            + "inline partial defined by inherit1, called from base\n"
            + "{{/inline}}\n"
            + "{{/base}}";
    String inherit2 = "inherit2<br>\n" + "{{#>base}}\n" + "{{/base}}";
    String main =
        "main has partials:<br>\n"
            + "-------------<br>\n"
            + "{{>inherit1}}\n"
            + "-------------<br>\n"
            + "{{>inherit2}}";

    shouldCompileToWithoutPreEvaluation(
        main,
        $("partials", $("base", base, "inherit1", inherit1, "inherit2", inherit2)),
        "main has partials:<br>\n"
            + "-------------<br>\n"
            + "inherit1<br>\n"
            + "text from base partial<br>\n"
            + "<br>\n"
            + "-------------<br>\n"
            + "inherit2<br>\n"
            + "text from base partial<br>\n"
            + "<br>");
  }

  @Test
  public void shouldCallPartialWithoutSideEffect() throws IOException {
    String base =
        "text from base partial<br>\n"
            + "{{> @partial-block}}{{#> inlinePartial}}{{/inlinePartial}}<br>";
    String inherit1 =
        "inherit1<br>\n"
            + "{{#>base}}\n"
            + "{{#*inline \"inlinePartial\"}}\n"
            + "inline partial defined by inherit1, called from base\n"
            + "{{/inline}}\n"
            + "{{/base}}";
    String inherit2 = "inherit2<br>\n" + "{{#>base}}\n" + "{{/base}}";
    String main =
        "main has partials:<br>\n"
            + "-------------<br>\n"
            + "{{>inherit1}}\n"
            + "-------------<br>\n"
            + "{{>inherit2}}";

    shouldCompileTo(
        main,
        $("partials", $("base", base, "inherit1", inherit1, "inherit2", inherit2)),
        "main has partials:<br>\n"
            + "-------------<br>\n"
            + "inherit1<br>\n"
            + "text from base partial<br>\n"
            + "\n\n\n"
            + "inline partial defined by inherit1, called from base\n"
            + "<br>\n"
            + "-------------<br>\n"
            + "inherit2<br>\n"
            + "text from base partial<br>\n"
            + "\n"
            + "<br>");
  }
}
