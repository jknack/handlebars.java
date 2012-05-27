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
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.Handlebars;
}

@members {

  private Handlebars handlebars;

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

compile[Handlebars handlebars] returns[Template node]
@init {
  this.handlebars = handlebars;
}
@after {
  this.handlebars = null;
}
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

section returns[Section node]
  :
  (
     ss=sectionStart
        {node=$ss.node;}
     child=body
        {node.body(child);}
     sectionEnd
  )
  ;

sectionStart returns[Section node]
@init {
  boolean inverted = false;
}
  :
  (
     BEGIN_SECTION s=freeText END_MUSTACHE {inverted=false;}
  |  BEGIN_INVERTED_SECTION s=freeText END_MUSTACHE {inverted=true;}
  )
  {
    node = new Section($s.text, inverted);
  }
  ;

sectionEnd
  :
     END_SECTION freeText END_MUSTACHE
  ;

partial returns[Partial node] throws [java.io.IOException]
  :
  (
     BEGIN_PARTIAL uri=freeText END_MUSTACHE
  )
  {
     node = new Partial(handlebars, $uri.text);
  }
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
