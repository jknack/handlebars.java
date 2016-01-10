/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Decorator;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

/**
 * Var decorator.
 *
 * @author edgar
 * @since 4.0.0
 */
public class VarDecorator extends Variable {

  /** True, if this is top level decorator. */
  private boolean root;

  /** Underlying decorator. */
  private Decorator decorator;

  /**
   * Creates a new {@link VarDecorator}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param type The variable's type. Required.
   * @param params The variable's parameters. Required.
   * @param hash The variable's hash. Required.
   * @param root True, if this is top level decorator.
   */
  public VarDecorator(final Handlebars handlebars, final String name, final TagType type,
      final List<Param> params, final Map<String, Param> hash, final boolean root) {
    super(handlebars, name, type, params, hash);
    this.root = root;
  }

  @Override
  protected void postInit() {
    this.decorator = handlebars.decorator(name);
  }

  @Override
  public void before(final Context context, final Writer writer) throws IOException {

    Context ctx = root ? Context.copy(context, null) : context;

    Options options = new Options(handlebars, name, type, ctx, this, Template.EMPTY,
        decoParams(ctx), hash(ctx), Collections.<String> emptyList(), null);
    options.data(Context.PARAM_SIZE, this.params.size());

    decorator.apply(this, options);
  }

  @Override
  public void apply(final Context context, final Writer writer) throws IOException {
    // NOOP
  }

  @Override
  public boolean decorate() {
    return true;
  }

  @Override
  protected String suffix() {
    return "*";
  }

}
