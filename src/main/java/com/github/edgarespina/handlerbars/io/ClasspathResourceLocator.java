package com.github.edgarespina.handlerbars.io;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class ClasspathResourceLocator extends ResourceLocator {

  private String basepath;

  public ClasspathResourceLocator(final String basepath) {
    this.basepath = notEmpty(basepath, "The base path is required.");
    if (!this.basepath.endsWith("/")) {
      this.basepath += "/";
    }
  }

  public ClasspathResourceLocator() {
    this("/");
  }

  @Override
  protected Reader read(final String uri) throws IOException {
    String path = basepath + uri;
    InputStream input = getClass().getResourceAsStream(path);
    if (input == null) {
      throw new IOException("Not found: " + path);
    }
    return new InputStreamReader(input);
  }

}
