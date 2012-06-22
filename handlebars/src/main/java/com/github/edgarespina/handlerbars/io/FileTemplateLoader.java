package com.github.edgarespina.handlerbars.io;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.github.edgarespina.handlerbars.TemplateLoader;

/**
 * Load templates from the file system. A base path must be specified at
 * creation time. The base path serve as template repository.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileTemplateLoader extends TemplateLoader<File> {

  /**
   * The base directory.
   */
  private final File basedir;

  /**
   * Creates a new {@link FileTemplateLoader}.
   *
   * @param basedir The base directory. Required.
   */
  public FileTemplateLoader(final File basedir) {
    this.basedir = checkNotNull(basedir, "The base dir is required.");
    checkArgument(basedir.exists(), "File not found: %s", basedir);
    checkArgument(basedir.isDirectory(), "A directory is required: %s",
        basedir);
  }

  @Override
  protected File resolve(final String uri) {
    return new File(basedir, uri);
  }

  @Override
  protected Reader read(final File file) throws IOException {
    if (!file.exists()) {
      return null;
    }
    return new FileReader(file);
  }

}
