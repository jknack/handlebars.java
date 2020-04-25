package com.github.jknack.handlebars.internal;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HbsParserTest {

  private boolean printTokens = true;

  @Test
  public void hello() {
    parse("Hello {{who}}\n!");
  }
  
  @Test
  public void subExpr() {
	  // below expression has unicode char with value: \u0001
	  parse("{{datawithCtrlChar}}");
  }

  @Test
  public void rawblock() {
    parse("{{{{raw}}}} {{test}} {{{{/raw}}}}");
  }

  @Test
  public void dynamicPartial() {
    parse("{{> (partial)}}");
  }

  @Test
  public void text() {
    parse("Hello world!");
  }

  @Test
  public void newlines() {
    parse("Alan's\rTest");
  }

  @Test
  public void var() {
    parse("{{variable 678}}");

    parse("{{array.[10]}}");

    parse("{{array.[1foo]}}");

    parse("{{array.['foo']}}");

    parse("{{array.['foo or bar}}]}}");

    parse("{{variable \"string\"}}");

    parse("{{variable \"true\"}}");

    parse("{{variable \"string\" 78}}");
  }

  @Test
  public void comments() {
    parse("12345{{! Comment Block! }}67890");
  }

  @Test
  public void partial() {
    parse("[ {{>include}} ]");
  }

  @Test
  public void ampvar() {
    parse("{{&variable 678}}");

    parse("{{&variable \"string\"}}");

    parse("{{&variable \"true\"}}");

    parse("{{&variable \"string\" 78}}");
  }

  @Test
  public void tvar() {
    parse("{{{variable 678}}}");

    parse("{{{variable \"string\"}}}");

    parse("{{{variable \"true\"}}}");

    parse("{{{variable \"string\" 78}}}");
  }

  @Test
  public void block() {
    parse("{{#block 678}}{{var}}{{/block}}");
    parse("{{#block 678}}then{{^}}else{{/block}}");
    parse("{{#block 678}}then{{^}}else{{/block}}");
  }

  @Test
  public void unless() {
    parse("{{^block}}{{var}}{{/block}}");
  }

  @Test
  public void hash() {
    parse("{{variable int=678}}");

    parse("{{variable string='string'}}");
  }

  @Test
  public void setDelim() {
    parse("{{=<% %>=}}<%hello%><%={{ }}=%>{{reset}}");
    parse("{{= | | =}}<|#lambda|-|/lambda|>");
    parse("{{=+-+ +-+=}}+-+test+-+");
  }

  @Test
  public void whiteSpaceHandledEqually() {
    String withSpace = "{{helper param1=\"1\" param2=2}}";
    ParseTree tree = parse(withSpace);

    String withNewlines = "{{helper\nparam1=\"1\"\nparam2=2}}";
    assertEquals("Newlines should be treated the same as spaces", tree.getText(), parse(withNewlines).getText());

    String withDosNewlines = "{{helper\r\nparam1=\"1\"\r\nparam2=2}}";
    assertEquals("DOS-style newlines should be treated the same as spaces", tree.getText(), parse(withDosNewlines).getText());

    String alignedWithTabs = "{{helper\n\tparam1=\"1\"\n\tparam2=2}}";
    assertEquals("Tab-aligned variables should be treated the same as spaces", tree.getText(), parse(alignedWithTabs).getText());
  }

  private ParseTree parse(final String input) {
    return parse(input, "{{", "}}");
  }

  private ParseTree parse(final String input, final String start, final String delim) {
    HbsErrorReporter errorReporter = new HbsErrorReporter("test.hbs");

    final HbsLexer lexer = new HbsLexer(new ANTLRInputStream(input), start, delim);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorReporter);

    CommonTokenStream tokens = new CommonTokenStream(lexer);

    HbsParser parser = new HbsParser(tokens) {
      @Override
      void setStart(final String start) {
        lexer.start = start;
      }

      @Override
      void setEnd(final String end) {
        lexer.end = end;
      }
    };
    parser.removeErrorListeners();
    parser.addErrorListener(errorReporter);
    ParseTree tree = parser.template();
    if (printTokens) {
      String[] tokenNames = parser.tokenNames();
      for (Token token : tokens.getTokens()) {
        int type = token.getType();
        String message = String.format("%s:%s:%s:%s", token.getText(), type == -1 ? ""
            : tokenNames[token.getType()], token.getLine(), token.getCharPositionInLine());
        System.out.println(message);
      }
    }
    System.out.println(tree.toStringTree(parser));
    return tree;
  }
}
