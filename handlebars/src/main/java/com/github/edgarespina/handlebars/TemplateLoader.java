package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

/**
 * Locate resource in a resource repository like: classpath, filesystem,
 * network, web context.
 *
 * @param <Location> The location type.
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class TemplateLoader<Location> {

  /**
   * Load the template from a template repository.
   *
   * @param uri The resource's uri. Required.
   * @return The requested resource or {@link #EMPTY} if the resource isn't
   *         found.
   * @throws IOException If the resource cannot be loaded.
   */
  public Reader load(final URI uri) throws IOException {
    checkNotNull(uri, "The uri is required.");
    checkArgument(uri.toString().length() > 0, "The uri is required.");
    Location location = resolve(normalize(uri.toString()));
    Reader reader = read(location);
    if (reader == null) {
      throw new FileNotFoundException(location.toString());
    }
    Handlebars.debug("Resource found: %s", location);
    return reader;
  }

  /**
   * Resolve the uri to an absolute location.
   *
   * @param uri The candidate uri.
   * @return Resolve the uri to an absolute location.
   */
  protected abstract Location resolve(final String uri);

  /**
   * Normalize the uri by removing '/' at the beginning.
   *
   * @param uri The candidate uri.
   * @return A uri without '/' at the beginning.
   */
  private String normalize(final String uri) {
    if (uri.startsWith("/")) {
      return uri.substring(1);
    }
    return uri;
  }

  /**
   * Read the resource from the given URI.
   *
   * @param location The resource's location.
   * @return The requested resource or null if not found.
   * @throws IOException If the resource cannot be loaded.
   */
  protected abstract Reader read(Location location) throws IOException;
}
