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
package com.github.jknack.handlebars;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BigIntegerNode;
import org.codehaus.jackson.node.BinaryNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DecimalNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.POJONode;
import org.codehaus.jackson.node.TextNode;

/**
 * Resolve a context stack value from {@link JsonNode}.
 *
 * @author edgar.espina
 * @since 0.9.0
 */
public enum JsonNodeValueResolver implements ValueResolver {

  /**
   * The singleton instance.
   */
  INSTANCE;

  @Override
  public Object resolve(final Object context, final String name) {
    Object value = null;
    if (context instanceof ArrayNode) {
      try {
        value = resolve(((ArrayNode) context).get(Integer.parseInt(name)));
      } catch (NumberFormatException ex) {
        // ignore undefined key and move on, see https://github.com/jknack/handlebars.java/pull/280
        value = null;
      }
    } else if (context instanceof JsonNode) {
      value = resolve(((JsonNode) context).get(name));
    }
    return value == null ? UNRESOLVED : value;
  }

  @Override
  public Object resolve(final Object context) {
    if (context instanceof JsonNode) {
      return resolve((JsonNode) context);
    }
    return UNRESOLVED;
  }

  /**
   * Resolve a {@link JsonNode} object to a primitive value.
   *
   * @param node A {@link JsonNode} object.
   * @return A primitive value, json object, json array or null.
   */
  private Object resolve(final JsonNode node) {
    // binary node
    if (node instanceof BinaryNode) {
      return ((BinaryNode) node).getBinaryValue();
    }
    // boolean node
    if (node instanceof BooleanNode) {
      return ((BooleanNode) node).getBooleanValue();
    }
    // null node
    if (node instanceof NullNode) {
      return null;
    }
    // numeric nodes
    if (node instanceof BigIntegerNode) {
      return ((BigIntegerNode) node).getBigIntegerValue();
    }
    if (node instanceof DecimalNode) {
      return ((DecimalNode) node).getDecimalValue();
    }
    if (node instanceof DoubleNode) {
      return ((DoubleNode) node).getDoubleValue();
    }
    if (node instanceof IntNode) {
      return ((IntNode) node).getIntValue();
    }
    if (node instanceof LongNode) {
      return ((LongNode) node).getLongValue();
    }
    // pojo
    if (node instanceof POJONode) {
      return ((POJONode) node).getPojo();
    }
    // string
    if (node instanceof TextNode) {
      return ((TextNode) node).getTextValue();
    }
    // container, array or null
    return node;
  }

  @Override
  public Set<Entry<String, Object>> propertySet(final Object context) {
    if (context instanceof ObjectNode) {
      ObjectNode node = (ObjectNode) context;
      Iterator<String> fieldNames = node.getFieldNames();
      Map<String, Object> result = new LinkedHashMap<String, Object>();
      while (fieldNames.hasNext()) {
        String key = fieldNames.next();
        result.put(key, resolve(node, key));
      }
      return result.entrySet();
    }
    return Collections.emptySet();
  }

}
