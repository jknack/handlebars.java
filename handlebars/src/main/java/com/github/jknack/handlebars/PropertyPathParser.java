package com.github.jknack.handlebars;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for tokenizing path experessions
 */
class PropertyPathParser {

  private final Pattern pattern;

  public PropertyPathParser(String pathSeparators) {
    pattern = Pattern.compile("((\\[[^\\[\\]]+])|([^" + Pattern.quote(pathSeparators) + "]+))");
  }

  /**
   * Split the property name by separator (except within a [] escaped blocked) and create an array of it
   *
   * @param key The property's name.
   * @return A path representation of the property (array based).
   */
  String[] parsePath(final String key) {
    Matcher matcher = pattern.matcher(key);
    List<String> tags = new ArrayList<String>();
    while(matcher.find()) {
      tags.add(matcher.group(1));
    }
    return tags.toArray(new String[tags.size()]);
  }

}
