package com.github.edgarespina.handlerbars.io;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class FileResourceLocator extends ResourceLocator {

  private final File basedir;

  public FileResourceLocator(final File basedir) {
    this.basedir = notNull(basedir, "The base dir is required.");
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    File file = new File(basedir, uri.toString());
    if (file.exists()) {
      throw new IOException("Not found: " + file);
    }
    return new FileReader(file);
  }

}
