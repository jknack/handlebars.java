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
parser grammar HandlebarsParser;

options {
  tokenVocab = HandlebarsLexer;
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
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.Handlebars;
}

@members {
  /**
   * The Handlebars compiler.
   */
  private Handlebars handlebars;

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
    s = section    {node.add(s);}
  | p = partial    {node.add(p);}
  | v = variable   {node.add(v);}
  | SET_DELIMITERS
  | TEXT           {node.add(new Text($TEXT.text));}
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
@init {
  boolean inverted = false;
}
  :
    (
      start = START_SECTION
        {inverted=false;}
      | start = START_INVERTED_SECTION
        {inverted=true;}
    )
      b = body
        {node = new Section($START_SECTION.text, inverted).body(b);}
      end = END_SECTION
      {
        if (!$start.text.equals($end.text)) {
          throw new HandlebarsException("line: " + $end.line + ":" +
            $end.pos +
            " expecting '" + $start.text +
            "' found: '" + $end.text + "'");
        }
      }
  ;

partial returns[Partial node] throws IOException
  :
    PARTIAL {node = new Partial(handlebars, $PARTIAL.text);}
  ;
