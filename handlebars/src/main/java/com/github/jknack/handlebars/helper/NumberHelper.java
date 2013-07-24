/**
 * Copyright (c) 2012-2013 Edgar Espina
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
package com.github.jknack.handlebars.helper;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public enum NumberHelper implements Helper<Number> {

	/**
	 * You can use the isEven helper to return a value only if the 
	 * first argument is even. Otherwise return null.
	 * 
	 * <li class="{{isEven value "leftBox"}}">
	 * 
	 * If value is 2, the output will be "row-even".
	 * 
	 */
	isEven {
		@Override
		public CharSequence safeApply(final Number value, Options options) {
			return value.intValue() % 2 == 0 ? options.param(0).toString() : null;
		}

	},
	
	/**
	 * You can use the isOdd helper to return a value only if the 
	 * first argument is odd. Otherwise return null.
	 * 
	 * <li class="{{isOdd value "rightBox"}}">
	 * 
	 * If value is 3, the output will be "row-odd".
	 * 
	 */
	isOdd {
		@Override
		public CharSequence safeApply(final Number value, Options options) {
			return value.intValue() % 2 == 0 ? null : options.param(0).toString();
		}

	},
	
	/**
	 * You can use the stripes helper to return different value
	 * if the passed argument is odd or even.
	 * 
	 * <tr class="{{stripes value "row-even" "row-odd"}}">
	 * 
	 * If value is 2, the output will be "row-even".
	 * 
	 */
	stripes {
		@Override
		public CharSequence safeApply(final Number value, Options options) {
			return value.intValue() % 2 == 0 ? options.param(0).toString() : options.param(1).toString();
		}

	};

	@Override
	public CharSequence apply(final Number context, final Options options) throws IOException {
		if (options.isFalsy(context)) {
			Object param = options.param(0, null);
			return param == null ? null : param.toString();
		}
		return safeApply(context, options);
	}

	/**
	 * Apply the helper to the context.
	 * 
	 * @param context
	 *          The context object (param=0).
	 * @param options
	 *          The options object.
	 * @return A string result.
	 */
	protected abstract CharSequence safeApply(final Number context, final Options options);

	/**
	 * Register the helper in a handlebars instance.
	 * 
	 * @param handlebars
	 *          A handlebars object. Required.
	 */
	public void registerHelper(final Handlebars handlebars) {
		notNull(handlebars, "The handlebars is required.");
		handlebars.registerHelper(this.name(), this);
	}

	/**
	 * Register all the number helpers.
	 * 
	 * @param handlebars
	 *          The helper's owner. Required.
	 */
	public static void register(final Handlebars handlebars) {
		notNull(handlebars, "A handlebars object is required.");
		NumberHelper[] helpers = values();
		for (NumberHelper helper : helpers) {
			helper.registerHelper(handlebars);
		}
	}
}
