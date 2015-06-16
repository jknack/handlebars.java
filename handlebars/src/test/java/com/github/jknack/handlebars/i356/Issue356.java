package com.github.jknack.handlebars.i356;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.AbstractTest;

public class Issue356 extends AbstractTest {

  @Test
  public void traversal() throws IOException {
    shouldCompileTo(
        "{{#data}}{{#each items}}\n" +
            "   1111:  {{this.trayType}} \n" +
            "   2222:  {{../this.trayType}}\n" +
            "   3333:  {{../../trayType}}\n" +
            "    {{#if imageOverridden ~}}\n" +
            "        image-overridden\n" +
            "    {{else ~}}\n" +
            "        {{#if ../../trayType ~}}\n" +
            "            size-{{../../trayType}}\n" +
            "        {{~/if}}\n" +
            "    {{~/if}}    \n" +
            "{{/each}}{{/data}}",
        $("trayType", "video", "data",
            $("items", new Object[]{$("id", "id-1", "name", "name-1") }, "config", $)), "\n" +
                "   1111:   \n" +
                "   2222:  \n" +
                "   3333:  video\n" +
                "    size-video    \n" +
                "");
  }

}
