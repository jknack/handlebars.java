package handlebarsjs.spec;

import com.github.jknack.handlebars.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class IncludeTest extends AbstractTest {

    private final Hash dudes = $("dudes",
            new Object[]{
                    $("name", "Yehuda", "url", "http://yehuda"),
                    $("name", "Alan", "url", "http://alan")
            });

    @Test
    public void include() throws IOException {
        String template = "{{#each dudes}}{{include \"dude\" greeting=\"Hi\"}} {{/each}}";
        String partial = "{{greeting}}, {{name}}!";
        String expected = "Hi, Yehuda! Hi, Alan! ";
        shouldCompileToWithPartials(template, dudes, $("dude", partial), expected);
    }

    /**
     * This is a port of the original test case from
     * https://github.com/wycats/handlebars.js/issues/182
     */
    @Test
    @Ignore("Accessing the parent context fails to parse.")
    public void includeWithParentContext() throws IOException {
        String template = "{{#each dudes}}{{include \"dude\" greeting=..}} {{/each}}";
        String partial = "{{greeting.hello}}, {{name}}!";
        String expected = "Hi, Yehuda! Hi, Alan! ";
        Hash partials = $("dude", partial);
        Hash context = $("hello", "Hi", "dudes", dudes);
        shouldCompileToWithPartials(template, context, partials, expected);
    }
}
