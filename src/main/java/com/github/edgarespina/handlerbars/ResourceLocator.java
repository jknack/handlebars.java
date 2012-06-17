package com.github.edgarespina.handlerbars;

import static org.parboiled.common.Preconditions.checkArgument;
import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

/**
 * Locate resource in a resource repository like: classpath, filesystem,
 * network, web context.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class ResourceLocator {

  /**
   * Default resource if cannot be found.
   */
  public static final StringReader EMPTY = new StringReader("");

  /**
   * Locate the resource from a resource repository.
   *
   * @param uri The resource's uri. Required.
   * @return The requested resource or {@link #EMPTY} if the resource isn't
   *         found.
   * @throws IOException If the resource cannot be loaded.
   */
  public Reader locate(final URI uri) throws IOException {
    checkNotNull(uri, "The uri is required.");
    checkArgument(uri.toString().length() > 0, "The uri is required.");
    Reader reader = read(normalize(uri.toString()));
    if (reader == null) {
      Handlebars.warn("Resource not found: {} using an empty resource.", uri);
      return EMPTY;
    }
    return reader;
  }

  /**
   * Normalize the uri by removing '/' at the beginning.
   *
   * @param uri The candidate uri.
   * @return A uri without '/' at the beginning.
   */
  private URI normalize(final String uri) {
    if (uri.startsWith("/")) {
      return URI.create(uri.substring(1));
    }
    return URI.create(uri);
  }

  /**
   * Read the resource from the given URI.
   *
   * @param uri The resource's location.
   * @return The requested resource or null if not found.
   * @throws IOException If the resource cannot be loaded.
   */
  protected abstract Reader read(URI uri) throws IOException;
}
