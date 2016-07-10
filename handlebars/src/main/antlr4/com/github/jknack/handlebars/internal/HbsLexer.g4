lexer grammar HbsLexer;

@members {

  // Some default values
  String start = "{{";

  String end = "}}";

  boolean whiteSpaceControl;

  public HbsLexer(CharStream input, String start, String end) {
    this(input);
    this.start = start;
    this.end = end;
  }

  private boolean isWhite(int ch) {
    return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
  }

  private boolean consumeUntil(final String token) {
    int offset = 0;
    while(!isEOF(offset) && !(ahead("\\" + token, offset) || ahead(token, offset)) &&
      !isWhite(_input.LA(offset + 1))) {
      offset+=1;
    }
    if (offset == 0) {
      return false;
    }
    // Since we found the text, increase the CharStream's index.
    _input.seek(_input.index() + offset - 1);
    getInterpreter().setCharPositionInLine(_tokenStartCharPositionInLine + offset - 1);
    return true;
  }

  private boolean comment(final String start, final String end) {
    String commentClose;
    if (ahead(start + "!--")) {
      commentClose = "--" + end;
    } else if (ahead(start + "!")) {
      commentClose = end;
    } else {
      return false;
    }

    int offset = 0;
    while (!isEOF(offset)) {
      if (ahead(commentClose, offset)) {
        break;
      }
      offset += 1;
    }
    offset += commentClose.length();
    // Since we found the text, increase the CharStream's index.
    _input.seek(_input.index() + offset - 1);
    getInterpreter().setCharPositionInLine(_tokenStartCharPositionInLine + offset - 1);
    return true;
  }

  private boolean varEscape(final String start, final String end) {
    if (ahead("\\" + start)) {
      int offset = start.length();
      while (!isEOF(offset)) {
        if (ahead(end, offset)) {
          break;
        }
        if (ahead(start, offset)) {
          return false;
        }
        offset += 1;
      }
      offset += end.length();
      // Since we found the text, increase the CharStream's index.
      _input.seek(_input.index() + offset - 1);
      getInterpreter().setCharPositionInLine(_tokenStartCharPositionInLine + offset - 1);
      return true;
    }
    return false;
  }

  private boolean startToken(final String delim) {
    boolean matches = tryToken(delim + "~");
    if (matches) {
      whiteSpaceControl = true;
    }
    return matches || tryToken(delim);
  }

  private boolean startToken(final String delim, String subtype) {
    boolean matches = tryToken(delim + subtype);
    if (!matches) {
      matches = tryToken(delim + "~" + subtype);
      if (matches) {
        whiteSpaceControl = true;
      }
    }
    return matches;
  }

  private boolean endToken(final String delim) {
    return endToken(delim, "");
  }

  private boolean endToken(final String delim, String subtype) {
    boolean matches = tryToken(subtype + delim);
    if (!matches) {
      matches = tryToken(subtype + "~" + delim);
      if (matches) {
        whiteSpaceControl = true;
      }
    }
    return matches;
  }

  private boolean tryToken(final String text) {
    if (ahead(text)) {
      // Since we found the text, increase the CharStream's index.
      _input.seek(_input.index() + text.length() - 1);
      getInterpreter().setCharPositionInLine(_tokenStartCharPositionInLine + text.length() - 1);
      return true;
    }
    return false;
  }

  private boolean isEOF(final int offset) {
    return _input.LA(offset + 1) == EOF;
  }

  private boolean ahead(final String text) {
    return ahead(text, 0);
  }

  private boolean ahead(final String text, int offset) {

    // See if `text` is ahead in the CharStream.
    for (int i = 0; i < text.length(); i++) {
      int ch = _input.LA(i + offset + 1);
      if (ch != text.charAt(i)) {
        // Nope, we didn't find `text`.
        return false;
      }
    }

    return true;
  }
}

ESC_VAR
  : {varEscape(start, end)}? .
  ;

TEXT
  : {consumeUntil(start)}? .
  ;

COMMENT
  : {comment(start, end)}? .
  ;

START_AMP
  :
    {startToken(start, "&")}? . -> pushMode(VAR)
  ;

END_RAW_BLOCK
 : {startToken(start, "{{/")}? . -> pushMode(VAR)
 ;

START_RAW
  :
    {startToken(start, "{{")}? . -> pushMode(VAR)
  ;

START_T
  :
    {startToken(start, "{")}? . -> pushMode(VAR)
  ;

UNLESS
 : {startToken(start, "^")}? . -> pushMode(VAR)
 ;

START_PARTIAL_BLOCK
 : {startToken(start, "#>")}? . -> pushMode(VAR)
 ;

START_BLOCK
 : {startToken(start, "#")}? . -> pushMode(VAR)
 ;

START_DELIM
 : {startToken(start, "=")}? . -> pushMode(SET_DELIMS)
 ;

START_PARTIAL
 : {startToken(start, ">")}? . -> pushMode(VAR)
 ;

END_BLOCK
 : {startToken(start, "/")}? . -> pushMode(VAR)
 ;

START
 : {startToken(start)}? . -> pushMode(VAR)
 ;

SPACE
 :
  [ \t]+
 ;

NL
 :
   '\r'? '\n'
 | '\r'
 ;

mode SET_DELIMS;

END_DELIM
  :
    {endToken("=" + end)}? . -> popMode
  ;

WS_DELIM
  :
    [ \t\r\n]
  ;

DELIM
  :
    .
  ;

mode VAR;

END_RAW
  :
    {endToken(end, "}}")}? . -> popMode
  ;

END_T
  :
    {endToken(end, "}")}? . -> popMode
  ;

END
  :
    {endToken(end)}? . -> mode(DEFAULT_MODE)
  ;

DECORATOR
  :
    '*'
  ;

AS
  :
    'as'
  ;

PIPE
  :
    '|'
  ;

DOUBLE_STRING
  :
    '"' ( '\\"' | ~[\n] )*? '"'
  ;

SINGLE_STRING
  :
    '\'' ( '\\\'' | ~[\n] )*? '\''
  ;

EQ
  :
    '='
  ;

INT
  :
    '-'? [0-9]+
  ;

BOOLEAN
  :
    'true'
  | 'false'
  ;

ELSE
  :
   '~'? 'else' '~'?
  ;

QID
 :
   '../' QID
 | '..'
 | './' QID
 | '.'
 | '[' ID ']' ID_SEPARATOR QID
 | '[' ID ']'
 | ID ID_SEPARATOR QID
 | ID
 ;

PATH
  :
    '[' PATH_SEGMENT ']'
  | PATH_SEGMENT
  ;

fragment
PATH_SEGMENT
  : [a-zA-Z0-9_$'/.:\-]+
  ;

fragment
ID_SEPARATOR
  : ('.'|'/'|'-');

fragment
ID
  :
  ID_START ID_SUFFIX*
  | ID_ESCAPE ID_SUFFIX*
 ;

fragment
ID_START
  :
   [a-zA-Z_$@:\u00C0-\u00FF]
  ;

fragment
ID_SUFFIX
  :
    '.' ID_ESCAPE
  | ID_START
  | ID_PART
  | '-'
  ;

fragment
ID_ESCAPE
  :
    '[' ~[\]]+? ']'
  ;

fragment
ID_PART
  :
   [0-9./]
  ;

LP
  :
    '('
  ;

RP
  :
    ')'
  ;

WS
 : [ \t\r\n] -> skip
 ;
