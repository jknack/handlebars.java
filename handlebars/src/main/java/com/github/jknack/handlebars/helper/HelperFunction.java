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
package com.github.jknack.handlebars.helper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Decorates a method that represents a helper function extracted via a "helper source"
 * with metadata that cannot be inferred from its signature, such as a custom helper name.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HelperFunction {

    /**
     * The name used to invoke the decorated helper function in a handlebars template.
     * @return Name or null/empty to use default method name.
     */
    String value();

}
