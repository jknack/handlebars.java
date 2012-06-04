package com.github.edgarespina.handlerbars.parser;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;

public class ExtendedHandlebarsLexer extends HandlebarsLexer {

  private int previous;

  public ExtendedHandlebarsLexer(final CharStream input) {
    super(input);
  }

  private int whitespaces() {
    int i = 1;
    int ch = input.LA(i);
    while(ch != EOF && Character.isWhitespace(ch)) {
      i++;
      ch = input.LA(i);
    }
    return i - 1;
  }

  private boolean ahead(final int offset, final String str) {
    for (int i = 0; i < str.length(); i++) {
      if (input.LA(i + offset + 1) != str.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void match(final String s) throws MismatchedTokenException {
    if (s.equals("{{")) {
      super.match(delimStart);
    } else if (s.equals("}}")) {
      super.match(delimEnd);
    } else {
      super.match(s);
    }
  }

  @Override
  public void mTokens() throws RecognitionException {
    final int alt;
    int offset = whitespaces();
    boolean eat = true;
    if (ahead(offset, delimStart + ">")) {
      alt = PARTIAL;
    } else if (ahead(offset, delimStart + "#")) {
      alt = START_SECTION;
    } else if (ahead(offset, delimStart + "^")) {
      alt = START_INVERTED_SECTION;
    } else if (ahead(offset, delimStart + "/")) {
      alt = END_SECTION;
    } else if (ahead(offset, delimStart + "{")) {
      alt = TRIPLE_VAR;
    } else if (ahead(offset, delimStart + "&")) {
      alt = AMPERSAND_VAR;
    } else if (ahead(offset, delimStart + "=")) {
      alt = SET_DELIMITERS;
    } else if (ahead(offset, delimStart + "!")) {
      alt = COMMENT;
    } else if (ahead(offset, delimStart)) {
      alt = VAR;
    } else {
      alt = TEXT;
      eat = previous != TEXT;
    }
    previous = alt;
    if (eat) {
      while(offset > 0) {
        input.consume();
        offset--;
      }
    }
    switch (alt) {
      case PARTIAL: {
        mPARTIAL();
        if (state.failed) {
          return;
        }
      }
        break;
      case START_SECTION: {
        mSTART_SECTION();
        if (state.failed) {
          return;
        }
      }
        break;
      case START_INVERTED_SECTION: {
        mSTART_INVERTED_SECTION();
        if (state.failed) {
          return;
        }
      }
        break;
      case END_SECTION: {
        mEND_SECTION();
        if (state.failed) {
          return;
        }
      }
        break;
      case TRIPLE_VAR: {
        mTRIPLE_VAR();
        if (state.failed) {
          return;
        }
      }
        break;
      case AMPERSAND_VAR: {
        mAMPERSAND_VAR();
        if (state.failed) {
          return;
        }
      }
        break;
      case SET_DELIMITERS: {
        mSET_DELIMITERS();
        if (state.failed) {
          return;
        }
      }
        break;
      case COMMENT: {
        mCOMMENT();
        if (state.failed) {
          return;
        }
      }
        break;
      case VAR: {
        mVAR();
        if (state.failed) {
          return;
        }

      }
        break;
      case TEXT: {
        mTEXT();
        if (state.failed) {
          return;
        }
      }
        break;
    }
  }

}
