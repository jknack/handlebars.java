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

import org.junit.Test;

public class ParamHashTest extends AbstractTest {

  @Test
  public void truthParam() throws IOException {
    Hash helpers = $("helper", new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        assertEquals(true, options.param(0));
        return "ok";
      }
    });
    shouldCompileTo("{{helper . true}}", new Object(), helpers, "ok");
  }

  @Test
  public void falsyParam() throws IOException {
    Hash helpers = $("helper", new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        assertEquals(false, options.param(0));
        return "ok";
      }
    });
    shouldCompileTo("{{helper . false}}", new Object(), helpers, "ok");
  }

  @Test
  public void truthHash() throws IOException {
    Hash helpers = $("helper", new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        assertEquals(true, options.hash("b"));
        return "ok";
      }
    });
    shouldCompileTo("{{helper . b=true}}", new Object(), helpers, "ok");
  }

  @Test
  public void falsyHash() throws IOException {
    Hash helpers = $("helper", new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options)
          throws IOException {
        assertEquals(false, options.hash("b"));
        return "ok";
      }
    });
    shouldCompileTo("{{helper . b=false}}", new Object(), helpers, "ok");
  }

}
