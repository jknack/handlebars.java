package com.github.edgarespina.handlerbars;

import java.io.IOException;

public interface Lambda<Out> {

  Out apply(Scope scope, Template template) throws IOException;
}
