package com.github.edgarespina.handlebars;

import java.io.IOException;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Scopes;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.parser.Parser;

public class HParserTest {

  @Test
  public void variable() throws IOException {
    compile("Hello {{name}}!");
  }

  @Test
  public void tripleVariable() throws IOException {
    compile("Hello {{{name}}}!");
  }

  @Test
  public void ampersandVariable() throws IOException {
    compile("Hello {{&name}}!");
  }

  @Test
  public void partial() throws IOException {
    compile("{{>/path/template}}");
  }

  @Test
  public void setDelimiters() throws IOException {
    compile("{{=<% %>=}}Hello <%name%>!");
  }

  @Test
  public void section() throws IOException {
    compile("{{#person}}{{name}}{{/person}}");
  }

  @Test
  public void npe() throws IOException {
    //compile(" | {{#boolean}} {{! Important Whitespace }}\n {{/boolean}} | \n");
    compile("| This Is\n  {{#boolean}}\n|\n  {{/boolean}}\n| A Line\n");
  }

  @Test
  public void standaloneWithoutPreviousLine() throws IOException {
    compile("  {{! I'm Still Standalone }}\n!");
  }

  @Test
  public void invertedSection() throws IOException {
    compile("{{^person}}{{name}}{{!Ignore me}}{{number}}{{/person}}");
  }

  private void compile(final String input) throws IOException {
    System.out.println("INPUT:");
    System.out.println(input);
    Parser parser = Parboiled.createParser(Parser.class);
    ReportingParseRunner<Template> runner =
        new ReportingParseRunner<Template>(parser.template());
    ParsingResult<Template> result = runner.run(input);
    if (result.hasErrors()) {
      throw new HandlebarsException(ErrorUtils.printParseErrors(result));
    } else {
      System.out.println(result.resultValue);
      String string = result.resultValue.merge(Scopes.newScope());
      System.out.println(string);
    }
  }
}
