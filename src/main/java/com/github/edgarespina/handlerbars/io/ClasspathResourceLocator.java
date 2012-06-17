package com.github.edgarespina.handlerbars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class ClasspathResourceLocator extends ResourceLocator {

  private String basepath;

  public ClasspathResourceLocator(final String basepath) {
    checkNotNull(basepath, "A base path is required.");
    checkArgument(basepath.length() > 0, "A base path is required.");
    this.basepath = basepath;
    if (!this.basepath.endsWith("/")) {
      this.basepath += "/";
    }
  }

  public ClasspathResourceLocator() {
    this("/");
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    String path = basepath + uri;
    InputStream input = getClass().getResourceAsStream(path);
    if (input == null) {
      throw new IOException("Not found: " + path);
    }
    return new InputStreamReader(input);
  }

}
