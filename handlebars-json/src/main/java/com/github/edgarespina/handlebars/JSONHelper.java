package com.github.edgarespina.handlebars;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 * Format the context object as JSON.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class JSONHelper implements Helper<Object> {

  /**
   * A singleton version of {@link JSONHelper}.
   */
  public static final Helper<Object> INSTANCE = new JSONHelper();

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
  private JSONHelper() {
    this(new ObjectMapper());
  }

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    if (context == null) {
      return "";
    }
    String viewName = options.hash("view", "");
    final ObjectWriter writer;
    if (viewName.length() > 0) {
      try {
        Class<?> viewClass = Class.forName(viewName);
        writer = mapper.writerWithView(viewClass);
      } catch (ClassNotFoundException ex) {
        throw new IllegalArgumentException(viewName, ex);
      }
    } else {
      writer = mapper.writer();
    }
    return new Handlebars.SafeString(writer.writeValueAsString(context));
  }

}
