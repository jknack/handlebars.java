/**
 * Copyright (c) 2012-2015 Edgar Espina
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

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.github.jknack.handlebars.Handlebars;

/**
 * An {@link URL} {@link TemplateSource}.
 *
 * @author edgar.espina
 * @since 0.11.0
 */
public class URLTemplateSource extends AbstractTemplateSource {

  /**
   * The resource. Required.
   */
  private URL resource;

  /**
   * The last modified date.
   */
  private Long lastModified;

  /**
   * The file's name.
   */
  private String filename;

  /**
   * Creates a new {@link URLTemplateSource}.
   *
   * @param filename The file's name.
   * @param resource The resource. Required.
   */
  public URLTemplateSource(final String filename, final URL resource) {
    this.filename = notEmpty(filename, "The filename is required.");
    this.resource = notNull(resource, "A resource is required.");
  }

  @Override
  public String content() throws IOException {
    Reader reader = null;
    final int bufferSize = 1024;
    try {
      reader = reader();
      char[] cbuf = new char[bufferSize];
      StringBuilder sb = new StringBuilder(bufferSize);
      int len;
      while ((len = reader.read(cbuf, 0, bufferSize)) != -1) {
        sb.append(cbuf, 0, len);
      }
      return sb.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public long lastModified() {
    synchronized (this) {
      if (lastModified == null) {
        lastModified = lastModified(resource);
      }
      return lastModified;
    }
  }

  /**
   * Open the stream under the URL.
   *
   * @return A reader.
   * @throws IOException If the stream can't be opened.
   */
  protected Reader reader() throws IOException {
    InputStream in = resource.openStream();
    return new InputStreamReader(in, "UTF-8");
  }

  /**
   * Read the last modified date from a resource.
   *
   * @param resource The resource.
   * @return The last modified date from a resource.
   * @throws IOException
   */
  private long lastModified(final URL resource) {
    URLConnection uc = null;
    try {
      uc = resource.openConnection();
      if (uc instanceof JarURLConnection) {
        URL jarURL = ((JarURLConnection) uc).getJarFileURL();
        if (jarURL.getProtocol().equals("file")) {
          uc = null;
          String file = jarURL.getFile();
          return new File(file).lastModified();
        }
      }
      return uc.getLastModified();
    } catch (IOException ex) {
      Handlebars.warn("Can't get last modified date of: %s", resource);
      return -1;
    } finally {
      try {
        if (uc != null) {
          // http://stackoverflow.com/questions/2057351/how-do-i-get-the-last-modification-time-of
          // -a-java-resource
          InputStream is = uc.getInputStream();
          if (is != null) {
            is.close();
          }
        }
      } catch (IOException e) {
        Handlebars.warn("Can't close: %s", resource);
      }
    }
  }

}
