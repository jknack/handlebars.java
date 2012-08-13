/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
 */
package com.github.jknack.handlebars.helper;

import java.util.Iterator;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;

/**
 * Publish pseudo variables for each element of an iterable.
 *
 * @author edgar.espina
 * @since 0.3.0
 */
public class EachPseudoVarHelper extends EachHelper {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new EachPseudoVarHelper();

  @Override
  protected Object next(final Context parent, final Iterator<Object> iterator,
      final int index) {
    Object element = super.next(parent, iterator, index);
    boolean first = index == 0;
    boolean even = index % 2 == 0;
    boolean last = !iterator.hasNext();
    return Context.newBuilder(parent, element)
        .combine("@index", index)
        .combine("@first", first ? "first" : "")
        .combine("@last", last ? "last" : "")
        .combine("@odd", even ? "" : "odd")
        .combine("@even", even ? "even" : "")
        .build();
  }
}
