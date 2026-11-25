lexer grammar HbsLexer;

options {
    superClass = HbsLexerBase;
}

// --- DEFAULT MODE ---

// 0. ESCAPED TAGS: Matches sequences of backslashes immediately preceding a start tag
ESCAPE_SEQUENCE
    : '\\'+ { peekStart("") }? { handleEscapeSequence(); } -> type(TEXT)
    ;

// 1. NORMAL SLASHES: Matches backslashes that are NOT followed by a tag
NORMAL_SLASHES
    : '\\'+ -> type(TEXT)
    ;

// 2. TEXT: Safely consumes all text.
// It explicitly stops if it sees a backslash OR a start tag.
TEXT
    : ( { _input.LA(1) != '\\' && !peekStart("") }? . )+
    ;

// 3. RAW BLOCK: Matches {{{{
START_RAW_BLOCK
    : { peekStart("{{") }? . { advanceStart("{{"); } -> pushMode(VAR)
    ;

// 4. RAW STACHE: Matches {{{
START_RAW
    : { peekStart("{") }? . { advanceStart("{"); } -> pushMode(VAR)
    ;

// 5. BLOCK COMMENT: Matches {{!--
START_BLOCK_COMMENT
    : { peekStart("!--") }? . { advanceStart("!--"); } -> pushMode(BLOCK_COMMENT_MODE)
    ;

// 6. STANDARD COMMENT: Matches {{!
START_COMMENT
    : { peekStart("!") }? . { advanceStart("!"); } -> pushMode(COMMENT_MODE)
    ;

// 7. SET DELIMITER: Matches {{=
START_SET_DELIMITER
    : { peekStart("=") }? . { advanceStart("="); } -> pushMode(SET_DELIMITER_MODE)
    ;

// 8. INHERIT: Matches {{<
START_LT
    : { peekStart("<") }? . { advanceStart("<"); } -> pushMode(VAR)
    ;

// 9. BLOCK DEF: Matches {{$
START_DOLLAR
    : { peekStart("$") }? . { advanceStart("$"); } -> pushMode(VAR)
    ;

// 10. GENERIC START: Matches {{
// This MUST be the absolute last rule in this block!
START
    : { peekStart("") }? . { advanceStart(""); } -> pushMode(VAR)
    ;


// --- COMMENT MODES ---
mode BLOCK_COMMENT_MODE;

END_BLOCK_COMMENT
    : { peekEnd("--") }? . { advanceEnd("--"); } -> popMode
    ;

COMMENT_CONTENT_BLOCK
    : ( { !peekEnd("--") }? . )+
    ;

mode COMMENT_MODE;

END_COMMENT
    : { peekEnd("") }? . { advanceEnd(""); } -> popMode
    ;

COMMENT_CONTENT
    : ( { !peekEnd("") }? . )+
    ;


// --- SET DELIMITER MODE ---
mode SET_DELIMITER_MODE;

END_SET_DELIMITER
    : { peekEnd("=") }? . { advanceEnd("="); applyDelimiters(); } -> popMode
    ;

DELIM_VAL
    : ( { !peekEnd("=") }? ~[ \t\r\n=]+ )+ { captureDelimiter(getText()); }
    ;

DELIM_WS
    : [ \t\r\n]+ -> skip
    ;


// --- VAR MODE ---
mode VAR;

WS_CONTROL : '~' ;
HASH       : '#' ;
SLASH      : '/' ;
CARET      : '^' ;
GT         : '>' ;
AMP        : '&' ;
EQ         : '=' ;

// End blocks follow the exact same longest-match-first ordering
END_RAW_BLOCK
    : { currentTagType == 2 && peekEnd("}}") }? . { advanceEnd("}}"); } -> popMode
    ;

END_RAW
    : { currentTagType == 1 && peekEnd("}") }? . { advanceEnd("}"); } -> popMode
    ;

END
    : { currentTagType == 0 && peekEnd("") }? . { advanceEnd(""); } -> popMode
    ;

STAR : '*' ;
AS   : 'as' ;
PIPE : '|' ;

DOUBLE_STRING : '"' ( '\\"' | . )*? '"' ;
SINGLE_STRING : '\'' ( '\\\'' | . )*? '\'' ;

NUMBER  : '-'? [0-9]* '.'? [0-9]+ ;
BOOLEAN : 'true' | 'false' ;
ELSE    : 'else' ;

MODEL_KW : '@model' ;

QID
    : '../' QID | '..' | './' QID | '.'
    | '[' ID ']' ID_SEPARATOR QID | '[' ID ']' | ID ID_SEPARATOR QID | ID
    ;

PATH : '[' PATH_SEGMENT ']' | PATH_SEGMENT ;
fragment PATH_SEGMENT : [a-zA-Z0-9_$'.:\-] [a-zA-Z0-9_$'/.:\-?]* ;
fragment ID_SEPARATOR : ('.'|'/'|'-') ;

// Notice ID_START perfectly retains '$'
fragment ID : ID_START ID_SUFFIX* | ID_ESCAPE ID_SUFFIX* | [0-9]+ ID_START ID_SUFFIX*;
fragment ID_START : [a-zA-ZА-Яа-я_$@:\u0001-\u0008\u000b\u000c\u000e-\u001E\u00C0-\u00FF] ;
fragment ID_SUFFIX : '.' ID_ESCAPE | ID_START | ID_PART | '-' | '"' | '<' | '>';
fragment ID_ESCAPE : '[' ('\\]' | ~[\]])+? ']' ;
fragment ID_PART : [0-9./] ;

LP : '(' ;
RP : ')' ;

WS : [ \t\r\n]+ -> skip ;