/**
 * Copyright (c) 2012-2013 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Parser;
import com.github.jknack.handlebars.ParserFactory;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * The default {@link ParserFactory}.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class HbsParserFactory implements ParserFactory {


  /**
   * The logging system.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Creates a new {@link Parser}.
   *
   * @param handlebars The parser owner.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return A new {@link Parser}.
   */
  @Override
  public Parser create(final Handlebars handlebars,
      final String startDelimiter,
      final String endDelimiter) {
    return new Parser() {

      @Override
      public Template parse(final TemplateSource source) throws IOException {
        Reader reader = null;
        try {
          logger.debug("About to parse: {}", source);
          final ANTLRErrorListener errorReporter = new HbsErrorReporter(source.filename());

          reader = source.reader();
          // 1. Lexer
          final HbsLexer lexer = newLexer(newStream(source.filename(), reader),
              startDelimiter, endDelimiter);
          configure(lexer, errorReporter);

          // 2. Parser
          final HbsParser parser = newParser(lexer);
          configure(parser, errorReporter);

          logger.debug("Building AST");
          // 3. Parse
          ParseTree tree = parser.template();

          // remove unnecessary spaces and new lines?
          if (handlebars.prettyWhitespaces()) {
            logger.debug("Applying Mustache spec");
            new ParseTreeWalker().walk(new SpaceTrimmer(), tree);
          }

          /**
           * Build the AST.
           */
          TemplateBuilder builder = new TemplateBuilder(handlebars, source) {
            @Override
            protected void reportError(final CommonToken offendingToken, final int line,
                final int column,
                final String message) {
              errorReporter.syntaxError(parser, offendingToken, line, column, message, null);
            }
          };
          logger.debug("Creating templates");
          Template template = builder.visit(tree);
          return template;
        } finally {
          if (reader != null) {
            reader.close();
          }
        }
      }

    };
  }

  /**
   * Creates a new {@link ANTLRInputStream}.
   *
   * @param filename The file's name.
   * @param reader A reader.
   * @return A new {@link ANTLRInputStream}.
   * @throws IOException If the reader can't be open.
   */
  private ANTLRInputStream newStream(final String filename, final Reader reader)
      throws IOException {
    ANTLRInputStream stream = new ANTLRInputStream(reader);
    stream.name = filename;
    return stream;
  }

  /**
   * Creates a new {@link HbsLexer}.
   *
   * @param stream The input stream.
   * @param startDelimiter The start delimiter.
   * @param endDelimiter The end delimiter.
   * @return A new {@link HbsLexer}.
   */
  private HbsLexer newLexer(final ANTLRInputStream stream, final String startDelimiter,
      final String endDelimiter) {
    return new HbsLexer(stream, startDelimiter, endDelimiter) {

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
  }

  /**
   * Creates a new {@link HbsParser}.
   *
   * @param lexer The {@link HbsLexer}.
   * @return A new {@link HbsParser}.
   */
  private HbsParser newParser(final HbsLexer lexer) {
    return new HbsParser(new CommonTokenStream(lexer)) {
      @Override
      void setStart(final String start) {
        lexer.start = start;
      }

      @Override
      void setEnd(final String end) {
        lexer.end = end;
      }
    };
  }

  /**
   * Configure a {@link HbsParser}.
   *
   * @param parser The {@link HbsParser}.
   * @param errorReporter The error reporter.
   */
  @SuppressWarnings("rawtypes")
  private void configure(final HbsParser parser, final ANTLRErrorListener errorReporter) {
    configure((Recognizer) parser, errorReporter);

    parser.setErrorHandler(new HbsErrorStrategy());
    parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
  }

  /**
   * Configure a recognizer with an error reporter.
   *
   * @param recognizer A recongnizer.
   * @param errorReporter The error reporter.
   */
  private void configure(final Recognizer<?, ?> recognizer,
      final ANTLRErrorListener errorReporter) {
    recognizer.removeErrorListeners();
    recognizer.addErrorListener(errorReporter);
  }
}
