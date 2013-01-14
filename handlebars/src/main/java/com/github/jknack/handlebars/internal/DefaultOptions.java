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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

/**
 * An implementation of {@link AbstractOptions}.
 * 
 * @author edgar.espina
 * @since 0.1.0
 */
class DefaultOptions extends AbstractOptions {

	/**
	 * An empty template implementation.
	 */
	private static Template EMPTY = new Template() {
		@Override
		public String text() {
			return "";
		}

		@Override
		public String apply(final Object context) throws IOException {
			return "";
		}

		@Override
		public String apply(final Context context) throws IOException {
			return "";
		}

		@Override
		public void apply(final Context context, final Writer writer)
				throws IOException {
		}

		@Override
		public void apply(final Object context, final Writer writer)
				throws IOException {
		}

		@Override
		public String toJavaScript() {
			return "";
		}
	};

	/**
	 * Creates a new {@link DefaultOptions}.
	 * 
	 * @param handlebars
	 *            The {@link Handlebars} object. Required.
	 * @param fn
	 *            The current template. Required.
	 * @param inverse
	 *            The current inverse template. Optional.
	 * @param context
	 *            The current context. Required.
	 * @param params
	 *            The parameters. Required.
	 * @param hash
	 *            The hash. Required.
	 */
	public DefaultOptions(final Handlebars handlebars, final Template fn,
			final Template inverse, final Context context,
			final Object[] params, final Map<String, Object> hash) {
		super(handlebars, context, fn, inverse == null ? EMPTY : inverse,
				params, hash);
	}

	@Override
	public CharSequence fn() throws IOException {
		return fn(context);
	}

	@Override
	public CharSequence fn(final Object context) throws IOException {
		return applyIfPossible(fn, context);
	}

	@Override
	public CharSequence inverse() throws IOException {
		return inverse(context);
	}

	@Override
	public CharSequence inverse(final Object context) throws IOException {
		return applyIfPossible(inverse, context);
	}

	@Override
	public <T> T get(final String name) {
		return get(name, null);
	}

	@Override
	public <T> T get(final String name, final T defaultValue) {
		@SuppressWarnings("unchecked")
		T value = (T) context.get(name);
		return value == null ? defaultValue : value;
	}

	@Override
	public Template partial(final String path) {
		return partials().get(path);
	}

	@Override
	public void partial(final String path, final Template partial) {
		partials().put(path, partial);
	}

	/**
	 * Apply the given template if the context object isn't null.
	 * 
	 * @param template
	 *            The template.
	 * @param context
	 *            The context object.
	 * @return The resulting text.
	 * @throws IOException
	 *             If a resource cannot be loaded.
	 */
	private CharSequence applyIfPossible(final Template template,
			final Object context) throws IOException {
		return apply(template, context);
	}

	@Override
	public CharSequence apply(final Template template) throws IOException {
		return apply(template, context);
	}

	@Override
	public CharSequence apply(final Template template, final Object context)
			throws IOException {
		return template.apply(wrap(context));
	}

	@Override
	public Context wrap(final Object model) {
		if (model == context) {
			return context;
		}
		if (model == context.model()) {
			return context;
		}
		if (model instanceof Context) {
			return (Context) model;
		}
		return Context.newContext(context, model);
	}

	/**
	 * Return the partials storage.
	 * 
	 * @return The partials storage.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Template> partials() {
		return (Map<String, Template>) data(Context.PARTIALS);
	}

	/**
	 * Cleanup resources.
	 */
	public void destroy() {
		hash.clear();
	}

	@Override
	public <T> T data(final String name) {
		return context.data(name);
	}

	@Override
	public void data(final String name, final Object value) {
		context.data(name, value);
	}

	@Override
	public Handlebars getHandlebars() {
		return super.handlebars;
	}

	@Override
	public Template getFn() {
		return super.fn;
	}

	@Override
	public Map<String, Object> getHash() {
		return super.hash;
	}
}
