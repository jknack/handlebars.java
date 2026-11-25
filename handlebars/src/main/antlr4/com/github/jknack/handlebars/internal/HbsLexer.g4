lexer grammar HbsLexer;

options {
  superClass = HbsLexerBase;
}

@members {
  public HbsLexer(CharStream input, String start, String end) {
    this(input);
    setDelimiters(start, end);
  }
}

ESCAPE_SEQUENCE
  : '\\'+ {peekStart("")}? {handleEscapeSequence();}
  ;

NORMAL_SLASHES
  : '\\'+ -> type(TEXT)
  ;

TEXT
  : ({_input.LA(1) != '\\' && !peekStart("")}? .)+
  ;

START_RAW_BLOCK
  : {peekStart("{{")}? . {advanceStart("{{");} -> pushMode(VAR)
  ;

START_RAW
  : {peekStart("{")}? . {advanceStart("{");} -> pushMode(VAR)
  ;

START_BLOCK_COMMENT
  : {peekStart("!--")}? . {advanceStart("!--");} -> pushMode(BLOCK_COMMENT_MODE)
  ;

START_COMMENT
  : {peekStart("!")}? . {advanceStart("!");} -> pushMode(COMMENT_MODE)
  ;

START_SET_DELIMITER
  : {peekStart("=")}? . {advanceStart("=");} -> pushMode(SET_DELIMITER_MODE)
  ;

START_LT
  : {peekStart("<")}? . {advanceStart("<");} -> pushMode(VAR)
  ;

START_DOLLAR
  : {peekStart("$")}? . {advanceStart("$");} -> pushMode(VAR)
  ;

START
  : {peekStart("")}? . {advanceStart("");} -> pushMode(VAR)
  ;

mode BLOCK_COMMENT_MODE;

END_BLOCK_COMMENT
  : {peekEnd("--")}? . {advanceEnd("--");} -> popMode
  ;

COMMENT_CONTENT_BLOCK
  : ({!peekEnd("--")}? .)+
  ;

mode COMMENT_MODE;

END_COMMENT
  : {peekEnd("")}? . {advanceEnd("");} -> popMode
  ;

COMMENT_CONTENT
  : ({!peekEnd("")}? .)+
  ;

mode SET_DELIMITER_MODE;

END_SET_DELIMITER
  : {peekEnd("=")}? . {advanceEnd("="); applyDelimiters();} -> popMode
  ;

DELIM_VAL
  : ({!peekEnd("=")}? ~[ \t\r\n=])+ {captureDelimiter(getText());}
  ;

DELIM_WS
  : [ \t\r\n]+ -> skip
  ;

mode VAR;

WS_CONTROL
  : '~' {whiteSpaceControl = true;}
  ;

END_RAW_BLOCK
  : {currentTagType == 2 && peekEnd("}}")}? . {advanceEnd("}}");} -> popMode
  ;

END_RAW
  : {currentTagType == 1 && peekEnd("}")}? . {advanceEnd("}");} -> popMode
  ;

END
  : {currentTagType == 0 && peekEnd("")}? . {advanceEnd("");} -> popMode
  ;

HASH : '#' ;
SLASH : '/' ;
CARET : '^' ;
GT : '>' ;
AMP : '&' ;
EQ : '=' ;

DECORATOR : '*' ;
AS : 'as' ;
PIPE : '|' ;

DOUBLE_STRING : '"' ('\\"' | .)*? '"' ;
SINGLE_STRING : '\'' ('\\\'' | .)*? '\'' ;

NUMBER : '-'? [0-9]* '.'? [0-9]+ ;
BOOLEAN : 'true' | 'false' ;
ELSE : 'else' ;
MODEL_KW : '@model' ;

QID
  : '../' QID
  | '..'
  | './' QID
  | '.'
  | '[' ID ']' ID_SEPARATOR QID
  | '[' ID ']'
  | ID ID_SEPARATOR QID
  | ID
  ;

PATH : '[' PATH_SEGMENT ']' | PATH_SEGMENT ;
fragment PATH_SEGMENT : [a-zA-Z0-9_$'.:\-] [a-zA-Z0-9_$'/.:\-?]* ;
fragment ID_SEPARATOR : ('.' | '/' | '-') ;
fragment ID
  : ID_START ID_SUFFIX*
  | ID_ESCAPE ID_SUFFIX*
  | [0-9]+ ID_START ID_SUFFIX*
  ;
fragment ID_START
  : [a-zA-ZА-Яа-я_$@:\u0001-\u0008\u000b\u000c\u000e-\u001E\u00C0-\u00FF]
  ;
fragment ID_SUFFIX
  : '.' ID_ESCAPE
  | ID_START
  | ID_PART
  | '-'
  | '"'
  | '<'
  | {!peekEnd("")}? '>'
  ;
fragment ID_ESCAPE : '[' ('\\]' | ~[\]])+? ']' ;
fragment ID_PART : [0-9./] ;

LP : '(' ;
RP : ')' ;
WS : [ \t\r\n]+ -> skip ;
