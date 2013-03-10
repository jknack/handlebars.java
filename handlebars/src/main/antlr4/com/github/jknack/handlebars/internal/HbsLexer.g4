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

  private boolean tryToken(final String text) {
    if (ahead(text)) {
      // Since we found the text, increase the CharStream's index.
      _input.seek(_input.index() + text.length() - 1);
      getInterpreter().setCharPositionInLine(_tokenStartCharPositionInLine + text.length() - 1);
      return true;
    }
    return false;
  }

  private boolean ahead(final String text) {

    // See if `text` is ahead in the CharStream.
    for (int i = 0; i < text.length(); i++) {
      int ch = _input.LA(i + 1);
      if (ch != text.charAt(i)) {
        // Nope, we didn't find `text`.
        return false;
      }
    }

    return true;
  }
}

START_COMMENT
  : {tryToken(start + "!")}? . -> pushMode(COMMENTS)
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
 ;

TEXT
  : {!tryToken(start)}?.;

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
   '.' '[' .+? ']'
  ;

fragment
ID_PART
  :
   [0-9./]
  ;

WS
 : [ \t\r\n] -> skip
 ;

mode COMMENTS;

END_COMMENT
  : {tryToken(end)}? . ->popMode
  ;

COMMENT_CHAR: .;
