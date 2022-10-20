/**
 * Copyright (c) 2012 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
import org.mockito.ArgumentCaptor;

/**
 * Unit test for {@link HandlebarsView}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsViewTest {

  @Test
  @SuppressWarnings("unchecked")
  public void renderMergedTemplateModel() throws Exception {
    Map<String, Object> model = mock(Map.class);

    PrintWriter writer = mock(PrintWriter.class);

    Template template = mock(Template.class);
    ArgumentCaptor<Context> captor = ArgumentCaptor.forClass(Context.class);

    HttpServletRequest request = mock(HttpServletRequest.class);

    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getWriter()).thenReturn(writer);

    HandlebarsView view = new HandlebarsView();
    view.setValueResolver(MapValueResolver.INSTANCE);
    view.setTemplate(template);
    view.renderMergedTemplateModel(model, request, response);

    verify(template).apply(captor.capture(), any(PrintWriter.class));
    assertNotNull(captor.getValue());
    verify(response).getWriter();
  }
}
