package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

public abstract class ResourceLocator {

  private static final StringReader EMPTY = new StringReader("");

  public Reader locate(final URI uri) throws IOException {
    notNull(uri, "The uri is required.");
    notEmpty(uri.toString(), "The uri is required.");
    Reader reader = read(normalize(uri.toString()));
    if (reader == null) {
      return EMPTY;
    }
    return reader;
  }

  private URI normalize(final String uri) {
    if (uri.startsWith("/")) {
      return URI.create(uri.substring(1));
    }
    return URI.create(uri);
  }

  protected abstract Reader read(URI uri) throws IOException;
}
