package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.util.Arrays;

public class Issue574 extends v4Test {

  @Test
  public void eachShouldExecuteElseBranchOnFalsyValue() throws Exception {
    shouldCompileTo("{{#each list}}not empty{{else}}empty{{/each}}",
        $("hash", $("list", Arrays.asList())), "empty");

    shouldCompileTo("{{#each list}}not empty{{else}}empty{{/each}}",
        $("hash", $("list", null)), "empty");
  }
}
