/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.jknack.handlebars.helper.AssignHelper;

public class AssignHelperTest extends AbstractTest {

  @Test
  public void assignResult() throws IOException {
    shouldCompileTo(
        "{{#assign \"benefitsTitle\"}} benefits.{{type}}.title {{/assign}}",
        $("type", "discounts"), "");
  }

  @Test
  public void assignContext() throws IOException {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("type", "discounts");
    Context context = Context.newContext(model);

    Handlebars handlebars = new Handlebars();
    assertEquals("", handlebars.compileInline(
        "{{#assign \"benefitsTitle\"}}" + "benefits.{{type}}.title"
            + " {{/assign}}").apply(context));

    assertEquals("benefits.discounts.title", context.data("benefitsTitle"));
  }

}
