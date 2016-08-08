package com.github.jknack.handlebars.issues;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.github.jknack.handlebars.v4Test;

public class Hbs531 extends v4Test {

  @Test
  public void dynamicPartialLookupWithADefaultParameterDoesn_tWork() throws IOException {
    shouldCompileTo("{{#each this}} {{> (lookup profession 'name') this.person}}<br /> {{/each}}",
        $("hash", Arrays.asList(
            $("firstName", "John", "lastName", "Foe", "profession", "developer"),
            $("firstName", "James", "lastName", "Doe", "profession", "analyst")),
            "partials",
            $("analyst", "Analyst - {{firstName}} {{lastName}}",
                "developer", "Developer - {{firstName}} {{lastName}}")),
        " Developer - John Foe<br />  Analyst - James Doe<br /> ");

    assertEquals("{{#each this}} {{>(lookup profession 'name') this.person}}<br /> {{/each}}",
        compile("{{#each this}} {{> (lookup profession 'name') this.person}}<br /> {{/each}}")
            .text());
  }

}
