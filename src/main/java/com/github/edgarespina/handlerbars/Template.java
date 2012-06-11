package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface Template {

  void merge(Map<String, Object> scope, Writer writer) throws IOException;

  String merge(Map<String, Object> scope) throws IOException;

  String text();
}
