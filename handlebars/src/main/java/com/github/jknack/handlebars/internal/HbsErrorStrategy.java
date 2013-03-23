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

import static org.apache.commons.lang3.Validate.notEmpty;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DelimitersContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;

/**
 * Fail in upon first error.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
class HbsErrorStrategy extends DefaultErrorStrategy {

  /**
   * Help to provide better error.
   *
   * @author edgar.espina
   * @since 0.10.0
   */
  private class ErrorStrategyVisitor extends HbsParserBaseVisitor<String> {

    /**
     * The start delimiter.
     */
    private String startDelimiter;

    /**
     * The end delimiter.
     */
    private String endDelimiter;

    /**
     * Creates a new {@link ErrorStrategyVisitor}.
     *
     * @param startDelimiter The start delimiter.
     * @param endDelimiter The end delimiter.
     */
    public ErrorStrategyVisitor(final String startDelimiter, final String endDelimiter) {
      this.startDelimiter = notEmpty(startDelimiter, "The startDelimiter can't be empty/null.");
      this.endDelimiter = notEmpty(endDelimiter, "The end delimiter can't be empty/null.");
    }

    @Override
    public String visitVar(final VarContext ctx) {
      if (ctx.stop == null) {
        return endDelimiter;
      }
      return null;
    }

    @Override
    public String visitTvar(final TvarContext ctx) {
      if (ctx.stop == null) {
        return "}" + endDelimiter;
      }
      return null;
    }

    @Override
    public String visitAmpvar(final AmpvarContext ctx) {
      if (ctx.stop == null) {
        return endDelimiter;
      }
      return null;
    }

    @Override
    public String visitBlock(final BlockContext ctx) {
      if (ctx.stop == null) {
        return startDelimiter + "/";
      }
      return null;
    }

    @Override
    public String visitUnless(final UnlessContext ctx) {
      if (ctx.stop == null) {
        return endDelimiter;
      }
      return null;
    }

    @Override
    public String visitPartial(final PartialContext ctx) {
      if (ctx.stop == null) {
        return endDelimiter;
      }
      return null;
    }

    @Override
    public String visitComment(final CommentContext ctx) {
      if (ctx.stop == null) {
        return endDelimiter;
      }
      return null;
    }

    @Override
    public String visitDelimiters(final DelimitersContext ctx) {
      if (ctx.stop == null) {
        return "=" + endDelimiter;
      }
      return null;
    }
  }

  @Override
  public void recover(final Parser recognizer, final RecognitionException e) {
    // always fail
    throw new HandlebarsException(e);
  }

  @Override
  public Token recoverInline(final Parser recognizer) {
    // always fail
    throw new InputMismatchException(recognizer);
  }

  @Override
  public void reportNoViableAlternative(final Parser recognizer, final NoViableAltException e) {
    HbsParser parser = (HbsParser) recognizer;
    TokenStream tokens = parser.getTokenStream();
    HbsLexer lexer = (HbsLexer) tokens.getTokenSource();
    String msg = new ErrorStrategyVisitor(lexer.start, lexer.end).visit(e.getCtx());
    if (msg != null) {
      recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    } else {
      super.reportNoViableAlternative(recognizer, e);
    }
  }

  @Override
  public void reportMissingToken(final Parser recognizer) {
    if (errorRecoveryMode) {
      return;
    }
    Token offendingToken = recognizer.getCurrentToken();
    IntervalSet expecting = getExpectedTokens(recognizer);
    String msg = expecting.toString(recognizer.getTokenNames());

    recognizer.notifyErrorListeners(offendingToken, msg, null);
  }

  @Override
  public void reportInputMismatch(final Parser recognizer, final InputMismatchException e) {
    String[] displayNames = displayNames(recognizer);
    String msg = e.getExpectedTokens().toString(displayNames);
    recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
  }

  /**
   * Translate the token's name to name that make more sense to the user.
   *
   * @param recognizer The lexer/parser.
   * @return User error messages.
   */
  private String[] displayNames(final Parser recognizer) {
    HbsParser parser = (HbsParser) recognizer;
    TokenStream tokens = parser.getTokenStream();
    HbsLexer lexer = (HbsLexer) tokens.getTokenSource();
    String[] tokenNames = recognizer.getTokenNames();
    String[] displayName = new String[tokenNames.length];
    for (int i = 0; i < displayName.length; i++) {
      String[] parts = StringUtils.split(tokenNames[i], "_");
      if (parts[0].equals("START")) {
        String suffix = "";
        if (parts.length > 1) {
          if (parts[1].equals("COMMENT")) {
            suffix = "!";
          } else if (parts[1].equals("AMP")) {
            suffix = "&";
          } else if (parts[1].equals("T")) {
            suffix = "{";
          } else if (parts[1].equals("BLOCK")) {
            suffix = "#";
          } else if (parts[1].equals("DELIM")) {
            suffix = "=";
          } else if (parts[1].equals("PARTIAL")) {
            suffix = ">";
          }
        }
        displayName[i] = lexer.start + suffix;
      } else if (parts[0].equals("END")) {
        String prefix = "";
        if (parts.length > 1) {
          if (parts[1].equals("BLOCK")) {
            displayName[i] = lexer.start + "/";
          } else if (parts[1].equals("DELIM")) {
            prefix = "=";
            displayName[i] = prefix + lexer.end;
          } else if (parts[1].equals("T")) {
            prefix = "}";
            displayName[i] = prefix + lexer.end;
          } else {
            displayName[i] = prefix + lexer.end;
          }
        } else {
          displayName[i] = prefix + lexer.end;
        }
      } else if (parts[0].equals("UNLESS")) {
        displayName[i] = "^";
      } else if (parts[0].equals("NL")) {
        displayName[i] = "\\n";
      } else if (parts[0].equals("WS")) {
        displayName[i] = "space";
      } else if (parts[0].equals("DOUBLE")) {
        displayName[i] = "string";
      } else if (parts[0].equals("SINGLE")) {
        displayName[i] = "string";
      } else if (parts[0].equals("QID")) {
        displayName[i] = "id";
      } else {
        displayName[i] = tokenNames[i];
      }
      displayName[i] = displayName[i].toLowerCase().replace("'", "");
    }
    return displayName;
  }

  @Override
  public void sync(final Parser recognizer) {
    // never sync
  }
}
