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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.springmvc.HandlebarsView;

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
    template.apply(model, writer);

    HttpServletRequest request = createMock(HttpServletRequest.class);

    HttpServletResponse response = createMock(HttpServletResponse.class);
    expect(response.getWriter()).andReturn(writer);

    replay(template, model, request, response);

    HandlebarsView view = new HandlebarsView();
    view.setTemplate(template);
    view.renderMergedTemplateModel(model, request, response);

    verify(template, model, request, response);
  }
}
