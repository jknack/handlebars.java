package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public abstract class ResourceLocator {

  private static final StringReader EMPTY = new StringReader("");

  public Reader locate(final String uri) throws IOException {
    notEmpty(uri, "The uri is required.");
    Reader reader = read(normalize(uri));
    if (reader == null) {
      return EMPTY;
    }
    return reader;
  }

  private String normalize(final String uri) {
    if (uri.startsWith("/")) {
      return uri.substring(1);
    }
    return uri;
  }

  protected abstract Reader read(String uri) throws IOException;
}
