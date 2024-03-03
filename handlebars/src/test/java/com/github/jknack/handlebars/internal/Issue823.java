/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue823 extends AbstractTest {
  // correct token position is captured when text block has new line
  @Test
  public void correctTokenPositionForTextBlockWithNewline() throws IOException {
    final HbsLexer lexer =
        new HbsLexer(new ANTLRInputStream("Hi {{x.y.z.a}}a\n a phrase{{x.y.z}}"), "{{", "}}");
    lexer.removeErrorListeners();
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    HbsParser parser =
        new HbsParser(tokens) {
          @Override
          void setStart(final String start) {
            lexer.start = "{{";
          }

          @Override
          void setEnd(final String end) {
            lexer.end = "}}";
          }
        };
    parser.removeErrorListeners();
    parser.template();
    List<Token> tList = tokens.getTokens();
    assertEquals(tList.get(5).getCharPositionInLine(), 9);
    assertEquals(tList.get(5).getLine(), 2);
  }
}
