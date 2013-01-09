package handlebarsjs.spec;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class InvertedSectionTest extends AbstractTest {

  @Test
  public void invertedSectionsWithUnsetValue() throws IOException {
    String string = "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}";
    Object hash = $;
    shouldCompileTo(string, hash, "Right On!", "Inverted section rendered when value isn't set.");
  }

  @Test
  public void invertedSectionsWithFalseValue() throws IOException {
    String string = "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}";
    Object hash = "{goodbyes: false}";
    shouldCompileTo(string, hash, "Right On!", "Inverted section rendered when value is false.");
  }

  @Test
  public void invertedSectionsWithEmptySet() throws IOException {
    String string = "{{#goodbyes}}{{this}}{{/goodbyes}}{{^goodbyes}}Right On!{{/goodbyes}}";
    Object hash = $("goodbyes", new Object[0]);
    shouldCompileTo(string, hash, "Right On!", "Inverted section rendered when value is empty set.");
  }
}
