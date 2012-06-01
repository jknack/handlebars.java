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
grammar Handlebars;

@lexer::header {
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
}

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

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.github.edgarespina.handlerbars.ParsingException;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.Handlebars;
}

@members {
  /**
   * The Handlebars compiler.
   */
  private Handlebars handlebars;


  @Override
  public void displayRecognitionError(String[] tokenNames,
                    RecognitionException e) {
    // Don't recover
    throw new ParsingException(e);
  }
}

// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
  catch (RecognitionException e) {
    throw e;
  }
}

compile[Handlebars handlebars] returns[Template node] throws IOException
@init {
  this.handlebars = handlebars;
}
@after {
  this.handlebars = null;
}
  :
    b = body {node = b;} EOF
  ;

body returns[Sequence node = new Sequence()] throws IOException
  :
  (
    s = section  {node.add(s);}
  | p = partial  {node.add(p);}
  | v = variable {node.add(v);}
  | TEXT         {node.add(new Text($TEXT.text));}
  | c = '{'      {node.add(new Text($c.text));}
  )*
  ;

variable returns[Variable node]
@init {
  boolean escape = true;
}
  :
  (
   var = AMPERSAND_VAR {escape = false;}
    | var = TRIPLE_VAR {escape = false;}
    | var = VAR        {escape = true;}
  )
  {
    node = new Variable($var.text, escape);
  }
  ;

section returns[Section node] throws IOException
  :
    START_SECTION
      b = body {node = new Section($START_SECTION.text, false).body(b);}
    END_SECTION
  | START_INVERTED_SECTION
      b = body {node = new Section($START_INVERTED_SECTION.text, true).body(b);}
    END_SECTION
  ;

partial returns[Partial node] throws IOException
  :
    PARTIAL {node = new Partial(handlebars, $PARTIAL.text);}
  ;

PARTIAL
  :
    ('{{>' WS_LOOP ID WS_LOOP '}}')
    {setText($ID.text);}
  ;

START_SECTION
  :
    ('{{#' WS_LOOP ID WS_LOOP '}}')
    {setText($ID.text);}
  ;

START_INVERTED_SECTION
  :
    ('{{^' WS_LOOP ID WS_LOOP '}}')
    {setText($ID.text);}
  ;

END_SECTION
  :
    '{{/' WS_LOOP ID WS_LOOP '}}'
  ;

VAR
  :
    ('{{' WS_LOOP ID WS_LOOP '}}')
    {setText($ID.text);}
  ;

TRIPLE_VAR
  :
    ('{{{' WS_LOOP ID WS_LOOP '}}}')
    {setText($ID.text);}
  ;

AMPERSAND_VAR
  :
    ('{{&' WS_LOOP ID WS_LOOP '}}')
    {setText($ID.text);}
  ;

fragment
ID
  :
  ID_START
  ID_PART*
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
WS_LOOP
  :
  (' '|'\t')*
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

COMMENT
  :
  (
    // WS_LOOP
    '{{!' (options {greedy=false;}: .)* '}}'
    WS_LOOP ('\r'? '\n')?
  )
  {
    $channel = HIDDEN;
  }
  ;

TEXT
  :
    ~('{')+
  ;