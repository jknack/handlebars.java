package com.github.edgarespina.handlerbars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import com.github.edgarespina.handlerbars.ResourceLocator;

/**
 * A resource locator that let you load files from the file system. A base path
 * must be specified at creation time. The base path serve as template
 * repository.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileLocator extends ResourceLocator {

  /**
   * The base directory.
   */
  private final File basedir;

  /**
   * Creates a new {@link FileLocator}.
   *
   * @param basedir The base directory. Required.
   */
  public FileLocator(final File basedir) {
    this.basedir = checkNotNull(basedir, "The base dir is required.");
    checkArgument(basedir.exists(), "File not found: %s", basedir);
    checkArgument(basedir.isDirectory(), "A directory is required: %s",
        basedir);
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    File file = new File(basedir, uri.toString());
    if (!file.exists()) {
      return null;
    }
    return new FileReader(file);
  }

}
