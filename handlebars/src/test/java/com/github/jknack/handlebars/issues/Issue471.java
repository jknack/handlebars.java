package com.github.jknack.handlebars.issues;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue471 extends v4Test {

  class Dummy {
    public String value;
    public Collection<Dummy> subDummies;

    public String getVal() {
      return value;
    }

    public Collection<Dummy> getSubDum() {
      return subDummies;
    }
  }

  @Test
  public void expansionsOfDifferentObjectsOfTheSameType() throws IOException {
    Dummy dummy1 = new Dummy(), dummy2 = new Dummy(), dummy3 = new Dummy(), dummy4 = new Dummy();
    dummy1.value = "d1";
    dummy2.value = "d2";
    dummy3.value = "d3";
    dummy4.value = "d4";

    dummy1.subDummies = Arrays.asList(dummy3);
    dummy2.subDummies = Arrays.asList(dummy4);

    Collection<Dummy> dummies = Arrays.asList(dummy1, dummy2);

    shouldCompileTo("{{#this}}{{val}}{{#subDum}}{{val}}{{/subDum}}{{/this}}",
        $("hash", dummies), "d1d3d2d4");

    shouldCompileTo("{{#each this}}{{val}}{{#each subDum}}{{val}}{{/each}}{{/each}}",
        $("hash", dummies), "d1d3d2d4");
  }

}
