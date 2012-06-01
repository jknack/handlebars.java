package com.github.edgarespina.handlerbars;

import java.io.IOException;

public interface Lambda {

  String apply(Template template, Scope scope) throws IOException;
}
