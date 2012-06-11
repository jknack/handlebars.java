package com.github.edgarespina.handlerbars.io;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import javax.servlet.ServletContext;

import com.github.edgarespina.handlerbars.ResourceLocator;

public class WebResourceLocator extends ResourceLocator {

  private final String basepath;

  private final ServletContext servletContext;

  public WebResourceLocator(final ServletContext servletContext) {
    this.servletContext =
        notNull(servletContext, "The servlet context is required.");
    this.basepath = "/";
  }

  @Override
  protected Reader read(final URI uri) throws IOException {
    String path = basepath + uri;
    InputStream input = servletContext.getResourceAsStream(path);
    if (input == null) {
      throw new IOException("Not found: " + path);
    }
    return new InputStreamReader(input);
  }

}
