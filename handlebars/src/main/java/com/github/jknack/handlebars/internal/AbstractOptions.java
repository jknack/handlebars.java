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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.Handlebars.Utils;

/**
 * Options available for {@link Helper#apply(Object, AbstractOptions)}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public abstract class AbstractOptions implements Options {

  /**
   * The {@link Handlebars} object. Not null.
   */
  public final Handlebars handlebars;

  /**
   * The current context. Not null.
   */
  public final Context context;

  /**
   * The current template. Not null.
   */
  public final Template fn;

  /**
   * The current inverse template. Not null.
   */
  public final Template inverse;

  /**
   * The parameters. Not null.
   */
  public final Object[] params;

  /**
   * The hash options. Not null.
   */
  public final Map<String, Object> hash;

  /**
   * Creates a new Handlebars {@link AbstractOptions}.
   *
   * @param handlebars The handlebars instance. Required.
   * @param context The current context. Required.
   * @param fn The template function. Required.
   * @param inverse The inverse template function. Required.
   * @param params The parameters. Required.
   * @param hash The optional hash. Required.
   */
  public AbstractOptions(final Handlebars handlebars, final Context context,
      final Template fn, final Template inverse, final Object[] params,
      final Map<String, Object> hash) {
    this.handlebars = notNull(handlebars, "The handlebars is required.");
    this.context = notNull(context, "The context is required");
    this.fn = notNull(fn, "The template is required.");
    this.inverse = notNull(inverse, "The inverse template is required.");
    this.params = notNull(params, "The parameters are required.");
    this.hash = notNull(hash, "The hash are required.");
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#fn()
 */
  @Override
public abstract CharSequence fn() throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#fn(java.lang.Object)
 */
  @Override
public abstract CharSequence fn(Object context) throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#inverse()
 */
  @Override
public abstract CharSequence inverse() throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#inverse(java.lang.Object)
 */
  @Override
public abstract CharSequence inverse(Object context) throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#apply(com.github.jknack.handlebars.Template, java.lang.Object)
 */
  @Override
public abstract CharSequence apply(final Template template,
      final Object context) throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#apply(com.github.jknack.handlebars.Template)
 */
  @Override
public abstract CharSequence apply(final Template template)
      throws IOException;

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#param(int)
 */
  @Override
@SuppressWarnings("unchecked")
  public final <T> T param(final int index) {
    return (T) params[index];
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#param(int, T)
 */
  @Override
@SuppressWarnings("unchecked")
  public final <T> T param(final int index, final T defaultValue) {
    T value = null;
    if (index >= 0 && index < params.length) {
      value = (T) params[index];
    }
    return value == null ? defaultValue : value;
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#get(java.lang.String, T)
 */
  @Override
public abstract <T> T get(String name, T defaultValue);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#get(java.lang.String)
 */
  @Override
public abstract <T> T get(String name);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#partial(java.lang.String)
 */
  @Override
public abstract Template partial(String path);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#partial(java.lang.String, com.github.jknack.handlebars.Template)
 */
  @Override
public abstract void partial(String path, Template partial);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#hash(java.lang.String)
 */
  @Override
public final <T> T hash(final String name) {
    return hash(name, null);
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#hash(java.lang.String, java.lang.Object)
 */
  @Override
@SuppressWarnings("unchecked")
  public final <T> T hash(final String name, final Object defaultValue) {
    Object value = hash.get(name);
    return (T) (value == null ? defaultValue : value);
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#isFalsy(java.lang.Object)
 */
  @Override
public final boolean isFalsy(final Object value) {
    return Handlebars.Utils.isEmpty(value);
  }

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#wrap(java.lang.Object)
 */
  @Override
public abstract Context wrap(Object model);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#data(java.lang.String)
 */
  @Override
public abstract <T> T data(final String name);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#data(java.lang.String, java.lang.Object)
 */
  @Override
public abstract void data(final String name, final Object value);

  /* (non-Javadoc)
 * @see com.github.jknack.handlebars.internal.IOptions#propertySet(java.lang.Object)
 */
  @Override
public Set<Entry<String, Object>> propertySet(final Object context) {
    return this.context.propertySet(context instanceof Context
        ? ((Context) context).model()
        : context);
  }
  
  @Override
	public  Object[] getParams() {
		return params;
	}



}
