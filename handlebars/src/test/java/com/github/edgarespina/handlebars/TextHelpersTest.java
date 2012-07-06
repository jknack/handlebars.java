package com.github.edgarespina.handlebars;

import static com.github.edgarespina.handlebars.StringHelpers.ABBREVIATE;
import static com.github.edgarespina.handlebars.StringHelpers.CAPITALIZE;
import static com.github.edgarespina.handlebars.StringHelpers.CAPITALIZE_FIRST;
import static com.github.edgarespina.handlebars.StringHelpers.CENTER;
import static com.github.edgarespina.handlebars.StringHelpers.CUT;
import static com.github.edgarespina.handlebars.StringHelpers.DEFAULT;
import static com.github.edgarespina.handlebars.StringHelpers.JOIN;
import static com.github.edgarespina.handlebars.StringHelpers.LJUST;
import static com.github.edgarespina.handlebars.StringHelpers.LOWER;
import static com.github.edgarespina.handlebars.StringHelpers.RJUST;
import static com.github.edgarespina.handlebars.StringHelpers.SLUGIFY;
import static com.github.edgarespina.handlebars.StringHelpers.STRING_FORMAT;
import static com.github.edgarespina.handlebars.StringHelpers.STRIP_TAGS;
import static com.github.edgarespina.handlebars.StringHelpers.UPPER;
import static com.github.edgarespina.handlebars.StringHelpers.WORD_WRAP;
import static com.github.edgarespina.handlebars.StringHelpers.YESNO;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test for {@link StringHelpers}.
 *
 * @author edgar.espina
 * @since 0.2.2
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Options.class, StringHelpers.class, Context.class })
public class TextHelpersTest {

  @Test
  public void capFirst() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("capitalizeFirst", CAPITALIZE_FIRST.helperName());
    assertEquals("Handlebars.java",
        CAPITALIZE_FIRST.apply("handlebars.java", options));

    verify(options);
  }

  @Test
  public void center() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(19);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("center", CENTER.helperName());
    assertEquals("  Handlebars.java  ",
        CENTER.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void centerWithPad() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(19);
    expect(options.hash("pad", " ")).andReturn("*");

    replay(options);

    assertEquals("center", CENTER.helperName());
    assertEquals("**Handlebars.java**",
        CENTER.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void cut() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, " ")).andReturn(" ");

    replay(options);

    assertEquals("cut", CUT.helperName());
    assertEquals("handlebars.java",
        CUT.apply("handle bars .  java", options));

    verify(options);
  }

  @Test
  public void cutNoWhitespace() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, " ")).andReturn("*");

    replay(options);

    assertEquals("cut", CUT.helperName());
    assertEquals("handlebars.java",
        CUT.apply("handle*bars*.**java", options));

    verify(options);
  }

  @Test
  public void defaultStr() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, "")).andReturn("handlebars.java").anyTimes();

    replay(options);

    assertEquals("default", DEFAULT.helperName());
    assertEquals("handlebars.java",
        DEFAULT.apply(null, options));
    assertEquals("handlebars.java",
        DEFAULT.apply(false, options));
    assertEquals("handlebars.java",
        DEFAULT.apply(Collections.emptyList(), options));

    assertEquals("something",
        DEFAULT.apply("something", options));

    verify(options);
  }

  @Test
  public void join() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, null)).andReturn(", ").anyTimes();
    expect(options.hash("prefix", "")).andReturn("").anyTimes();
    expect(options.hash("suffix", "")).andReturn("").anyTimes();

    replay(options);

    assertEquals("join", JOIN.helperName());
    assertEquals("6, 7, 8",
        JOIN.apply(Arrays.asList("6", "7", "8"), options));
    assertEquals("6, 7, 8",
        JOIN.apply(new Object[] {"6", "7", "8" }, options));

    assertEquals(null,
        JOIN.apply("Not an array or iterable", options));
    assertEquals(null, JOIN.apply(null, options));

    verify(options);
  }

  @Test
  public void joinWithPrefixAndSuffix() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, null)).andReturn(", ");
    expect(options.hash("prefix", "")).andReturn("<");
    expect(options.hash("suffix", "")).andReturn(">");

    replay(options);

    assertEquals("<6, 7, 8>",
        JOIN.apply(Arrays.asList("6", "7", "8"), options));

    verify(options);
  }

  @Test
  public void ljust() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(20);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("ljust", LJUST.helperName());
    assertEquals("Handlebars.java     ",
        LJUST.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void ljustWithPad() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(17);
    expect(options.hash("pad", " ")).andReturn("+");

    replay(options);

    assertEquals("ljust", LJUST.helperName());
    assertEquals("Handlebars.java++",
        LJUST.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void rjust() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(20);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("rjust", RJUST.helperName());
    assertEquals("     Handlebars.java",
        RJUST.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void rjustWithPad() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("size")).andReturn(17);
    expect(options.hash("pad", " ")).andReturn("+");

    replay(options);

    assertEquals("rjust", RJUST.helperName());
    assertEquals("++Handlebars.java",
        RJUST.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void lower() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("lower", LOWER.helperName());
    assertEquals("handlebars.java",
        LOWER.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void upper() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("upper", UPPER.helperName());
    assertEquals("HANDLEBARS.JAVA",
        UPPER.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void slugify() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("slugify", SLUGIFY.helperName());
    assertEquals("joel-is-a-slug",
        SLUGIFY.apply("  Joel is a slug  ", options));

    verify(options);
  }

  @Test
  public void stringFormat() throws IOException {
    Options options =
        OptionsMock.options(new Object[] {"handlebars.java" },
            new HashMap<String, Object>());

    assertEquals("stringFormat", STRING_FORMAT.helperName());

    assertEquals("Hello handlebars.java!",
        STRING_FORMAT.apply("Hello %s!", options));
  }

  @Test
  public void stringDecimalFormat() throws IOException {
    Options options =
        OptionsMock.options(new Object[] {10.0 / 3.0 },
            new HashMap<String, Object>());

    assertEquals("stringFormat", STRING_FORMAT.helperName());

    assertEquals("10 / 3 = 3.33",
        STRING_FORMAT.apply("10 / 3 = %.2f", options));
  }

  @Test
  public void stripTags() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("stripTags", STRIP_TAGS.helperName());
    assertEquals("Joel is a slug",
        STRIP_TAGS.apply("<b>Joel</b> <button>is</button> a <span>slug</span>",
            options));

    verify(options);
  }

  @Test
  public void stripTagsMultiLine() throws IOException {
    Options options = PowerMock.createMock(Options.class);

    replay(options);

    assertEquals("stripTags", STRIP_TAGS.helperName());
    assertEquals("Joel\nis a slug",
        STRIP_TAGS.apply(
            "<b>Joel</b>\n<button>is<\n/button> a <span>slug</span>",
            options));

    verify(options);
  }

  @Test
  public void capitalize() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("fully", false)).andReturn(false);
    expect(options.hash("fully", false)).andReturn(true);

    replay(options);

    assertEquals("capitalize", CAPITALIZE.helperName());

    assertEquals("Handlebars Java",
        CAPITALIZE.apply("handlebars java", options));

    assertEquals("Handlebars Java",
        CAPITALIZE.apply("HAndleBars JAVA", options));

    verify(options);
  }

  @Test
  public void abbreviate() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, null)).andReturn(13);

    replay(options);

    assertEquals("abbreviate", ABBREVIATE.helperName());

    assertEquals("Handlebars...",
        ABBREVIATE.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void wordWrap() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.param(0, null)).andReturn(5);

    replay(options);

    assertEquals("wordWrap", WORD_WRAP.helperName());

    assertEquals("Joel\nis a\nslug",
        WORD_WRAP.apply("Joel is a slug", options));

    verify(options);
  }

  @Test
  public void yesno() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("yes", "yes")).andReturn("yes");
    expect(options.hash("no", "no")).andReturn("no");
    expect(options.hash("maybe", "maybe")).andReturn("maybe");

    replay(options);

    assertEquals("yesno", YESNO.helperName());

    assertEquals("yes", YESNO.apply(true, options));
    assertEquals("no", YESNO.apply(false, options));
    assertEquals("maybe", YESNO.apply(null, options));

    verify(options);
  }

  @Test
  public void yesnoCustom() throws IOException {
    Options options = PowerMock.createMock(Options.class);
    expect(options.hash("yes", "yes")).andReturn("yea");
    expect(options.hash("no", "no")).andReturn("nop");
    expect(options.hash("maybe", "maybe")).andReturn("whatever");

    replay(options);

    assertEquals("yesno", YESNO.helperName());

    assertEquals("yea", YESNO.apply(true, options));
    assertEquals("nop", YESNO.apply(false, options));
    assertEquals("whatever", YESNO.apply(null, options));

    verify(options);
  }

}
