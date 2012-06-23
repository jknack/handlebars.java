package com.github.edgarespina.handlebars.springmvc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.github.edgarespina.handlebars.Template;

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
