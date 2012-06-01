package com.github.edgarespina.handlerbars;

import java.util.Map;

public interface Scope extends Map<String, Object> {

  Scope push(String name);

  Scope pop();
}
