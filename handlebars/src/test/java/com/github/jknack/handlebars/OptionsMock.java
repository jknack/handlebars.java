package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.Map;

import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

public class OptionsMock extends Options {

  private OptionsMock(final Handlebars handlebars, final Context context,
      final Template fn,
      final Template inverse, final Object[] params,
      final Map<String, Object> hash) {
    super(handlebars, context, fn, inverse, params, hash);
  }

  public static Options options(final Object[] params,
      final Map<String, Object> hash) {
    Handlebars handlebars = EasyMock.createMock(Handlebars.class);
    Context context = PowerMock.createMock(Context.class);
    Template fn = PowerMock.createMock(Template.class);
    Template inverse = PowerMock.createMock(Template.class);
    return new OptionsMock(handlebars, context, fn, inverse, params, hash);
  }

  @Override
  public CharSequence fn() throws IOException {
    return null;
  }

  @Override
  public Context wrap(final Object model) {
    return null;
  }

  @Override
  public CharSequence fn(final Object context) throws IOException {
    return null;
  }

  @Override
  public CharSequence inverse() throws IOException {
    return null;
  }

  @Override
  public CharSequence inverse(final Object context) throws IOException {
    return null;
  }

  @Override
  public CharSequence apply(final Template template, final Object context)
      throws IOException {
    return null;
  }

  @Override
  public CharSequence apply(final Template template) throws IOException {
    return null;
  }

  @Override
  public <T> T get(final String name) {
    return null;
  }

  @Override
  public Template partial(final String path) {
    return null;
  }

  @Override
  public void partial(final String path, final Template partial) {
  }

}
