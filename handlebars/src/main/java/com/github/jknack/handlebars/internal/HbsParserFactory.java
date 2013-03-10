/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.util.LinkedList;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.ParserFactory;
import com.github.jknack.handlebars.Template;

/**
 * The default {@link ParserFactory}.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class HbsParserFactory implements ParserFactory {

  /**
   * Creates a new {@link Parser}.
   *
   * @param handlebars The parser owner.
   * @param filename The file's name.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @param partials Any partial previously loaded.
   * @param stacktrace The current stacktrace.
   * @return A new {@link Parser}.
   */
  public Parser create(final Handlebars handlebars,
      final String filename,
      final String startDelimiter,
      final String endDelimiter,
      final Map<String, Partial> partials,
      final LinkedList<Stacktrace> stacktrace) {
    return new Parser() {

      @Override
      public Template parse(final String input) {
        String fname = handlebars.getTemplateLoader().resolve(filename);
        ANTLRInputStream stream = new ANTLRInputStream(input);
        stream.name = fname;
        final ANTLRErrorListener errorReporter = new HbsErrorReporter(stream.name);
        final HbsLexer lexer = new HbsLexer(stream, startDelimiter,
            endDelimiter) {

          @Override
          public void notifyListeners(final LexerNoViableAltException e) {
            String text = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()));
            String msg = "found: '" + getErrorDisplay(text) + "'";
            ANTLRErrorListener listener = getErrorListenerDispatch();
            listener
                .syntaxError(this, null, _tokenStartLine, _tokenStartCharPositionInLine, msg, e);
          }

          @Override
          public void recover(final LexerNoViableAltException e) {
            throw new IllegalArgumentException(e);
          }
        };
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorReporter);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        final HbsParser parser = new HbsParser(tokens) {
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
        parser.setErrorHandler(new HbsErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        ParseTree tree = parser.template();

        if (handlebars.prettyWhitespaces()) {
          // remove unnecessary spaces and new lines
          new ParseTreeWalker().walk(new SpaceTrimmer(), tree);
        }

        TemplateBuilder builder = new TemplateBuilder(handlebars, fname, partials, stacktrace) {
          @Override
          protected void reportError(final CommonToken offendingToken, final int line,
              final int column,
              final String message) {
            errorReporter.syntaxError(parser, offendingToken, line, column, message, null);
          }
        };
        Template template = builder.visit(tree);
        return template;
      }
    };
  }

  @Override
  public Parser create(final Handlebars handlebars,
      final String filename,
      final String startDelimiter,
      final String endDelimiter) {
    return create(handlebars, filename, startDelimiter, endDelimiter,
        null, null);
  }

}
