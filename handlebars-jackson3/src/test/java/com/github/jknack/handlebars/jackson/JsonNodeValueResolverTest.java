/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.BigIntegerNode;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.POJONode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JsonNodeValueResolverTest {

  @Test
  void resolveValueNode() throws IOException {
    Handlebars handlebars = new Handlebars();

    Map<String, Object> root = new LinkedHashMap<String, Object>();
    root.put("string", "abc");
    root.put("int", 678);
    root.put("long", 6789L);
    root.put("float", 7.13f);
    root.put("double", 3.14d);
    root.put("bool", true);

    assertEquals(
        "abc 678 6789 7.13 3.14 true",
        handlebars
            .compileInline("{{string}} {{int}} {{long}} {{float}} {{double}} {{bool}}")
            .apply(context(root)));
  }

  @Test
  void nullMustBeResolvedToUnresolved() {
    Assertions.assertEquals(
        ValueResolver.UNRESOLVED, JsonNodeValueResolver.INSTANCE.resolve(null, "nothing"));
  }

  @Test
  void resolveBinaryNode() {
    String name = "binary";
    byte[] result = new byte[] {1};

    JsonNode node = mock(JsonNode.class);
    BinaryNode value = BinaryNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void resolveNullNode() {
    String name = "null";
    Object result = ValueResolver.UNRESOLVED;

    JsonNode node = mock(JsonNode.class);
    NullNode value = NullNode.instance;
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void resolveBigIntegerNode() {
    String name = "bigInt";
    BigInteger result = BigInteger.ONE;

    JsonNode node = mock(JsonNode.class);
    JsonNode value = BigIntegerNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void resolveDecimalNode() {
    String name = "decimal";
    BigDecimal result = BigDecimal.TEN;

    JsonNode node = mock(JsonNode.class);
    JsonNode value = DecimalNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void resolveLongNode() {
    String name = "long";
    long result = 678L;

    JsonNode node = mock(JsonNode.class);
    JsonNode value = LongNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void resolvePojoNode() {
    String name = "pojo";
    Object result = new Object();

    JsonNode node = mock(JsonNode.class);
    JsonNode value = new POJONode(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  void propertySet() {
    Map<String, Object> root = new LinkedHashMap<String, Object>();
    root.put("string", "abc");
    root.put("int", 678);
    root.put("double", 3.14d);
    root.put("bool", true);

    Assertions.assertEquals(
        root.entrySet(), JsonNodeValueResolver.INSTANCE.propertySet(node(root)));
  }

  @Test
  void emptyPropertySet() {
    Set<Entry<String, Object>> propertySet =
        JsonNodeValueResolver.INSTANCE.propertySet(new Object());
    assertNotNull(propertySet);
    assertEquals(0, propertySet.size());
  }

  @Test
  void resolveObjectNode() throws IOException {
    Handlebars handlebars = new Handlebars();
    Object item =
        new Object() {
          @SuppressWarnings("unused")
          public String getKey() {
            return "pojo";
          }
        };

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("pojo", item);

    assertEquals("pojo", handlebars.compileInline("{{pojo.key}}").apply(context(root)));
  }

  @Test
  void resolveSimpleArrayNode() throws IOException {
    Handlebars handlebars = new Handlebars();

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("array", new Object[] {1, 2, 3});

    assertEquals(
        "123",
        handlebars.compileInline("{{array.[0]}}{{array.[1]}}{{array.[2]}}").apply(context(root)));
    assertEquals(
        "123", handlebars.compileInline("{{#array}}{{this}}{{/array}}").apply(context(root)));
  }

  @Test
  void resolveArrayNode() throws IOException {
    Handlebars handlebars = new Handlebars();

    Object item =
        new Object() {
          @SuppressWarnings("unused")
          public String getKey() {
            return "pojo";
          }
        };

    Map<String, Object> root = new HashMap<String, Object>();
    root.put("array", new Object[] {item});

    assertEquals("pojo", handlebars.compileInline("{{array.[0].key}}").apply(context(root)));
    assertEquals(
        "pojo", handlebars.compileInline("{{#array}}{{key}}{{/array}}").apply(context(root)));
  }

  public static JsonNode node(final Object object) {
    JsonMapper mapper = JsonMapper.builder().build();
    JsonNode node = mapper.readTree(mapper.writeValueAsString(object));
    return node;
  }

  public static Context context(final Object object) {
    return Context.newBuilder(node(object))
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
        .build();
  }
}
