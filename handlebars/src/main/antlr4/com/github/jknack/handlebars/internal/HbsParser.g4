parser grammar HbsParser;

options {
  tokenVocab = HbsLexer;
}

@members {
  public String[] tokenNames() {
    String[] tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }
      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
    return tokenNames;
  }

  void setStart(String start) {
  }

  void setEnd(String end) {
  }

  private String join(List<Token> tokens) {
    StringBuilder text = new StringBuilder();
    for (Token token : tokens) {
      text.append(token.getText());
    }
    return text.toString();
  }
}

template
  : body EOF
  ;

body
  : statement*
  ;

statement
  : text
  | block
  | var
  | tvar
  | ampvar
  | unless
  | partial
  | partialBlock
  | rawBlock
  | escape
  | comment
  | delimiters
  | inheritBlock
  | blockDef
  ;

escape
  : ESCAPE_SEQUENCE
  ;

text
  : TEXT
  ;

block
  : START WS_CONTROL? HASH DECORATOR? sexpr blockParams? WS_CONTROL? END
    thenBody=body
    elseBlock
    START WS_CONTROL? SLASH nameEnd=(QID | DOUBLE_STRING | SINGLE_STRING | NUMBER) WS_CONTROL? END
  ;

rawBlock
  : START_RAW_BLOCK WS_CONTROL? DECORATOR? sexpr WS_CONTROL? END_RAW_BLOCK
    thenBody=body
    START_RAW_BLOCK WS_CONTROL? SLASH nameEnd=QID WS_CONTROL? END_RAW_BLOCK
  ;

blockParams
  : AS PIPE QID+ PIPE
  ;

sexpr
  : pathId param* hash*
  ;

pathId
  : QID (DECORATOR+ QID)*
  | DOUBLE_STRING
  | SINGLE_STRING
  | NUMBER
  | BOOLEAN
  ;

elseBlock
  : elseStmtChain* elseStmt?
  ;

elseStmt
  : (START WS_CONTROL? inverseToken=CARET | START WS_CONTROL? inverseToken=ELSE) WS_CONTROL? END unlessBody=body
  ;

elseStmtChain
  : (START WS_CONTROL? inverseToken=CARET | START WS_CONTROL? inverseToken=ELSE) WS_CONTROL? sexpr blockParams? WS_CONTROL? END unlessBody=body
  ;

unless
  : START WS_CONTROL? CARET sexpr blockParams? WS_CONTROL? END
    body
    START WS_CONTROL? SLASH nameEnd=QID WS_CONTROL? END
  ;

tvar
  : START_RAW DECORATOR? sexpr WS_CONTROL? END_RAW
  ;

ampvar
  : START WS_CONTROL? AMP sexpr WS_CONTROL? END
  ;

var
  : START WS_CONTROL? DECORATOR? sexpr WS_CONTROL? END
  ;

delimiters
  : START_SET_DELIMITER
    startDelim+=DELIM_VAL
    endDelim+=DELIM_VAL
    END_SET_DELIMITER
    {setStart(join($startDelim)); setEnd(join($endDelim));}
  ;

partial
  : START WS_CONTROL? GT pexpr WS_CONTROL? END
  ;

partialBlock
  : START WS_CONTROL? HASH GT pexpr WS_CONTROL? END
    thenBody=body
    START WS_CONTROL? SLASH nameEnd=QID WS_CONTROL? END
  ;

pexpr
  : LP sexpr RP pctx? hash*                                      #dynamicPath
  | DECORATOR+ sexpr pctx? hash*                                 #dynamicPath
  | path=(QID | PATH) pctx? hash*                                #staticPath
  | path=(DOUBLE_STRING | SINGLE_STRING | NUMBER) pctx? hash*   #literalPath
  ;

pctx
  : QID
  | DOUBLE_STRING
  | SINGLE_STRING
  | NUMBER
  ;

param
  : DOUBLE_STRING #stringParam
  | SINGLE_STRING #charParam
  | NUMBER #numberParam
  | BOOLEAN #boolParam
  | QID #refParam
  | LP sexpr RP #subParamExpr
  ;

hash
  : QID EQ param
  ;

comment
  : START_BLOCK_COMMENT COMMENT_CONTENT_BLOCK? END_BLOCK_COMMENT
  | START_COMMENT COMMENT_CONTENT? END_COMMENT
  ;

inheritBlock
  : START_LT WS_CONTROL? pexpr WS_CONTROL? END
    inheritBody*
    START WS_CONTROL? SLASH QID WS_CONTROL? END
  ;

inheritBody
  : blockDef
  | TEXT
  ;

blockDef
  : START_DOLLAR WS_CONTROL? name=QID WS_CONTROL? END
    body
    START WS_CONTROL? SLASH nameEnd=QID WS_CONTROL? END
  ;
