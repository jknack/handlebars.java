/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.io;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * Load templates from the file system. A base path must be specified at
 * creation time. The base path serve as template repository.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileTemplateLoader extends URLTemplateLoader {

  /**
   * Creates a new {@link FileTemplateLoader}.
   *
   * @param basedir The base directory. Required.
   * @param suffix The view suffix. Required.
   */
  public FileTemplateLoader(final File basedir, final String suffix) {
    notNull(basedir, "The base dir is required.");
    isTrue(basedir.exists(), "File not found: %s", basedir);
    isTrue(basedir.isDirectory(), "A directory is required: %s",
        basedir);
    setPrefix(basedir.toString());
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link FileTemplateLoader}.
   *
   * @param basedir The base directory. Required.
   */
  public FileTemplateLoader(final File basedir) {
    this(basedir, DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link FileTemplateLoader}.
   *
   * @param basedir The base directory. Required.
   * @param suffix The view suffix. Required.
   */
  public FileTemplateLoader(final String basedir, final String suffix) {
    setPrefix(basedir);
    setSuffix(suffix);
  }

  /**
   * Creates a new {@link FileTemplateLoader}.
   *
   * @param basedir The base directory. Required.
   */
  public FileTemplateLoader(final String basedir) {
    this(basedir, DEFAULT_SUFFIX);
  }

  @Override
  protected URL getResource(final String location) throws IOException {
    File file = new File(location);
    return file.exists() ? file.toURI().toURL() : null;
  }
}
