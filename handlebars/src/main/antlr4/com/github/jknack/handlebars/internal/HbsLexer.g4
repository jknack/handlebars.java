lexer grammar HbsLexer;

@members {

  // Some default values
  String start = "{{";

  String end = "}}";

  public HbsLexer(CharStream input, String start, String end) {
    this(input);
    this.start = start;
    this.end = end;
  }

  private boolean isNotSpace(int ch) {
    return ch != ' ' && ch != '\t';
  }

  private boolean consumeUntil(final String token) {
    int offset = 0;
    while(!isEOF(offset) && !ahead(token, offset) &&
      isNotSpace(_input.LA(offset + 1))) {
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
    if (ahead(start + "!")) {
      int offset = 0;
      int level = -1;
      while (!isEOF(offset)) {
        if (ahead(end, offset)) {
          if (level == 0) {
            break;
          } else {
            level -= 1;
          }
        }
        if (ahead(start, offset)) {
          level += 1;
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

TEXT
  : {consumeUntil(start)}? .
  ;

COMMENT
  : {comment(start, end)}? .
  ;

START_AMP
 : {tryToken(start + "&")}? . -> pushMode(VAR)
 ;

START_T
 : {tryToken(start + "{")}? . -> pushMode(VAR)
 ;

UNLESS
 : {tryToken(start + "^")}? . -> pushMode(VAR)
 ;

START_BLOCK
 : {tryToken(start + "#")}? . -> pushMode(VAR)
 ;

START_DELIM
 : {tryToken(start + "=")}? . -> pushMode(SET_DELIMS)
 ;

START_PARTIAL
 : {tryToken(start + ">")}? . -> pushMode(PARTIAL)
 ;

END_BLOCK
 : {tryToken(start + "/")}? . -> pushMode(VAR)
 ;

START
 : {tryToken(start)}? . -> pushMode(VAR)
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
  : {tryToken("=" + end)}? . -> popMode
  ;

WS_DELIM
  : [ \t\r\n]
  ;

DELIM: .;

mode PARTIAL;

PATH
  :
  (
    '[' PATH_SEGMENT ']'
  | PATH_SEGMENT
  ) -> mode(VAR)
  ;

fragment
PATH_SEGMENT
  : [a-zA-Z0-9_$'/.:\-]+
  ;

WS_PATH
  : [ \t\r\n] -> skip
  ;

mode VAR;

END_T
 : {tryToken("}" + end)}? . -> popMode
 ;

END
 : {tryToken(end)}? . -> mode(DEFAULT_MODE)
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
  : '='
  ;

INT
  :
    [0-9]+
  ;

BOOLEAN
  :
    'true'
  | 'false'
  ;

ELSE
  :
    'else'
  ;

QID
 :
   '../' QID
 | '..'
 | '.'
 | '[' ID ']' ID_SEPARATOR QID
 | '[' ID ']'
 | ID ID_SEPARATOR QID
 | ID
 ;

fragment
ID_SEPARATOR
  : ('.'|'/'|'-');

fragment
ID
  :
  ID_START ID_SUFFIX*
 ;

fragment
ID_START
  :
   [a-zA-Z_$@]
  ;

fragment
ID_SUFFIX
  :
    ID_ESCAPE
  | ID_START
  | ID_PART
  ;

fragment
ID_ESCAPE
  :
   '.' '[' ~[\r\n]+? ']'
  ;

fragment
ID_PART
  :
   [0-9./]
  ;

WS
 : [ \t\r\n] -> skip
 ;
