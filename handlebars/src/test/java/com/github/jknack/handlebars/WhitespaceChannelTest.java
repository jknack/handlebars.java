/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class WhitespaceChannelTest {

  /**
   * Test that whitespace INSIDE tags does NOT affect rendering. The whitespace inside tags is
   * preserved in the token stream but ignored during rendering.
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

  /** Test that templates compile successfully with various whitespace patterns. */
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

  /** Test text() method returns normalized representation which remove whitespaces. */
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
}
