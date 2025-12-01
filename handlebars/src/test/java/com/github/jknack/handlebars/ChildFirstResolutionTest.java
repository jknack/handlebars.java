/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ChildFirstResolutionTest extends AbstractTest {

  @Override
  protected void configure(final Handlebars handlebars) {
    handlebars.childFirstResolution(true);
  }

  @Test
  public void testNestedWithChildFirst() throws IOException {
    shouldCompileTo(
        "{{x}} {{#with a}}{{x}} {{#with b}}{{x}}{{/with}}{{/with}}",
        $("x", "1", "a", $("x", "2", "b", $("x", "3"))),
        "1 2 3");
  }

  @Test
  public void testBlockParamsChildFirst() throws IOException {
    shouldCompileTo(
        "{{#each items as |item|}}{{name}}{{/each}}",
        $("name", "Global", "items", new Object[] {$("name", "Item1"), $("name", "Item2")}),
        "Item1Item2");
  }

  @Test
  public void testNestedContextsChildFirst() throws IOException {
    shouldCompileTo(
        "{{#each outer}}{{#with inner}}{{field}}{{/with}}{{/each}}",
        $(
            "field",
            "Root",
            "outer",
            new Object[] {$("field", "Outer1", "inner", $("field", "Inner1"))}),
        "Inner1");
  }

  @Test
  public void testMixedContexts() throws IOException {
    shouldCompileTo(
        "{{#with person}}{{#if active}}{{name}}{{/if}}{{/with}}",
        $("name", "Outer", "person", $("name", "Inner", "active", true)),
        "Inner");
  }

  @Test
  public void testChildFirstConfiguration() {
    Handlebars handlebars = new Handlebars();
    assertFalse(handlebars.childFirstResolution());

    handlebars.setChildFirstResolution(true);
    assertTrue(handlebars.childFirstResolution());

    Handlebars handlebars2 = new Handlebars().childFirstResolution(true);
    assertTrue(handlebars2.childFirstResolution());
  }

  @Test
  public void testEachWithIndexChildFirst() throws IOException {
    shouldCompileTo(
        "{{#each items as |item index|}}{{index}}:{{value}} {{/each}}",
        $("value", "Global", "items", new Object[] {$("value", "A"), $("value", "B")}),
        "0:A 1:B ");
  }

  @Test
  public void testPartialWithChildFirst() throws IOException {
    shouldCompileToWithPartials(
        "{{> partial name=\"Override\"}}",
        $("name", "Original"),
        $("partial", "{{name}}"),
        "Override");
  }

  @Test
  public void testWithHelperMapData() throws IOException {
    shouldCompileTo(
        "Name: {{name}}\n{{#with person}}Name: {{name}}\nAge: {{age}}{{/with}}",
        $("name", "Outer", "person", $("name", "Inner", "age", 30)),
        "Name: Outer\nName: Inner\nAge: 30");
  }

  @Test
  public void testEachItemsWithGlobalField() throws IOException {
    shouldCompileTo(
        "{{#each items}}Global: {{globalField}}\nItem: {{name}}\n{{/each}}",
        $(
            "globalField",
            "Global Value",
            "items",
            new Object[] {
              $("name", "Item 1", "globalField", "Item 1 Global"),
              $("name", "Item 2", "globalField", "Item 2 Global")
            }),
        "Global: Item 1 Global\nItem: Item 1\nGlobal: Item 2 Global\nItem: Item 2\n");
  }

  @Test
  public void testDeepNestingChildFirst() throws IOException {
    shouldCompileTo(
        "{{#with a}}{{#with b}}{{#with c}}{{value}}{{/with}}{{/with}}{{/with}}",
        $(
            "value",
            "root",
            "a",
            $("value", "a-value", "b", $("value", "b-value", "c", $("value", "c-value")))),
        "c-value");
  }

  @Test
  public void testLocalPathBehavior() throws IOException {
    shouldCompileTo(
        "{{#with child}}{{./name}}{{/with}}",
        $("name", "Parent", "child", $("name", "Child")),
        "Child");
  }
}
