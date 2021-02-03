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
     */
    String value();

}
