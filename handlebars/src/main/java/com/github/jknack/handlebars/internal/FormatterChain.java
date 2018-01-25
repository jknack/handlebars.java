/**
 * Copyright (c) 2012-2015 Edgar Espina
 * <p>
 * This file is part of Handlebars.java.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.internal;

import com.github.jknack.handlebars.Formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Default implementation for formatter chain.
 *
 * @author edgar
 * @since 2.1.0
 */
public class FormatterChain implements Formatter.Chain {

    /** Pointer to next formatter Chain. */
    private Formatter.Chain nextFormatterChain;
    /** Pointer to formatter. */
    private Formatter formatter;

    /**
     * Creates a new {@link FormatterChain}.
     *
     * @param formatters List of available formatters.
     */
    public FormatterChain(final List<Formatter> formatters) {
        List<Formatter> formattersToAdd = new ArrayList<Formatter>(formatters);
        Iterator<Formatter> formatIter = formattersToAdd.iterator();
        if (formatIter.hasNext()) {
            this.formatter = formatIter.next();
            formatIter.remove();
            if (formatIter.hasNext()) {
                nextFormatterChain = new FormatterChain(formattersToAdd);
            } else {
                nextFormatterChain = Formatter.NOOP;
            }
        }
    }

    @Override
    public Object format(final Object value) {
        Object output;
        if (formatter != null) {
            output = formatter.format(value, this.nextFormatterChain);
            notNull(output, "Formatter " + formatter.getClass() + " returned a null result for "
                    + value);
        } else {
            output = value.toString();
        }
        return output;
    }

}
