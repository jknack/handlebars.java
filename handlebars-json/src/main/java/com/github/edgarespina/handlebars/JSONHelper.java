package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Format the context object as JSON.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class JSONHelper implements Helper<Object> {

  /**
   * The JSON parser.
   */
  private final ObjectMapper mapper;

  /**
   * Creates a new {@link JSONHelper}.
   *
   * @param objectMapper The object's mapper. Required.
   */
  public JSONHelper(final ObjectMapper objectMapper) {
    this.mapper = checkNotNull(objectMapper, "The object mapper is required.");
  }

  /**
   * Creates a new {@link JSONHelper}.
   */
  public JSONHelper() {
    this(new ObjectMapper());
  }

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return "";
    }
    return new Handlebars.SafeString(mapper.writeValueAsString(context));
  }

}
