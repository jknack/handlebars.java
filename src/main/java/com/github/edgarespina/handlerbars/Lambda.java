package com.github.edgarespina.handlerbars;

import java.io.IOException;
import java.util.Map;

public interface Lambda {

  String apply(Template template, Map<String, Object> scope) throws IOException;
}
