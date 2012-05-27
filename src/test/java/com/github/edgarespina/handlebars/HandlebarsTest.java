package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.junit.Test;

import com.github.edgarespina.handlerbars.HandlebarsLexer;
import com.github.edgarespina.handlerbars.HandlebarsParser;
import com.github.edgarespina.handlerbars.Node;

public class HandlebarsTest {

  @Test
  public void section() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    Map<String, Object> person = new HashMap<String, Object>();
    person.put("firstName", "John");
    person.put("lastName", "Doe");
    scope.put("person", person);

    compile("Section", scope,
        "{{#person}}Hi {{firstName}} {{lastName}}!{{/person}}", "Hi John Doe!");
  }

  @Test
  public void emptyList() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("list", Collections.emptyList());

    compile("Empty List", scope, "{{#list}}False{{/list}}", "");
  }

  @Test
  public void simpleList() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("list", Arrays.asList("One", "Two", "Three"));

    compile("Simple List", scope, "{{#list}}*{{.}} {{/list}}",
        "*One *Two *Three ");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void peopleList() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    Map<String, Object> john = new HashMap<String, Object>();
    john.put("firstName", "John");
    john.put("lastName", "Doe");

    Map<String, Object> peter = new HashMap<String, Object>();
    peter.put("firstName", "Peter");
    peter.put("lastName", "Doe");

    scope.put("list", Arrays.asList(john, peter));

    compile("People List", scope,
        "{{#list}}* {{lastName}}, {{firstName}}{{/list}}",
        "* Doe, John* Doe, Peter");
  }

  @Test
  public void inverted() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("repo", Collections.emptyList());

    compile("Inverted Section", scope, "{{#repo}}<b>{{name}}</b>{{/repo}}" +
        "{{^repo}}No repos :({{/repo}}", "No repos :(");
  }

  @Test
  public void falseValues() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("none", false);

    compile("False Values", scope, "{{#none}}False{{/none}}", "");
  }

  @Test
  public void trueValues() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("none", true);

    compile("True Values", scope, "{{#none}}True{{/none}}", "True");
  }

  @Test
  public void variable() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "World");

    compile("Variable", scope, "Hello {{name}}!", "Hello World!");
  }

  @Test
  public void variableHtml() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "<b>World</b>");

    compile("Variable Html", scope, "Hello {{name}}!",
        "Hello &lt;b&gt;World&lt;/b&gt;!");
  }

  @Test
  public void unescapeVariable() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "<b>World</b>");

    compile("Unescape Html", scope, "Hello {{&name}}!",
        "Hello <b>World</b>!");
  }

  @Test
  public void comment() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();

    compile("Comment", scope, "<h1>Today{{! ignore me }}.</h1>",
        "<h1>Today.</h1>");
  }

  @Test
  public void unescapeTripleMustacheVariable() throws RecognitionException {
    Map<String, Object> scope = new HashMap<String, Object>();
    scope.put("name", "<b>World</b>");

    compile("Unescape Html", scope, "Hello {{{name}}}!",
        "Hello <b>World</b>!");
  }

  private void compile(final String testCase, final Map<String, Object> scope,
      final String input, final String output)
      throws RecognitionException {
    StopWatch stopWatch = new StopWatch(testCase);
    HandlebarsLexer lexer =
        new HandlebarsLexer(new ANTLRStringStream(input));
    TokenStream tokens = new CommonTokenStream(lexer);
    HandlebarsParser parser = new HandlebarsParser(tokens);
    Node node = parser.compile();
    StringBuilder buffer = new StringBuilder();
    node.toString(buffer, scope);
    stopWatch.done(buffer.toString());
    assertEquals(output, buffer.toString());
  }
}
