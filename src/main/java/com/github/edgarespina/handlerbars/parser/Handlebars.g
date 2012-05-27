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
package com.github.edgarespina.handlerbars.parser;
}

@header {
package com.github.edgarespina.handlerbars.parser;

import java.util.Map;
import java.util.Collections;
}

@members {
  @Override
  public Object recoverFromMismatchedSet(final IntStream input,
      final RecognitionException e, final BitSet follow) throws RecognitionException {
    throw e;
  }

}

// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
  catch (RecognitionException e) {
    throw e;
  }
}

compile returns[Node node]
  :
  (
     child=body {node = child;} EOF
  )
  ;

body returns[Sequence node = new Sequence()]
  :
  (
     s = section           {node.add(s);}
  |  partial
  |  uv = unescapeVariable {node.add(uv);}
  |  v = variable          {node.add(v);}
  |  comment
  |  content=freeText      {node.add(new Text($content.text));}
  )*
  ;

variable returns[Variable node]
  :
    BEGIN_MUSTACHE var=freeText END_MUSTACHE
  {
    $node = new Variable($var.text.trim(), true);
  }
  ;

unescapeVariable returns[Variable node]
  :
  (
     BEGIN_UNESCAPE var = freeText END_MUSTACHE
  |  BEGIN_TRIPLE_MUSTACHE var = freeText END_TRIPLE_MUSTACHE
  )
  {
    $node = new Variable($var.text.trim(), false);
  }
  ;

section returns[Section node = new Section()]
  :
     (ss=sectionStart child=body {node.body(child);} sectionEnd)
     {
        node.name($ss.name);
        node.inverted($ss.invert);
     }
  ;

sectionStart returns[String name, boolean invert]
  :
  (
     BEGIN_SECTION s=freeText END_MUSTACHE {$invert=false;}
  |  BEGIN_INVERTED_SECTION s=freeText END_MUSTACHE {$invert=true;}
  )
  {
    $name = $s.text;
  }
  ;

sectionEnd
  :
     END_SECTION freeText END_MUSTACHE
  ;

partial
  :
     BEGIN_PARTIAL freeText END_MUSTACHE
  ;

comment
  :
     BEGIN_COMMENT ~(END_MUSTACHE)* END_MUSTACHE
  ;

freeText
  :
     ANY+
  ;

BEGIN_UNESCAPE
  :
     '{{&'
  ;

BEGIN_SECTION
  :
     '{{#'
  ;

BEGIN_INVERTED_SECTION
  :
     '{{^'
  ;

END_SECTION
  :
     '{{/'
  ;

BEGIN_TRIPLE_MUSTACHE
  :
     '{{{'
  ;

END_TRIPLE_MUSTACHE
  :
     '}}}'
  ;

BEGIN_PARTIAL
  :
     '{{>'
  ;

BEGIN_COMMENT
  :
     '{{!'
  ;

BEGIN_MUSTACHE
  :
     '{{'
  ;

END_MUSTACHE
  :
     '}}'
  ;

ANY:
  ~(
      BEGIN_UNESCAPE
   |  BEGIN_TRIPLE_MUSTACHE
   |  BEGIN_SECTION
   |  BEGIN_INVERTED_SECTION
   |  END_SECTION
   |  BEGIN_PARTIAL
   |  BEGIN_COMMENT
   |  BEGIN_MUSTACHE
   |  END_TRIPLE_MUSTACHE
   |  END_MUSTACHE
   )
  ;
