package com.github.jknack.handlebars;

import static com.github.jknack.handlebars.StringHelpers.abbreviate;
import static com.github.jknack.handlebars.StringHelpers.capitalize;
import static com.github.jknack.handlebars.StringHelpers.capitalizeFirst;
import static com.github.jknack.handlebars.StringHelpers.center;
import static com.github.jknack.handlebars.StringHelpers.cut;
import static com.github.jknack.handlebars.StringHelpers.defaultIfEmpty;
import static com.github.jknack.handlebars.StringHelpers.ljust;
import static com.github.jknack.handlebars.StringHelpers.lower;
import static com.github.jknack.handlebars.StringHelpers.rjust;
import static com.github.jknack.handlebars.StringHelpers.slugify;
import static com.github.jknack.handlebars.StringHelpers.stringFormat;
import static com.github.jknack.handlebars.StringHelpers.stripTags;
import static com.github.jknack.handlebars.StringHelpers.upper;
import static com.github.jknack.handlebars.StringHelpers.wordWrap;
import static com.github.jknack.handlebars.StringHelpers.yesno;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

/**
 * Unit test for {@link StringHelpers}.
 *
 * @author edgar.espina
 * @since 0.2.2
 */
public class StringHelpersTest extends AbstractTest {

  @Test
  public void capFirst() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("capitalizeFirst", capitalizeFirst.name());
    assertEquals("Handlebars.java",
        capitalizeFirst.apply("handlebars.java", options));

    verify(options);
  }

  @Test
  public void center() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(19);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("center", center.name());
    assertEquals("  Handlebars.java  ",
        center.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void centerWithPad() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(19);
    expect(options.hash("pad", " ")).andReturn("*");

    replay(options);

    assertEquals("center", center.name());
    assertEquals("**Handlebars.java**",
        center.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void cut() throws IOException {
    Options options = createMock(Options.class);
    expect(options.param(0, " ")).andReturn(" ");

    replay(options);

    assertEquals("cut", cut.name());
    assertEquals("handlebars.java",
        cut.apply("handle bars .  java", options));

    verify(options);
  }

  @Test
  public void cutNoWhitespace() throws IOException {
    Options options = createMock(Options.class);
    expect(options.param(0, " ")).andReturn("*");

    replay(options);

    assertEquals("cut", cut.name());
    assertEquals("handlebars.java",
        cut.apply("handle*bars*.**java", options));

    verify(options);
  }

  @Test
  public void defaultStr() throws IOException {
    Options options = createMock(Options.class);
    expect(options.param(0, "")).andReturn("handlebars.java").anyTimes();

    replay(options);

    assertEquals("defaultIfEmpty", defaultIfEmpty.name());
    assertEquals("handlebars.java",
        defaultIfEmpty.apply(null, options));
    assertEquals("handlebars.java",
        defaultIfEmpty.apply(false, options));
    assertEquals("handlebars.java",
        defaultIfEmpty.apply(Collections.emptyList(), options));

    assertEquals("something",
        defaultIfEmpty.apply("something", options));

    verify(options);
  }

  @Test
  public void joinIterable() throws IOException {
    shouldCompileTo("{{{join this \", \"}}}", Arrays.asList("6", "7", "8"),
        $("join", StringHelpers.join), "6, 7, 8");
  }

  @Test
  public void joinIterator() throws IOException {
    shouldCompileTo("{{{join this \", \"}}}", Arrays.asList("6", "7", "8").iterator(),
        $("join", StringHelpers.join), "6, 7, 8");
  }

  @Test
  public void joinArray() throws IOException {
    shouldCompileTo("{{{join this \", \"}}}", new String[]{"6", "7", "8" },
        $("join", StringHelpers.join), "6, 7, 8");
  }

  @Test
  public void joinValues() throws IOException {
    shouldCompileTo("{{{join \"6\" 7 n8 \"-\"}}}", $("n8", 8), $("join", StringHelpers.join),
        "6-7-8");
  }

  @Test
  public void joinWithPrefixAndSuffix() throws IOException {
    shouldCompileTo("{{{join this \", \" prefix='<' suffix='>'}}}", Arrays.asList("6", "7", "8"),
        $("join", StringHelpers.join), "<6, 7, 8>");
  }

  @Test
  public void ljust() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(20);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("ljust", ljust.name());
    assertEquals("Handlebars.java     ",
        ljust.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void ljustWithPad() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(17);
    expect(options.hash("pad", " ")).andReturn("+");

    replay(options);

    assertEquals("ljust", ljust.name());
    assertEquals("Handlebars.java++",
        ljust.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void rjust() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(20);
    expect(options.hash("pad", " ")).andReturn(null);

    replay(options);

    assertEquals("rjust", rjust.name());
    assertEquals("     Handlebars.java",
        rjust.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void rjustWithPad() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("size")).andReturn(17);
    expect(options.hash("pad", " ")).andReturn("+");

    replay(options);

    assertEquals("rjust", rjust.name());
    assertEquals("++Handlebars.java",
        rjust.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void lower() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("lower", lower.name());
    assertEquals("handlebars.java",
        lower.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void upper() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("upper", upper.name());
    assertEquals("HANDLEBARS.JAVA",
        upper.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void slugify() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("slugify", slugify.name());
    assertEquals("joel-is-a-slug",
        slugify.apply("  Joel is a slug  ", options));

    verify(options);
  }

  @Test
  public void stringFormat() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn)
        .setParams(new Object[]{"handlebars.java" })
        .build();

    assertEquals("stringFormat", stringFormat.name());

    assertEquals("Hello handlebars.java!",
        stringFormat.apply("Hello %s!", options));
  }

  @Test
  public void stringDecimalFormat() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn)
        .setParams(new Object[]{10.0 / 3.0 })
        .build();

    assertEquals("stringFormat", stringFormat.name());

    assertEquals(String.format("10 / 3 = %.2f", 10.0 / 3.0),
        stringFormat.apply("10 / 3 = %.2f", options));
  }

  @Test
  public void stripTags() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("stripTags", stripTags.name());
    assertEquals("Joel is a slug",
        stripTags.apply("<b>Joel</b> <button>is</button> a <span>slug</span>",
            options));

    verify(options);
  }

  @Test
  public void stripTagsMultiLine() throws IOException {
    Options options = createMock(Options.class);

    replay(options);

    assertEquals("stripTags", stripTags.name());
    assertEquals("Joel\nis a slug",
        stripTags.apply(
            "<b>Joel</b>\n<button>is<\n/button> a <span>slug</span>",
            options));

    verify(options);
  }

  @Test
  public void capitalize() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("fully", false)).andReturn(false);
    expect(options.hash("fully", false)).andReturn(true);

    replay(options);

    assertEquals("capitalize", capitalize.name());

    assertEquals("Handlebars Java",
        capitalize.apply("handlebars java", options));

    assertEquals("Handlebars Java",
        capitalize.apply("HAndleBars JAVA", options));

    verify(options);
  }

  @Test
  public void abbreviate() throws IOException {
    Options options = createMock(Options.class);
    expect(options.param(0, null)).andReturn(13);

    replay(options);

    assertEquals("abbreviate", abbreviate.name());

    assertEquals("Handlebars...",
        abbreviate.apply("Handlebars.java", options));

    verify(options);
  }

  @Test
  public void wordWrap() throws IOException {
    Options options = createMock(Options.class);
    expect(options.param(0, null)).andReturn(5);

    replay(options);

    assertEquals("wordWrap", wordWrap.name());

    assertEquals("Joel" + SystemUtils.LINE_SEPARATOR + "is a"
        + SystemUtils.LINE_SEPARATOR + "slug",
        wordWrap.apply("Joel is a slug", options));

    verify(options);
  }

  @Test
  public void yesno() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("yes", "yes")).andReturn("yes");
    expect(options.hash("no", "no")).andReturn("no");
    expect(options.hash("maybe", "maybe")).andReturn("maybe");

    replay(options);

    assertEquals("yesno", yesno.name());

    assertEquals("yes", yesno.apply(true, options));
    assertEquals("no", yesno.apply(false, options));
    assertEquals("maybe", yesno.apply(null, options));

    verify(options);
  }

  @Test
  public void yesnoCustom() throws IOException {
    Options options = createMock(Options.class);
    expect(options.hash("yes", "yes")).andReturn("yea");
    expect(options.hash("no", "no")).andReturn("nop");
    expect(options.hash("maybe", "maybe")).andReturn("whatever");

    replay(options);

    assertEquals("yesno", yesno.name());

    assertEquals("yea", yesno.apply(true, options));
    assertEquals("nop", yesno.apply(false, options));
    assertEquals("whatever", yesno.apply(null, options));

    verify(options);
  }

}
