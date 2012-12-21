package com.github.jknack.handlebars.internal;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.parboiled.Parboiled;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.Parser;
import com.github.jknack.handlebars.internal.ParserFactory;

public class ParserFactory {

  public static Parser create(final Handlebars handlebars,
      final String filename,
      final String startDelimiter,
      final String endDelimiter) {
    return create(handlebars, filename, null,
        startDelimiter, endDelimiter, new LinkedList<Stacktrace>());
  }

  static Parser create(final Handlebars handlebars,
      final String filename, final Map<String, Partial> partials,
      final String startDelimiter,
      final String endDelimiter,
      final LinkedList<Stacktrace> stacktrace) {
    return new Parser$$parboiled(handlebars, filename,
          partials, startDelimiter, endDelimiter, stacktrace);
  }
}
