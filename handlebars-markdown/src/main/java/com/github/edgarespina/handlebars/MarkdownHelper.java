package com.github.edgarespina.handlebars;

import java.io.IOException;

import org.pegdown.PegDownProcessor;

/**
 * A markdown helper.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class MarkdownHelper implements Helper<Object> {

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    String markdown = (String) context;
    PegDownProcessor processor = new PegDownProcessor();
    return new Handlebars.SafeString(processor.markdownToHtml(markdown));
  }

}
