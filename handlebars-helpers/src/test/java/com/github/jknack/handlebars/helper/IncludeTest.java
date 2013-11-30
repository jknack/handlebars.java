package com.github.jknack.handlebars.helper;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;

public class IncludeTest extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.registerHelper(IncludeHelper.NAME, IncludeHelper.INSTANCE);
  }

  @Test
  public void include() throws IOException {
    String template = "{{#each dudes}}{{include \"dude\" greeting=\"Hi\"}} {{/each}}";
    String partial = "{{greeting}}, {{name}}!";
    String expected = "Hi, Yehuda! Hi, Alan! ";
    Hash dudes = $("dudes",
        new Object[]{
            $("name", "Yehuda", "url", "http://yehuda"),
            $("name", "Alan", "url", "http://alan")
        });
    shouldCompileToWithPartials(template, dudes, $("dude", partial), expected);
  }

  @Test
  public void accessToParentContext() throws IOException {
    String string = "{{#each hobbies}}{{../name}} has hobby {{hobbyname}} and lives in {{../town}} {{/each}}";
    Object hash = $("name", "Dennis", "town", "berlin", "hobbies",
        new Object[]{$("hobbyname", "swimming"), $("hobbyname", "dancing"),
            $("hobbyname", "movies") });
    shouldCompileTo(string, hash, "Dennis has hobby swimming and lives in berlin " +
        "Dennis has hobby dancing and lives in berlin " +
        "Dennis has hobby movies and lives in berlin ");
  }

  @Test
  public void accessToParentContextFromPartialUsingInclude() throws IOException {
    String string = "{{#each hobbies}}{{include \"hobby\" parentcontext=.. town=../town}} {{/each}}";
    Object hash = $("name", "Dennis", "town", "berlin", "hobbies",
        new Object[]{$("hobbyname", "swimming"), $("hobbyname", "dancing"),
            $("hobbyname", "movies") });
    Hash partials = $("hobby",
        "{{parentcontext.name}} has hobby {{hobbyname}} and lives in {{town}}");

    shouldCompileToWithPartials(string, hash, partials,
        "Dennis has hobby swimming and lives in berlin " +
            "Dennis has hobby dancing and lives in berlin " +
            "Dennis has hobby movies and lives in berlin ");
  }

  @Test
  public void accessToParentContextFromPartialMustacheSpec() throws IOException {
    String string = "{{#each hobbies}}{{> hobby}} {{/each}}";

    Object hash = $("name", "Dennis", "town", "berlin", "hobbies",
        new Object[]{$("hobbyname", "swimming"), $("hobbyname", "dancing"),
            $("hobbyname", "movies") });

    Hash partials = $("hobby",
        "{{name}} has hobby {{hobbyname}} and lives in {{town}}");

    shouldCompileToWithPartials(string, hash, partials,
        "Dennis has hobby swimming and lives in berlin " +
            "Dennis has hobby dancing and lives in berlin " +
            "Dennis has hobby movies and lives in berlin ");
  }

  @Test
  public void explicitAccessToParentContextFromPartialMustacheSpec() throws IOException {
    String string = "{{#each hobbies}}{{> hobby}} {{/each}}";

    Object hash = $("name", "Dennis", "town", "berlin", "hobbies",
        new Object[]{$("hobbyname", "swimming"), $("hobbyname", "dancing"),
            $("hobbyname", "movies") });

    Hash partials = $("hobby",
        "{{../name}} has hobby {{hobbyname}} and lives in {{../town}}");

    shouldCompileToWithPartials(string, hash, partials,
        "Dennis has hobby swimming and lives in berlin " +
            "Dennis has hobby dancing and lives in berlin " +
            "Dennis has hobby movies and lives in berlin ");
  }
}
