/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Mustache Specs")
@SelectClasses({
  CommentsTest.class,
  DelimitersTest.class,
  InterpolationTest.class,
  InvertedTest.class,
  SectionsTest.class,
  PartialsTest.class,
  LambdasTest.class
})
public class SpecTests {}
