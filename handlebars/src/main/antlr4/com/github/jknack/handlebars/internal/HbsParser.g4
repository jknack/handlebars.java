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
  | comment
  | delimiters
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
    START_BLOCK nameStart=QID param* hash* END
    thenBody=body
    elseBlock?
    END_BLOCK nameEnd=QID END
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
   START_T QID param* hash* END_T
  ;

ampvar
  :
   START_AMP QID param* hash* END
  ;

var
  :
   START QID param* hash* END
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
  | INT           #intParam
  | BOOLEAN       #boolParam
  | QID           #refPram
  ;

hash
  :
    QID EQ hashValue
  ;

hashValue
  :
    DOUBLE_STRING #stringHash
  | SINGLE_STRING #charsHash
  | INT           #intHash
  | BOOLEAN       #boolHash
  | QID           #refHash
  ;

comment
  : COMMENT;
