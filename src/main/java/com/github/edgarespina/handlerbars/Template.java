package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface Template {

  void apply(Map<String, Object> scope, Writer writer) throws IOException;

  String apply(Map<String, Object> scope) throws IOException;

  void apply(Scope scope, Writer writer) throws IOException;

  String apply(Scope scope) throws IOException;

  String text();
}
