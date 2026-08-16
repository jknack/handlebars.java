/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import com.github.jknack.handlebars.ValueResolver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.BigIntegerNode;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import tools.jackson.databind.node.StringNode;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Resolve a context stack value from {@link JsonNode}.
 *
 * @author edgar.espina, jolarsen
 * @since 4.5.5
 */
public enum JsonNodeValueResolver implements ValueResolver {

  /** The singleton instance. */
  INSTANCE;

  @Override
  public Object resolve(final Object context, final String name) {
    Object value = null;
    if (context instanceof ArrayNode arrayNode) {
      try {
        if (name.equals("length")) {
          return arrayNode.size();
        }
        value = resolve(arrayNode.get(Integer.parseInt(name)));
      } catch (NumberFormatException ex) {
        // ignore undefined key and move on, see
        // https://github.com/jknack/handlebars.java/pull/280
      }
    } else if (context instanceof JsonNode jsonNode) {
      value = resolve(jsonNode.get(name));
    }
    return value == null ? UNRESOLVED : value;
  }

  @Override
  public Object resolve(final Object context) {
    if (context instanceof JsonNode jsonNode) {
      return resolve(jsonNode);
    }
    return UNRESOLVED;
  }

  /**
   * Resolve a {@link JsonNode} object to a primitive value.
   *
   * @param node A {@link JsonNode} object.
   * @return A primitive value, json object, json array or null.
   */
  private static Object resolve(final JsonNode node) {
    // binary node
    if (node instanceof BinaryNode binaryNode) {
      return binaryNode.binaryValue();
    }
    // boolean node
    if (node instanceof BooleanNode booleanNode) {
      return booleanNode.booleanValue();
    }
    // null node
    if (node instanceof NullNode) {
      return null;
    }
    // numeric nodes
    if (node instanceof BigIntegerNode bigIntegerNode) {
      return bigIntegerNode.bigIntegerValue();
    }
    if (node instanceof DecimalNode decimalNode) {
      return decimalNode.decimalValue();
    }
    if (node instanceof DoubleNode doubleNode) {
      return doubleNode.doubleValue();
    }
    if (node instanceof IntNode intNode) {
      return intNode.intValue();
    }
    if (node instanceof LongNode longNode) {
      return longNode.longValue();
    }
    // pojo
    if (node instanceof POJONode pojoNode) {
      return pojoNode.getPojo();
    }
    // string
    if (node instanceof StringNode stringNode) {
      return stringNode.stringValue();
    }
    // object node to map
    if (node instanceof ObjectNode objectNode) {
      return toMap(objectNode);
    }
    // container, array or null
    return node;
  }

  /**
   * @param node A json node.
   * @return A map from a json node.
   */
  private static Map<String, Object> toMap(final ObjectNode node) {
    return new AbstractMap<String, Object>() {
      @Override
      public Object get(final Object key) {
        return resolve(node.get((String) key));
      }

      @Override
      public int size() {
        return node.size();
      }

      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public Set<Entry<String, Object>> entrySet() {
        Set set = new LinkedHashSet<Map.Entry<String, Object>>();
        node.propertyStream().forEach(set::add);
        return set;
      }
    };
  }

  @Override
  public Set<Entry<String, Object>> propertySet(final Object context) {
    if (context instanceof ObjectNode node) {
      Map<String, Object> result = new LinkedHashMap<>();
      node.propertyNames().forEach(name -> result.put(name, resolve(node.get(name))));
      return result.entrySet();
    }
    return Collections.emptySet();
  }
}
