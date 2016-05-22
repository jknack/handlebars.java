package handlebarsjs.spec;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class BasicContextTest extends AbstractTest {


  @Test
  public void mostBasic() throws IOException {
    shouldCompileTo("{{foo}}", "{foo: foo}", "foo");
  }


  @Test
  public void compilingWithBasicContext() throws IOException {
    shouldCompileTo("Goodbye\n{{cruel}}\n{{world}}!",
        "{cruel: cruel, world: world}",
        "Goodbye\ncruel\nworld!",
        "It works if all the required keys are provided");
  }
  
  @Test
  public void compilingWithBasicContextAccentsAndColon() throws IOException {
    shouldCompileTo("Goodbye\n{{x:cruél}}\n{{world}}!",
        "{'x:cruél': cruel, world: world}",
        "Goodbye\ncruel\nworld!",
        "It works if all the required keys are provided");
  }



  @Test
  public void comments() throws IOException {
    shouldCompileTo("{{! Goodbye}}Goodbye\n{{cruel}}\n{{world}}!",
        "{cruel: cruel, world: world}",
        "Goodbye\ncruel\nworld!",
        "comments are ignored");
  }


  @Test
  public void bool() throws IOException {
    String string = "{{#goodbye}}GOODBYE {{/goodbye}}cruel {{world}}!";
    shouldCompileTo(string, "{goodbye: true, world: world}", "GOODBYE cruel world!",
        "booleans show the contents when true");

    shouldCompileTo(string, "{goodbye: false, world: world}", "cruel world!",
        "booleans do not show the contents when false");
  }


  @Test
  public void zeros() throws IOException {
    shouldCompileTo("num1: {{num1}}, num2: {{num2}}", "{num1: 42, num2: 0}",
        "num1: 42, num2: 0");
    shouldCompileTo("num: {{.}}", "0", "num: 0");
    shouldCompileTo("num: {{num1/num2}}", "{num1: {num2: 0}}", "num: 0");
  }


  @Test
  public void newlines() throws IOException {
    shouldCompileTo("Alan's\nTest", "{}", "Alan's\nTest");
    shouldCompileTo("Alan's\rTest", "{}", "Alan's\rTest");
  }


  @Test
  public void escapingText() throws IOException {
    shouldCompileTo("Awesome's", "{}", "Awesome's",
        "text is escaped so that it doesn't get caught on single quotes");
    shouldCompileTo("Awesome\\", "{}", "Awesome\\",
        "text is escaped so that the closing quote can't be ignored");
    shouldCompileTo("Awesome\\\\ foo", "{}", "Awesome\\\\ foo",
        "text is escaped so that it doesn't mess up backslashes");
    shouldCompileTo("Awesome {{foo}}", "{foo: '\\'}", "Awesome \\",
        "text is escaped so that it doesn't mess up backslashes");
    shouldCompileTo(" \" \" ", "{}", " \" \" ", "double quotes never produce invalid javascript");
  }


  @Test
  public void escapingExpressions() throws IOException {
    shouldCompileTo("{{{awesome}}}", "{awesome: '&\"\\<>'}", "&\"\\<>",
        "expressions with 3 handlebars aren't escaped");

    shouldCompileTo("{{&awesome}}", "{awesome: '&\"\\<>'}", "&\"\\<>",
        "expressions with {{& handlebars aren't escaped");

    shouldCompileTo("{{awesome}}", $("awesome", "&\"'`\\<>"), "&amp;&quot;&#x27;&#x60;\\&lt;&gt;",
        "by default expressions should be escaped");

    shouldCompileTo("{{awesome}}", "{awesome: 'Escaped, <b> looks like: &lt;b&gt;'}",
        "Escaped, &lt;b&gt; looks like: &amp;lt;b&amp;gt;",
        "escaping should properly handle amperstands");
  }


  @SuppressWarnings("unused")
  @Test
  public void functionReturningSafeStringsShouldnotBeEscaped() throws IOException {
    Object hash = new Object() {
      public Object getAwesome() {
        return new Handlebars.SafeString("&\"\\<>");
      }
    };
    shouldCompileTo("{{awesome}}", hash, "&\"\\<>",
        "functions returning safestrings aren't escaped");
  }


  @Test
  @SuppressWarnings("unused")
  public void functions() throws IOException {
    shouldCompileTo("{{awesome}}", new Object() {
      public Object getAwesome() {
        return "Awesome";
      }
    }, "Awesome", "functions are called and render their output");

    shouldCompileTo("{{awesome}}", new Object() {
      String more = "More awesome";

      public Object getAwesome() {
        return more;
      }
    }, "More awesome", "functions are called and render their output");
  }


  @Test
  public void pathsWithHyphens() throws IOException {
    shouldCompileTo("{{foo-bar}}", "{foo-bar: baz}", "baz", "Paths can contain hyphens (-)");

    shouldCompileTo("{{foo.foo-bar}}", "{foo: {foo-bar: baz}}", "baz",
        "Paths can contain hyphens (-)");

    shouldCompileTo("{{foo/foo-bar}}", "{foo: {foo-bar: baz}}", "baz",
        "Paths can contain hyphens (-)");
  }


  @Test
  public void nestedPaths() throws IOException {
    shouldCompileTo("Goodbye {{alan/expression}} world!", "{alan: {expression: beautiful}}",
        "Goodbye beautiful world!", "Nested paths access nested objects");
  }


  @Test
  public void nestedPathsWithEmptyStringValue() throws IOException {
    shouldCompileTo("Goodbye {{alan/expression}} world!", "{alan: {expression: ''}}",
        "Goodbye  world!", "Nested paths access nested objects with empty string");
  }


  @Test
  public void literalPaths() throws IOException {
    Object hash = $("@alan", $("expression", "beautiful"));
    shouldCompileTo("Goodbye {{[@alan]/expression}} world!", hash,
        "Goodbye beautiful world!", "Literal paths can be used");
  }


  @Test
  public void thatCurrentContextPathDoesnotHitHelpers() throws IOException {
    shouldCompileTo("test: {{.}}", (Object) null, $("helper", "awesome"), "test: ");
  }


  @Test
  public void complexButEmptyPaths() throws IOException {
    shouldCompileTo("{{person/name}}", "{person: {name: null}}", "");
    shouldCompileTo("{{person/name}}", "{person: {}}", "");
  }


  @Test
  public void thisKeywordInPaths() throws IOException {
    String string = "{{#goodbyes}}{{this}}{{/goodbyes}}";
    Object hash = $("goodbyes", new String[]{"goodbye", "Goodbye", "GOODBYE" });
    shouldCompileTo(string, hash, "goodbyeGoodbyeGOODBYE",
        "This keyword in paths evaluates to current context");

    string = "{{#hellos}}{{this/text}}{{/hellos}}";
    hash = $("hellos", new Object[]{$("text", "hello"), $("text", "Hello"), $("text", "HELLO") });
    shouldCompileTo(string, hash, "helloHelloHELLO", "This keyword evaluates in more complex paths");
  }


  @Test
  public void thisKeywordInHelpers() throws IOException {
    Hash helpers = $("foo", new Helper<Object>() {

      @Override
      public Object apply(final Object value, final Options options) throws IOException {
        return "bar " + value;
      }
    });
    String string = "{{#goodbyes}}{{foo this}}{{/goodbyes}}";
    Object hash = $("goodbyes", new String[]{"goodbye", "Goodbye", "GOODBYE" });
    shouldCompileTo(string, hash, helpers, "bar goodbyebar Goodbyebar GOODBYE",
        "This keyword in paths evaluates to current context");

    string = "{{#hellos}}{{foo this/text}}{{/hellos}}";
    hash = $("hellos", new Object[]{$("text", "hello"), $("text", "Hello"), $("text", "HELLO") });
    shouldCompileTo(string, hash, helpers, "bar hellobar Hellobar HELLO",
        "This keyword evaluates in more complex paths");
  }
}
