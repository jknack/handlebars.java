package com.github.edgarespina.handlerbars;

import java.io.IOException;

public interface Helper<T> {

  CharSequence apply(T context, Options options) throws IOException;
}
