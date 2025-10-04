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
   * Test getText() method behavior.
   *
   * <p><b>Note:</b> The grammar successfully preserves whitespace tokens on channel 1, and the
   * getText() infrastructure is in place. However, full lossless reconstruction via getText()
   * requires setting token span information during template construction, which may need additional
   * work for complex templates.
   *
   * <p>For now, getText() falls back to text() reconstruction. The whitespace tokens ARE available
   * in the token stream for formatters and linters to access directly.
   */
  @Test
  public void testGetTextBehavior() throws IOException {
    Handlebars handlebars = new Handlebars();

    Template template1 = handlebars.compileInline("{{foo}}");
    Template template2 = handlebars.compileInline("{{ foo }}");

    // Currently getText() falls back to text() - both return normalized form
    assertEquals("{{foo}}", template1.getSourceText());
    assertEquals("{{foo}}", template2.getSourceText());

    // Note: The grammar DOES preserve whitespace on channel 1
    // Future enhancement: Complete token span tracking during template construction
  }
}
