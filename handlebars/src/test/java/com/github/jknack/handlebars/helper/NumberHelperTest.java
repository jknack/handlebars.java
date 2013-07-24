package com.github.jknack.handlebars.helper;

import static com.github.jknack.handlebars.helper.NumberHelper.isEven;
import static com.github.jknack.handlebars.helper.NumberHelper.isOdd;
import static com.github.jknack.handlebars.helper.NumberHelper.stripes;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

public class NumberHelperTest extends AbstractTest {

  @Test
  public void isOdd() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn).setParams(
        new Object[] { "rightBox" }).build();

    assertEquals("isOdd", isOdd.name());
    assertEquals("rightBox", isOdd.apply(3, options));
  }

  @Test
  public void isEven() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn).setParams(
        new Object[] {"leftBox"}).build();

    assertEquals("isEven", isEven.name());
    assertEquals("leftBox", isEven.apply(2, options));
  }

  @Test
  public void stripesWithOddParameter() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn).setParams(
        new Object[] {"leftBox", "rightBox"}).build();

    assertEquals("stripes", stripes.name());
    assertEquals("leftBox", stripes.apply(2, options));
  }

  @Test
  public void stripesWithEvenParameter() throws IOException {
    Handlebars hbs = createMock(Handlebars.class);
    Context ctx = createMock(Context.class);
    Template fn = createMock(Template.class);

    Options options = new Options.Builder(hbs, TagType.VAR, ctx, fn).setParams(
        new Object[] { "leftBox", "rightBox" }).build();

    assertEquals("stripes", stripes.name());
    assertEquals("rightBox", stripes.apply(3, options));
  }
}
