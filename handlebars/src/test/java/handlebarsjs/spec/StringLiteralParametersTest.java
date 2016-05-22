package handlebarsjs.spec;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class StringLiteralParametersTest extends AbstractTest {

  @Test
  public void simpleLiteralWork() throws IOException {
    String string = "Message: {{hello \"world\" 12 true false}}";

    Hash helpers = $("hello", new Helper<Object>() {
      @Override
      public Object apply(final Object context, final Options options) throws IOException {
        return "Hello " + context + " " + options.param(0) + " times: " + options.param(1)
            + " " + options.param(2);
      }
    });
    shouldCompileTo(string, $, helpers, "Message: Hello world 12 times: true false",
        "template with a simple String literal");
  }

  @Test(expected = HandlebarsException.class)
  public void usingQuoteInTheMiddleOfParameterRaisesAnError() throws IOException {
    shouldCompileTo("Message: {{hello wo\"rld\"}}", $, null);
  }

  @Test
  public void escapingAStringIsPossible() throws IOException {
    String string = "Message: {{{hello \"\\\"world\\\"\"}}}";
    Hash helpers = $("hello", new Helper<String>() {
      @Override
      public Object apply(final String param, final Options options) throws IOException {
        return "Hello " + param;
      }
    });
    shouldCompileTo(string, $, helpers, "Message: Hello \"world\"",
        "template with an escaped String literal");
  }

  @Test
  public void itWorksWithSingleQoutes() throws IOException {
    String string = "Message: {{{hello \"Alan\'s world\"}}}";
    Hash helpers = $("hello", new Helper<String>() {
      @Override
      public Object apply(final String param, final Options options) throws IOException {
        return "Hello " + param;
      }
    });
    shouldCompileTo(string, $, helpers, "Message: Hello Alan's world", "template with a ' mark");
  }

  @Test
  public void simpleMultiParamsWork() throws IOException {
    String string = "Message: {{goodbye cruel world}}";
    String hash = "{cruel: cruel, world: world}";
    Hash helpers = $("goodbye", new Helper<String>() {
      @Override
      public Object apply(final String cruel, final Options options) throws IOException {
        return "Goodbye " + cruel + " " + options.get("world");
      }
    });
    shouldCompileTo(string, hash, helpers, "Message: Goodbye cruel world",
        "regular helpers with multiple params");
  }

  @Test
  public void blockMultiParamsWork() throws IOException {
    String string = "Message: {{#goodbye cruel world}}{{greeting}} {{adj}} {{noun}}{{/goodbye}}";
    String hash = "{cruel: cruel, world: world}";
    Hash helpers = $("goodbye", new Helper<String>() {
      @Override
      public Object apply(final String cruel, final Options options) throws IOException {
        return options.fn($("greeting", "Goodbye", "adj", "cruel", "noun", "world"));
      }
    });
    shouldCompileTo(string, hash, helpers, "Message: Goodbye cruel world",
        "block helpers with multiple params");
  }

}
