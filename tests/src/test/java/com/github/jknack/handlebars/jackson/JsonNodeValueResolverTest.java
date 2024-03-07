/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;

public class JsonNodeValueResolverTest {

  @Test
  public void resolveValueNode() throws IOException {
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
  public void nullMustBeResolvedToUnresolved() {
    Assertions.assertEquals(
        ValueResolver.UNRESOLVED, JsonNodeValueResolver.INSTANCE.resolve(null, "nothing"));
  }

  @Test
  public void resolveBinaryNode() {
    String name = "binary";
    byte[] result = new byte[] {1};

    JsonNode node = Mockito.mock(JsonNode.class);
    BinaryNode value = BinaryNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void resolveNullNode() {
    String name = "null";
    Object result = ValueResolver.UNRESOLVED;

    JsonNode node = Mockito.mock(JsonNode.class);
    NullNode value = NullNode.instance;
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void resolveBigIntegerNode() {
    String name = "bigInt";
    BigInteger result = BigInteger.ONE;

    JsonNode node = Mockito.mock(JsonNode.class);
    JsonNode value = BigIntegerNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void resolveDecimalNode() {
    String name = "decimal";
    BigDecimal result = BigDecimal.TEN;

    JsonNode node = Mockito.mock(JsonNode.class);
    JsonNode value = DecimalNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void resolveLongNode() {
    String name = "long";
    Long result = 678L;

    JsonNode node = Mockito.mock(JsonNode.class);
    JsonNode value = LongNode.valueOf(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void resolvePojoNode() {
    String name = "pojo";
    Object result = new Object();

    JsonNode node = Mockito.mock(JsonNode.class);
    JsonNode value = new POJONode(result);
    when(node.get(name)).thenReturn(value);

    Assertions.assertEquals(result, JsonNodeValueResolver.INSTANCE.resolve(node, name));

    verify(node).get(name);
  }

  @Test
  public void propertySet() throws IOException {
    Map<String, Object> root = new LinkedHashMap<String, Object>();
    root.put("string", "abc");
    root.put("int", 678);
    root.put("double", 3.14d);
    root.put("bool", true);

    Assertions.assertEquals(
        root.entrySet(), JsonNodeValueResolver.INSTANCE.propertySet(node(root)));
  }

  @Test
  public void emptyPropertySet() throws IOException {
    Set<Entry<String, Object>> propertySet =
        JsonNodeValueResolver.INSTANCE.propertySet(new Object());
    assertNotNull(propertySet);
    assertEquals(0, propertySet.size());
  }

  @Test
  public void resolveObjectNode() throws IOException {
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
  public void resolveSimpleArrayNode() throws IOException {
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
  public void resolveArrayNode() throws IOException {
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

  public static JsonNode node(final Object object) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(mapper.writeValueAsString(object));
    return node;
  }

  public static Context context(final Object object) throws IOException {
    return Context.newBuilder(node(object))
        .resolver(MapValueResolver.INSTANCE, JsonNodeValueResolver.INSTANCE)
        .build();
  }
}
