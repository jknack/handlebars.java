/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package mustache.specs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  CommentsTest.class,
  DelimitersTest.class,
  InterpolationTest.class,
  InvertedTest.class,
  SectionsTest.class,
  PartialsTest.class,
  LambdasTest.class
})
public class SpecTests {}
