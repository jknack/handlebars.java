package com.github.edgarespina.handlerbars.custom;

import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import specs.Spec;
import specs.SpecTest;

public class CustomObjectTest extends SpecTest {

  public CustomObjectTest(final Spec spec) {
    super(spec);
  }

  @Parameters
  public static Collection<Object[]> data() {
    return data(CustomObjectTest.class, "customObjects.yml");
  }

}
