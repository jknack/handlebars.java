/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for preserveParentContext configuration.
 *
 * <p>This test verifies the behavior difference between:
 * <ul>
 *   <li>Traditional mode (preserveParentContext=false): Creates a new PartialCtx for each partial</li>
 *   <li>Preserve mode (preserveParentContext=true): Preserves the parent context chain</li>
 * </ul>
 */
public class PartialContextModeTest extends AbstractTest {

  /**
   * Test default mode preserves traditional behavior.
   *
   * <p>In default mode, {{..}} in a partial references the partial call site,
   * not the parent of the current context.
   */
  @Test
  public void testDefaultMode_PreservesBehavior() throws IOException {
    Handlebars hbs = new Handlebars();

    // Verify default configuration is false for backward compatibility
    assertEquals(false, hbs.preserveParentContext());
  }

  /**
   * Test setting preserveParentContext via fluent API.
   */
  @Test
  public void testFluentAPI() throws IOException {
    Handlebars hbs = new Handlebars();

    // Default should be false
    assertEquals(false, hbs.preserveParentContext());

    // Set to true via fluent API
    hbs.preserveParentContext(true);
    assertEquals(true, hbs.preserveParentContext());

    // Set back to false
    hbs.preserveParentContext(false);
    assertEquals(false, hbs.preserveParentContext());
  }

  /**
   * Test setting preserveParentContext via setter.
   */
  @Test
  public void testSetter() throws IOException {
    Handlebars hbs = new Handlebars();

    // Default should be false
    assertEquals(false, hbs.preserveParentContext());

    // Set to true via setter
    hbs.setPreserveParentContext(true);
    assertEquals(true, hbs.preserveParentContext());

    // Set back to false
    hbs.setPreserveParentContext(false);
    assertEquals(false, hbs.preserveParentContext());
  }

  /**
   * Test backward compatibility - default value should be false.
   */
  @Test
  public void testBackwardCompatibility_DefaultValue() throws IOException {
    Handlebars hbs = new Handlebars();

    // Default value should be false for backward compatibility
    assertEquals(false, hbs.preserveParentContext());
  }

  // ========================================
  // Render tests - actual template rendering
  // ========================================

  /**
   * Test rendering with {{> partial this}} in default mode.
   *
   * <p>Default mode creates a new PartialCtx, so {{../name}} in partial references the call site.
   */
  @Test
  public void testRender_DefaultMode_ThisContext() throws IOException {
    // In default mode, {{../name}} in partial references the partial call context
    shouldCompileToWithPartials(
        "{{name}} {{> myPartial this}}",
        $("name", "root"),
        $("myPartial", "{{../name}}"),
        "root root");
  }

  /**
   * Test that preserveParentContext=false doesn't affect non-partial context navigation.
   *
   * <p>This ensures backward compatibility - normal {{../name}} usage outside partials is unaffected.
   */
  @Test
  public void testRender_DefaultMode_NormalParentNavigation() throws IOException {
    // Normal {{../name}} navigation should still work
    shouldCompileTo(
        "{{#with child}}{{childValue}} {{../rootValue}}{{/with}}",
        $("rootValue", "fromRoot", "child", $("childValue", "fromChild")),
        "fromChild fromRoot");
  }

  /**
   * Nested test class for preserve mode tests.
   *
   * <p>This allows us to override newHandlebars() to return a configured instance.
   */
  public static class PreserveModeTest extends AbstractTest {

    @Override
    protected Handlebars newHandlebars() {
      return new Handlebars().preserveParentContext(true);
    }

    /**
     * Test rendering with {{> partial}} (implicit this) in preserve mode.
     *
     * <p>When preserveParentContext is enabled, {{> partial}} uses current context directly.
     */
    @Test
    public void testRender_PreserveMode_ImplicitThis() throws IOException {
      // {{> partial}} with no context parameter should work like {{> partial this}}
      shouldCompileToWithPartials(
          "{{name}} {{> myPartial}}",
          $("name", "root", "value", "rootValue"),
          $("myPartial", "{{name}}"),
          "root root");
    }

    /**
     * Test rendering with {{> partial this}} in preserve mode.
     *
     * <p>When preserveParentContext is enabled, {{> partial this}} uses current context directly.
     */
    @Test
    public void testRender_PreserveMode_ThisContext() throws IOException {
      // {{> partial this}} should use the current context directly
      shouldCompileToWithPartials(
          "{{name}} {{> myPartial this}}",
          $("name", "root", "value", "rootValue"),
          $("myPartial", "{{name}}"),
          "root root");
    }

    /**
     * Test rendering with {{> partial ..}} in preserve mode.
     *
     * <p>When preserveParentContext is enabled, {{> partial ..}} should use parent context.
     * This verifies that the partial can access parent properties.
     */
    @Test
    public void testRender_PreserveMode_SingleParent() throws IOException {
      // Create a context where root has rootValue and child has childValue
      // When inside child context, {{> partial ..}} should access root context
      shouldCompileToWithPartials(
          "{{rootValue}} {{#with child}}{{childValue}} {{> myPartial ..}}{{/with}}",
          $(
              "rootValue", "fromRoot",
              "child", $("childValue", "fromChild")),
          $("myPartial", "{{rootValue}}"),
          "fromRoot fromChild fromRoot");
    }

    /**
     * Test rendering with {{> partial ../..}} in preserve mode (multi-level parent).
     *
     * <p>When preserveParentContext is enabled, {{> partial ../..}} should navigate up two levels.
     */
    @Test
    public void testRender_PreserveMode_MultiLevelParent() throws IOException {
      // Create a triple-nested context structure
      // Root -> Level1 -> Level2
      // {{> myPartial ../..}} from Level2 should access Root
      shouldCompileToWithPartials(
          "{{rootValue}} {{#with level1}}{{#with level2}}{{level2Value}} {{> myPartial ../..}}{{/with}}{{/with}}",
          $(
              "rootValue", "fromRoot",
              "level1", $("level1Value", "fromLevel1"),
              "level2", $("level2Value", "fromLevel2")),
          $("myPartial", "{{rootValue}}"),
          "fromRoot fromLevel2 fromRoot");
    }

    /**
     * Test rendering - preserve mode with {{> partial this}}.
     *
     * <p>When preserveParentContext is enabled, {{../name}} in partial with 'this' context
     * references the original parent.
     */
    @Test
    public void testRender_PreserveMode_WithThis() throws IOException {
      // Create a context where root has a name property at top level
      // Inside {{#with root}}, {{../name}} should access the top-level name
      shouldCompileToWithPartials(
          "{{name}} {{#with root}}{{name}} {{> myPartial this}}{{/with}}",
          $(
              "name", "rootName",
              "root", $("name", "rootValue")),
          $("myPartial", "{{../name}}"),
          "rootName rootValue rootName");
    }

    /**
     * Test rendering with nested partials in preserve mode.
     *
     * <p>Verifies that nested partial calls maintain the correct parent chain.
     */
    @Test
    public void testRender_PreserveMode_NestedPartials() throws IOException {
      // myPartial calls anotherPartial
      // Both should maintain correct parent context
      shouldCompileToWithPartials(
          "{{rootValue}} {{> myPartial}}",
          $("rootValue", "fromRoot", "nestedValue", "fromNested"),
          $(
              "myPartial", "{{rootValue}} {{> anotherPartial this}}",
              "anotherPartial", "{{nestedValue}}"),
          "fromRoot fromRoot fromNested");
    }

    /**
     * Test rendering with complex nested structure in preserve mode.
     *
     * <p>Verifies correct context navigation through multiple nesting levels.
     */
    @Test
    public void testRender_PreserveMode_ComplexNesting() throws IOException {
      // Root -> outer -> inner
      // From inner, use partial with ../.. to access root
      shouldCompileToWithPartials(
          "{{root}} {{#with outer}}{{outer}} {{#with inner}}{{inner}} {{> myPartial ../..}}{{/with}}{{/with}}",
          $(
              "root", "ROOT",
              "outer", $("outer", "OUTER"),
              "inner", $("inner", "INNER")),
          $("myPartial", "{{root}}"),
          "ROOT OUTER INNER ROOT");
    }

    /**
     * Test rendering with partial accessing parent properties directly.
     *
     * <p>Verifies that partial can access parent context properties when preserveParentContext is enabled.
     */
    @Test
    public void testRender_PreserveMode_PartialAccessesParentProperties() throws IOException {
      // Partial should be able to access parent context properties
      shouldCompileToWithPartials(
          "{{title}} {{#with content}}{{body}} {{> myPartial ..}}{{/with}}",
          $(
              "title", "MyTitle",
              "content", $("body", "MyBody"),
              "footer", "MyFooter"),
          $("myPartial", "{{title}}:{{footer}}"),
          "MyTitle MyBody MyTitle:MyFooter");
    }

    /**
     * Test rendering with {{> partial ../../}} in preserve mode (three levels up).
     *
     * <p>Verifies navigation up three context levels.
     */
    @Test
    public void testRender_PreserveMode_ThreeLevelParent() throws IOException {
      // Create a four-nested context structure
      // Top -> Level1 -> Level2 -> Level3
      // {{> myPartial ../../..}} from Level3 should access Top
      shouldCompileToWithPartials(
          "{{topLevel}} {{#with level1}}{{#with level2}}{{#with level3}}{{level3Value}} {{> myPartial ../../..}}{{/with}}{{/with}}{{/with}}",
          $(
              "topLevel", "TOP",
              "level1", $("level1Value", "fromLevel1"),
              "level2", $("level2Value", "fromLevel2"),
              "level3", $("level3Value", "fromLevel3")),
          $("myPartial", "{{topLevel}}"),
          "TOP fromLevel3 TOP");
    }

    /**
     * Test that the preserveParentContext setting doesn't affect normal partial behavior.
     *
     * <p>Ensures backward compatibility - normal partial invocation with explicit context still works.
     */
    @Test
    public void testRender_PreserveMode_NormalPartialWithContext() throws IOException {
      // Normal partial with explicit context should still work
      shouldCompileToWithPartials(
          "{{rootName}} {{> myPartial myContext}}",
          $("rootName", "rootValue", "myContext", $("name", "myContextValue")),
          $("myPartial", "{{name}}"),
          "rootValue myContextValue");
    }

    /**
     * Test rendering with {{> partial ../..}} and verify partial can access multiple parent levels.
     *
     * <p>Verifies navigation up two levels correctly accesses grandparent properties.
     */
    @Test
    public void testRender_PreserveMode_TwoLevelAccess() throws IOException {
      shouldCompileToWithPartials(
          "{{grandParent}} {{#with parent}}{{parent}} {{#with child}}{{child}} {{> myPartial ../..}}{{/with}}{{/with}}",
          $(
              "grandParent", "GRAND",
              "parent", $("parent", "PARENT"),
              "child", $("child", "CHILD")),
          $("myPartial", "{{grandParent}}"),
          "GRAND PARENT CHILD GRAND");
    }

    /**
     * Test case as requested: Create context with parent-child relationship directly.
     *
     * <p>Context structure:
     * <ul>
     *   <li>root = Context.newContext({"name": "Root"})</li>
     *   <li>child = Context.newContext(root, {"name": "Child"})</li>
     *   <li>template.apply(child) where template = "I am {{name}}, child of {{> outputParent ..}}"</li>
     * </ul>
     *
     * <p>When preserveParentContext is enabled, {{> outputParent ..}} should access root context.
     */
    @Test
    public void testRender_ChildContextAccessRootViaPartial_DirectContext() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("outputParent", "{{name}}");
      hbs.with(loader);

      // Create root context: {"name": "Root"}
      Context root = Context.newContext($("name", "Root"));

      // Create child context with root as parent: {"name": "Child"}
      Context child = Context.newContext(root, $("name", "Child"));

      // Template: "I am {{name}}, child of {{> outputParent ..}}"
      Template template = hbs.compileInline("I am {{name}}, child of {{> outputParent ..}}");
      String result = template.apply(child);

      assertEquals("I am Child, child of Root", result);
    }

    /**
     * Test case: Multi-level parent navigation with direct context creation.
     *
     * <p>Context structure:
     * <ul>
     *   <li>root = Context.newContext({"value": "ROOT"})</li>
     *   <li>level1 = Context.newContext(root, {"value": "L1"})</li>
     *   <li>level2 = Context.newContext(level1, {"value": "L2"})</li>
     *   <li>template.apply(level2) where template = "{{value}}-{{> myPartial ../..}}"</li>
     * </ul>
     *
     * <p>When preserveParentContext is enabled, {{> myPartial ../..}} should access root context.
     */
    @Test
    public void testRender_MultiLevelParent_DirectContext() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("myPartial", "{{value}}");
      hbs.with(loader);

      // Create multi-level context chain
      Context root = Context.newContext($("value", "ROOT"));
      Context level1 = Context.newContext(root, $("value", "L1"));
      Context level2 = Context.newContext(level1, $("value", "L2"));

      // Template: "{{value}}-{{> myPartial ../..}}"
      Template template = hbs.compileInline("{{value}}-{{> myPartial ../..}}");
      String result = template.apply(level2);

      assertEquals("L2-ROOT", result);
    }

    /**
     * Test case: Verify {{> partial this}} preserves parent chain with direct context.
     *
     * <p>Context structure:
     * <ul>
     *   <li>root = Context.newContext({"rootName": "Root"})</li>
     *   <li>child = Context.newContext(root, {"childName": "Child"})</li>
     *   <li>template.apply(child) where template = "{{childName}}-{{> myPartial this}}"</li>
     * </ul>
     *
     * <p>Partial template: "{{rootName}}" - should access root context's property.
     */
    @Test
    public void testRender_ThisContext_DirectContext() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("myPartial", "{{rootName}}");
      hbs.with(loader);

      // Create context chain
      Context root = Context.newContext($("rootName", "Root"));
      Context child = Context.newContext(root, $("childName", "Child"));

      // Template: "{{childName}}-{{> myPartial this}}"
      Template template = hbs.compileInline("{{childName}}-{{> myPartial this}}");
      String result = template.apply(child);

      assertEquals("Child-Root", result);
    }

    /**
     * Test rendering with nested property path in context parameter.
     *
     * <p>When preserveParentContext is enabled and using explicit context chain,
     * {{> partial ../../target/nested/value}} should:
     * <ol>
     *   <li>Navigate up two context levels (via ../..)</li>
     *   <li>Access 'target.nested.value' property path at that level</li>
     *   <li>Render the partial with that value as context</li>
     * </ol>
     *
     * <p>This tests the new nested property path resolution feature using PathCompiler.
     * Note: This test manually creates the context chain to ensure proper parent-child relationships.
     */
    @Test
    public void testRender_PreserveMode_NestedPropertyPath() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("value", "{{this}}");  // Partial renders current context value
      hbs.with(loader);

      // Create a proper context chain: root -> level1 -> level2
      // root.target.nested.value = "NESTED_VALUE"
      Map<String, Object> nested = $("value", "NESTED_VALUE");
      Map<String, Object> target = new HashMap<>();
      target.put("nested", nested);

      // Build context chain: create root first, then children
      // When template is applied to level2, parent() navigates up to level1, then root
      Context root = Context.newContext($("name", "root", "target", target));
      Context level1 = Context.newContext(root, $("name", "level1"));
      Context level2 = Context.newContext(level1, $("name", "level2"));

      // Template: "{{> value ../../target/nested/value}}"
      // - ../../ navigates up 2 levels to root
      // - target/nested/value resolves to root.target.nested.value
      // - partial "value" renders {{this}} which should render "NESTED_VALUE"
      Template template = hbs.compileInline("{{> value ../../target/nested/value}}");
      String result = template.apply(level2);

      assertEquals("NESTED_VALUE", result);
    }

    /**
     * Test rendering with single-level property path in context parameter.
     *
     * <p>Tests the basic case of accessing a single property after parent navigation.
     */
    @Test
    public void testRender_PreserveMode_SinglePropertyPath() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("value", "{{this}}");
      hbs.with(loader);

      // Create context chain: parent -> child
      // parent.targetValue = "TARGET"
      Context parent = Context.newContext($("name", "parent", "targetValue", "TARGET"));
      Context child = Context.newContext(parent, $("name", "child"));

      // Template: "{{> value ../targetValue}}"
      // - .. navigates up 1 level to parent
      // - targetValue resolves to parent.targetValue
      Template template = hbs.compileInline("{{> value ../targetValue}}");
      String result = template.apply(child);

      assertEquals("TARGET", result);
    }

    /**
     * Test that partial with nested property path handles missing properties gracefully.
     *
     * <p>When a property in the path doesn't exist, the behavior should be consistent
     * with Handlebars' standard missing property handling (renders empty string).
     */
    @Test
    public void testRender_PreserveMode_NestedPropertyPath_MissingProperty() throws IOException {
      Handlebars hbs = new Handlebars().preserveParentContext(true);
      MapTemplateLoader loader = new MapTemplateLoader();
      loader.define("value", "{{this}}");
      hbs.with(loader);

      // Create context chain: parent -> child
      // Neither parent nor child has the target property
      Context parent = Context.newContext($("name", "parent"));
      Context child = Context.newContext(parent, $("name", "child"));

      // Template: "{{> value ../missingProperty}}"
      // - .. navigates up 1 level to parent
      // - missingProperty resolves to null (not found)
      Template template = hbs.compileInline("{{> value ../missingProperty}}");
      String result = template.apply(child);

      // When property is missing, should render empty string
      assertEquals("", result);
    }
  }
}
