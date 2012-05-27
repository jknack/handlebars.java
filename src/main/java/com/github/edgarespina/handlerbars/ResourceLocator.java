package com.github.edgarespina.handlerbars;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.io.Reader;

public abstract class ResourceLocator {

  public Reader locate(final String uri) throws IOException {
    notEmpty(uri, "The uri is required.");
    return read(normalize(uri));
  }

  private String normalize(final String uri) {
    if (uri.startsWith("/")) {
      return uri.substring(1);
    }
    return uri;
  }

  protected abstract Reader read(String uri) throws IOException;
}
