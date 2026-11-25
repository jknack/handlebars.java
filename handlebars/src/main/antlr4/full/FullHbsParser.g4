parser grammar HbsParser;

options {
    tokenVocab = FullHbsLexer;
}

template
    : modelDirective? body EOF
    ;

body : statement* ;

modelDirective
    : ignored=TEXT? START WS_CONTROL? MODEL_KW type=QID WS_CONTROL? END
    ;

statement
    : text | block | variable | partial | partialBlock | rawBlock | comment | setDelimiterTag | inheritBlock | blockDef;

text : TEXT ;

block
    : START WS_CONTROL? (HASH | caret=CARET) STAR? sexpr blockParams? WS_CONTROL? END
      thenBody=body
      elseBlock
      START WS_CONTROL? SLASH nameEnd=pathId WS_CONTROL? END
    ;

inheritBlock
    : START_LT WS_CONTROL? pexpr WS_CONTROL? END
      inheritBody*
      START WS_CONTROL? SLASH nameEnd=pathId WS_CONTROL? END
    ;

blockDef
    : START_DOLLAR WS_CONTROL? name=pathId WS_CONTROL? END
      body
      START WS_CONTROL? SLASH nameEnd=pathId WS_CONTROL? END
    ;

inheritBody
    : blockDef | TEXT;

setDelimiterTag
    : START_SET_DELIMITER DELIM_VAL DELIM_VAL END_SET_DELIMITER
    ;

rawBlock
    : START_RAW_BLOCK WS_CONTROL? STAR? sexpr WS_CONTROL? END_RAW_BLOCK
      thenBody=body
      START_RAW_BLOCK WS_CONTROL? SLASH nameEnd=pathId WS_CONTROL? END_RAW_BLOCK
    ;

blockParams : AS PIPE QID+ PIPE ;

pathId
    : QID (STAR+ QID)*
    | DOUBLE_STRING
    | SINGLE_STRING
    | NUMBER
    | BOOLEAN
;

sexpr : pathId param* hash* ;

elseBlock : elseStmtChain* elseStmt? ;

elseStmt
    : (START WS_CONTROL? CARET | START WS_CONTROL? ELSE) WS_CONTROL? END unlessBody=body
    ;

elseStmtChain
    : (START WS_CONTROL? CARET | START WS_CONTROL? ELSE) WS_CONTROL? sexpr blockParams? WS_CONTROL? END unlessBody=body
    ;

variable
     : START_RAW STAR? sexpr END_RAW
     | START WS_CONTROL? STAR? AMP sexpr WS_CONTROL? END
     | START WS_CONTROL? STAR? sexpr WS_CONTROL? END;

partial : START WS_CONTROL? GT pexpr WS_CONTROL? END ;

partialBlock
    : START WS_CONTROL? HASH GT pexpr WS_CONTROL? END
      thenBody=body
      START WS_CONTROL? SLASH nameEnd=QID WS_CONTROL? END
    ;

pexpr
    : LP sexpr RP optionalContext=paramList hash* #dynamicPath
    | STAR+ sexpr optionalContext=paramList hash* #dynamicPath
    | path=(QID|PATH) optionalContext=paramList hash* #staticPath
    | path=(DOUBLE_STRING|SINGLE_STRING|NUMBER) optionalContext=paramList hash* #literalPath
    ;

paramList : param* ;

param
    : DOUBLE_STRING #stringParam
    | SINGLE_STRING #charParam
    | NUMBER #numberParam
    | BOOLEAN #boolParam
    | QID #refParam
    | LP sexpr RP #subParamExpr
    ;

hash : QID EQ param ;

comment
    : START_BLOCK_COMMENT COMMENT_CONTENT_BLOCK? END_BLOCK_COMMENT
    | START_COMMENT COMMENT_CONTENT? END_COMMENT
    ;
