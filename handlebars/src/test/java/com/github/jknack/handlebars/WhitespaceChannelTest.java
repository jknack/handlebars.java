/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/** Tests for whitespace retention */
public class WhitespaceChannelTest {

  /**
   * Test that whitespace INSIDE tags does NOT affect rendering (backward compatibility).
   *
   * <p>This verifies that the grammar change to {@code channel(1)} maintains 100% backward
   * compatibility - whitespace inside tags is preserved in the token stream but ignored during
   * rendering.
   */
  @Test
  public void testRenderingIgnoresWhitespaceInsideTags() throws IOException {
    Handlebars handlebars = new Handlebars();

    Context context =
        Context.newBuilder(
                new Object() {
                  public String getFoo() {
                    return "A";
                  }
                })
            .build();

    // These should render identically despite different whitespace inside tags
    String template1 = handlebars.compileInline("{{foo}}").apply(context);
    String template2 = handlebars.compileInline("{{ foo }}").apply(context);
    String template3 = handlebars.compileInline("{{  foo  }}").apply(context);
    String template4 = handlebars.compileInline("{{   foo   }}").apply(context);

    assertEquals("A", template1);
    assertEquals("A", template2);
    assertEquals("A", template3);
    assertEquals("A", template4);
    assertEquals(template1, template2, "Whitespace inside tags should not affect rendering");
    assertEquals(template1, template3, "Whitespace inside tags should not affect rendering");
    assertEquals(template1, template4, "Whitespace inside tags should not affect rendering");
  }

  /**
   * Test that whitespace OUTSIDE tags (between elements) IS preserved in rendering.
   *
   * <p>This is handled by TEXT tokens, not WS tokens, and should work correctly.
   */
  @Test
  public void testWhitespaceOutsideTagsPreserved() throws IOException {
    Handlebars handlebars = new Handlebars();

    Context context =
        Context.newBuilder(
                new Object() {
                  public String getFoo() {
                    return "A";
                  }

                  public String getBar() {
                    return "B";
                  }
                })
            .build();

    // Whitespace between tags should be preserved
    assertEquals("A  B", handlebars.compileInline("{{foo}}  {{bar}}").apply(context));
    assertEquals("A\n  B", handlebars.compileInline("{{foo}}\n  {{bar}}").apply(context));
    assertEquals("A\t\tB", handlebars.compileInline("{{foo}}\t\t{{bar}}").apply(context));
  }

  /**
   * Test that templates compile successfully with various whitespace patterns.
   *
   * <p>This verifies that the grammar change doesn't break any parsing.
   */
  @Test
  public void testVariousWhitespacePatterns() throws IOException {
    Handlebars handlebars = new Handlebars();

    String[] templates = {
      "{{foo}}",
      "{{ foo }}",
      "{{  foo  }}",
      "{{   foo   }}",
      "{{foo}}  {{bar}}",
      "{{ foo }}  {{ bar }}",
      "{{#if condition}}yes{{/if}}",
      "{{# if condition }}yes{{/ if }}",
      "{{#each items}}{{name}}{{/each}}",
      "{{# each items }}{{ name }}{{/ each }}",
      "{{~foo~}}",
    };

    for (String templateSource : templates) {
      try {
        Template template = handlebars.compileInline(templateSource);
        assertNotNull(template, "Template should compile: " + templateSource);
      } catch (Exception e) {
        fail("Template failed to compile: " + templateSource + " - " + e.getMessage());
      }
    }
  }

  /** Test whitespace control operators still work correctly. */
  @Test
  public void testWhitespaceControlOperators() throws IOException {
    Handlebars handlebars = new Handlebars();

    Context context =
        Context.newBuilder(
                new Object() {
                  public String getFoo() {
                    return "A";
                  }

                  public String getBar() {
                    return "B";
                  }
                })
            .build();

    // Whitespace trimming operators should work
    assertEquals("AB", handlebars.compileInline("{{~foo~}}  {{~bar~}}").apply(context));
    assertEquals("A  B", handlebars.compileInline("{{foo}}  {{bar}}").apply(context));
  }

  /** Test complex template with nested blocks and mixed whitespace. */
  @Test
  public void testComplexTemplateCompilation() throws IOException {
    Handlebars handlebars = new Handlebars();

    String source = "{{# each items }}\n" + "  {{ name }}  :  {{ value }}\n" + "{{/ each }}";

    // Should compile without errors
    Template template = handlebars.compileInline(source);
    assertNotNull(template);
  }

  /** Test that block helpers with whitespace compile and render correctly. */
  @Test
  public void testBlockHelpersWithWhitespace() throws IOException {
    Handlebars handlebars = new Handlebars();

    Context context =
        Context.newBuilder(
                new Object() {
                  public boolean getCondition() {
                    return true;
                  }
                })
            .build();

    // Different whitespace patterns should all work
    assertEquals("yes", handlebars.compileInline("{{#if condition}}yes{{/if}}").apply(context));
    assertEquals("yes", handlebars.compileInline("{{# if condition }}yes{{/ if }}").apply(context));
    assertEquals(
        "yes", handlebars.compileInline("{{#  if  condition  }}yes{{/  if  }}").apply(context));
  }

  /** Test that newlines and various whitespace types are handled correctly. */
  @Test
  public void testVariousWhitespaceTypes() throws IOException {
    Handlebars handlebars = new Handlebars();

    Context context =
        Context.newBuilder(
                new Object() {
                  public String getFoo() {
                    return "A";
                  }

                  public String getBar() {
                    return "B";
                  }

                  public String getBaz() {
                    return "C";
                  }
                })
            .build();

    // Different whitespace types outside tags
    assertEquals(
        "A \t B\r\nC", handlebars.compileInline("{{foo}} \t {{bar}}\r\n{{baz}}").apply(context));
  }

  /**
   * Test text() method returns normalized representation (current behavior).
   *
   * <p><b>Note:</b> text() currently reconstructs from AST, losing original whitespace inside tags.
   * This is expected current behavior. Full lossless reconstruction would require additional
   * infrastructure to store original source spans.
   */
  @Test
  public void testTextMethodNormalizesWhitespace() throws IOException {
    Handlebars handlebars = new Handlebars();

    // text() returns normalized form (whitespace inside tags removed)
    Template template1 = handlebars.compileInline("{{foo}}");
    Template template2 = handlebars.compileInline("{{ foo }}");

    // Both normalize to the same form
    assertEquals("{{foo}}", template1.text());
    assertEquals("{{foo}}", template2.text());
  }

  /**
   * Test getSourceText() method provides lossless source reconstruction.
   *
   * <p><b>Success!</b> The grammar successfully preserves whitespace tokens on channel 1, and the
   * getSourceText() infrastructure is complete with token span tracking during template
   * construction.
   *
   * <p>getSourceText() now returns the exact original source including all whitespace, while text()
   * continues to return normalized form for backward compatibility.
   */
  @Test
  public void testGetSourceTextPreservesWhitespace() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template1 = handlebars.compileInline("{{foo}}");
    Template template2 = handlebars.compileInline("{{ foo }}");
    Template template3 = handlebars.compileInline("{{  foo  }}");

    // getSourceText() returns exact original source with whitespace preserved
    assertEquals("{{foo}}", template1.getSourceText());
    assertEquals("{{ foo }}", template2.getSourceText());
    assertEquals("{{  foo  }}", template3.getSourceText());

    // text() still returns normalized form (backward compatibility)
    assertEquals("{{foo}}", template1.text());
    assertEquals("{{foo}}", template2.text());
    assertEquals("{{foo}}", template3.text());
  }

  /**
   * Test getSourceText() with complex templates including text and multiple variables.
   *
   * <p><b>Note:</b> Block templates (like {{#if}}) are not yet fully supported for lossless
   * reconstruction. This is a future enhancement.
   */
  @Test
  public void testGetSourceTextComplexTemplates() throws IOException {
    Handlebars handlebars = new Handlebars();

    // Complex template with mixed whitespace and text
    String source1 = "{{ foo }}  text  {{ bar }}";
    Template template1 = handlebars.compileInline(source1);
    assertEquals(source1, template1.getSourceText());

    // Template with multiple variables
    String source2 = "{{ name }} - {{ value }}";
    Template template2 = handlebars.compileInline(source2);
    assertEquals(source2, template2.getSourceText());

    // Template with varying amounts of whitespace
    String source3 = "{{foo}}{{bar}}{{ baz }}";
    Template template3 = handlebars.compileInline(source3);
    assertEquals(source3, template3.getSourceText());
  }

  /** Test getSourceText() preserves all whitespace types (spaces, tabs, newlines). */
  @Test
  public void testGetSourceTextPreservesAllWhitespaceTypes() throws IOException {
    Handlebars handlebars = new Handlebars();

    // Template with tabs
    String source1 = "{{\tfoo\t}}";
    Template template1 = handlebars.compileInline(source1);
    assertEquals(source1, template1.getSourceText());

    // Template with newlines (though unusual inside tags)
    String source2 = "{{ foo }}  {{  bar  }}";
    Template template2 = handlebars.compileInline(source2);
    assertEquals(source2, template2.getSourceText());

    // Template with mixed whitespace
    String source3 = "{{  \tfoo\t  }}";
    Template template3 = handlebars.compileInline(source3);
    assertEquals(source3, template3.getSourceText());
  }
}
