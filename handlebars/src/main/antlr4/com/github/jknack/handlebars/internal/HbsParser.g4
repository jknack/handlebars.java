parser grammar HbsParser;

options {
  tokenVocab=HbsLexer;
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
    for(Token token: tokens) {
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
  :
    spaces
  | newline
  | text
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
  ;

escape
  :
    ESC_VAR
  ;

text
  :
    TEXT
  ;

spaces
  : SPACE
  ;

newline
  : NL
  ;

block
  :
    startToken = START_BLOCK DECORATOR? sexpr blockParams? END
    thenBody=body
    elseBlock*
    END_BLOCK nameEnd=QID END
  ;

rawBlock
  :
    startToken = START_RAW sexpr END_RAW
    thenBody=body
    END_RAW_BLOCK nameEnd=QID END_RAW
  ;

blockParams
  :
    AS PIPE QID+ PIPE
  ;

sexpr
  :
    QID param* hash*
  ;

elseBlock
  :
    elseStmt
  | elseStmtChain
  ;

elseStmt
  :
    (inverseToken=UNLESS | START inverseToken=ELSE) END unlessBody=body
  ;

elseStmtChain
  :
    (inverseToken=UNLESS | START inverseToken=ELSE) sexpr blockParams? END unlessBody=body
  ;

unless
  :
    UNLESS sexpr blockParams? END
    body
    END_BLOCK nameEnd=QID END
  ;

tvar
  :
   START_T sexpr END_T
  ;

ampvar
  :
   START_AMP sexpr END
  ;

var
  :
   START DECORATOR? sexpr END
  ;

delimiters
  :
    START_DELIM
    WS_DELIM*
    startDelim+=DELIM+
      {setStart(join($startDelim));}
    WS_DELIM+
    endDelim+=DELIM+
    WS_DELIM*
    END_DELIM
    {setEnd(join($endDelim));}
  ;

partial
  :
    START_PARTIAL pexpr END
  ;

partialBlock
  :
    START_PARTIAL_BLOCK pexpr END
    thenBody=body
    END_BLOCK nameEnd=QID END
  ;

pexpr
  :
    LP sexpr RP QID? hash*                            #dynamicPath
  | path = (QID|PATH) QID? hash*                      #staticPath
  | path = (DOUBLE_STRING | SINGLE_STRING) QID? hash* #literalPath
  ;

param
  :
    DOUBLE_STRING #stringParam
  | SINGLE_STRING #charParam
  | INT           #intParam
  | BOOLEAN       #boolParam
  | QID           #refParam
  | LP sexpr RP   #subParamExpr
  ;

hash
  :
    QID EQ param
  ;

comment
  : COMMENT;
