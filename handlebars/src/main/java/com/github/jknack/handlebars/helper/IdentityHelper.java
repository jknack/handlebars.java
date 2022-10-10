package com.github.jknack.handlebars.helper;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

/**
 * Mainly used with {@link com.github.jknack.handlebars.HelperRegistry#registerHelperMissing}.
 * Use case example: Applying multiple handlebars implementations with different helpers.
 */
public class IdentityHelper implements Helper<Object> {
    @Override
    public Object apply(Object context, Options options) {
        return options.fn.text();
    }
}
