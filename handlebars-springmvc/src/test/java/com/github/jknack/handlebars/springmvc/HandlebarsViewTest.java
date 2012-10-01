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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.junit.Test;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;

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
    Map<String, Object> model = createMock(Map.class);

    PrintWriter writer = createMock(PrintWriter.class);

    Template template = createMock(Template.class);
    Capture<Context> context = new Capture<Context>();
    template.apply(capture(context), isA(PrintWriter.class));

    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpServletResponse response = createMock(HttpServletResponse.class);
    expect(response.getWriter()).andReturn(writer);

    replay(template, model, request, response);

    HandlebarsView view = new HandlebarsView();
    view.setValueResolver(MapValueResolver.INSTANCE);
    view.setTemplate(template);
    view.renderMergedTemplateModel(model, request, response);

    assertNotNull(context.getValue());

    verify(template, model, request, response);
  }
}
