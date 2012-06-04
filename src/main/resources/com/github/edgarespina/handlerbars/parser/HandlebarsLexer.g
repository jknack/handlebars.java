/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
lexer grammar HandlebarsLexer;

@header {
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.edgarespina.handlerbars.parser;

import com.github.edgarespina.handlerbars.HandlebarsException;
}

@members {
  protected String delimStart = "{{";
  protected String delimEnd   = "}}";

  @Override
  public void emitErrorMessage(String msg) {
    // Don't recover
    throw new HandlebarsException(msg);
  }
}

// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
  catch (RecognitionException e) {
    throw e;
  }
}

PARTIAL
  :
    (DELIM_START '>' WS_LOOP ID WS_LOOP DELIM_END)
    {setText($ID.text);}
  ;

START_SECTION
  :
    (DELIM_START '#' WS_LOOP ID WS_LOOP DELIM_END)
    {setText($ID.text);}
  ;

START_INVERTED_SECTION
  :
    (DELIM_START '^' WS_LOOP ID WS_LOOP DELIM_END)
    {setText($ID.text);}
  ;

END_SECTION
  :
    (DELIM_START '/' WS_LOOP ID WS_LOOP DELIM_END)
    {setText($ID.text);}
  ;

TRIPLE_VAR
  :
    (DELIM_START '{' WS_LOOP ID WS_LOOP '}' DELIM_END)
    {setText($ID.text);}
  ;
 
AMPERSAND_VAR
  :
    (DELIM_START '&' WS_LOOP ID WS_LOOP DELIM_END)
    {setText($ID.text);}
  ;

SET_DELIMITERS
  :
    (
      DELIM_START '='
        start = NEW_DELIM_START
      WS+
        end = NEW_DELIM_END
      '=' DELIM_END
    )
    {
      delimStart = $start.text;
      delimEnd = $end.text;
    }
  ;

VAR
  :
    (
      DELIM_START
        WS_LOOP ID WS_LOOP
      DELIM_END
    )
    {setText($ID.text);}
  ;

COMMENT
  :
    (
      DELIM_START '!' (options{greedy=false;}: .)* DELIM_END
    )
    {$channel=HIDDEN;}
  ;

TEXT
  :
    .
  ;

fragment
NEW_DELIM_START
  :
  (~WS)+
  ;

fragment
NEW_DELIM_END
  :
  (~'=')+
;

fragment
DELIM_START
  :
    '{{'
  ;

fragment
DELIM_END
  :
    '}}'
  ;

fragment
WS
  :
    (' ' | '\t')
  ;

fragment
NL
  :
    ('\r'? '\n')
  ;

fragment
WS_LOOP
  :
    WS*
  ;

fragment
ID
  :
  '.'
  | ID_START ID_PART*
  ;

fragment
ID_START
  :
    '$' | '_' | '/' | LETTER
  ;

fragment
ID_PART
  :
    '.' | '-' | LETTER | DIGIT
  ;

fragment
DIGIT
  :
    '0'..'9'
  ;

fragment
LETTER
  :
    '\u0024'
    | '\u005f'
    | '\u0041'..'\u005a'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
    ;
