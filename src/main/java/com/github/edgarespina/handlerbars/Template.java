package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;

public interface Template {

  void apply(Object context, Writer writer) throws IOException;

  String apply(Object context) throws IOException;

  String text();
}
