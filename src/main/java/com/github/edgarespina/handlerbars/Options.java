package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;


public interface Options {

  String fn() throws IOException;

  String fn(Object context) throws IOException;

  String inverse() throws IOException;

  String inverse(Object context) throws IOException;

  <T> T param(int index);

  int paramSize();

  Template template();

  <T> T hash(String name);

  Set<Entry<String, Object>> hash();

  boolean empty(Object value);
}
