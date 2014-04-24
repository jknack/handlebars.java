parser grammar HbsParser;

options {
  tokenVocab=HbsLexer;
}

@members {
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
  | escape
  | comment
  | delimiters
  ;

escape
  :
    ESC_VAR
  ;

text
  : TEXT
  ;

spaces
  : SPACE
  ;

newline
  : NL
  ;

block
  :
    START_BLOCK sexpr END
    thenBody=body
    elseBlock?
    END_BLOCK nameEnd=QID END
  ;

sexpr
  :
    QID param* hash*
  ;

elseBlock
  :
    (inverseToken=UNLESS | START inverseToken=ELSE) END unlessBody=body
  ;

unless
  :
    UNLESS nameStart=QID END
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
   START sexpr END
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
    START_PARTIAL PATH QID? END
  ;

param
  :
    DOUBLE_STRING #stringParam
  | SINGLE_STRING #charParam
  | INT           #intParam
  | BOOLEAN       #boolParam
  | QID           #refPram
  | LP sexpr RP #subexpression
  ;

hash
  :
    QID EQ hashValue
  ;

hashValue
  :
    DOUBLE_STRING #stringHash
  | SINGLE_STRING #charHash
  | INT           #intHash
  | BOOLEAN       #boolHash
  | QID           #refHash
  ;

comment
  : COMMENT;
