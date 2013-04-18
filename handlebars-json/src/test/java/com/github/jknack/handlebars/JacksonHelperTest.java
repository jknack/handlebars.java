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

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.junit.Test;

import com.github.jknack.handlebars.Blog.Views.Public;

/**
 * Unit test for {@link JacksonHelper}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class JacksonHelperTest {

  @Test
  public void toJSON() throws IOException {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Template template = handlebars.compileInline("{{@json this}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}",
        result);
  }

  @Test
  public void toJSONViewInclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    handlebars.registerHelper("@json", JacksonHelper.INSTANCE);

    Template template =
        handlebars
            .compileInline("{{@json this view=\"com.github.jknack.handlebars.Blog$Views$Public\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\",\"body\":\"...\",\"comments\":[]}",
        result);
  }

  @Test
  public void toJSONViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(Feature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json", new JacksonHelper(mapper));

    Template template =
        handlebars
            .compileInline("{{@json this view=\"com.github.jknack.handlebars.Blog$Views$Public\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\"}", result);
  }

  @Test
  public void toJSONAliasViewExclusive() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(Feature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json",
        new JacksonHelper(mapper).viewAlias("myView", Public.class));

    Template template =
        handlebars
            .compileInline("{{@json this view=\"myView\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\"}", result);
  }

  @Test(expected = HandlebarsException.class)
  public void jsonViewNotFound() throws IOException {
    Handlebars handlebars = new Handlebars();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(Feature.DEFAULT_VIEW_INCLUSION, false);

    handlebars.registerHelper("@json", new JacksonHelper(mapper));

    Template template =
        handlebars
            .compileInline("{{@json this view=\"missing.ViewClass\"}}");

    CharSequence result = template.apply(new Blog("First Post", "..."));

    assertEquals("{\"title\":\"First Post\"}", result);
  }
}
