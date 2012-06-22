package com.github.edgarespina.handlebars.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.github.edgarespina.handlerbars.Template;

/**
 * A handlebars view implementation.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsView extends AbstractTemplateView {

  /**
   * The compiled template.
   */
  private Template template;

  /**
   * Merge model into the view. {@inheritDoc}
   */
  @Override
  protected void renderMergedTemplateModel(final Map<String, Object> model,
      final HttpServletRequest request, final HttpServletResponse response)
      throws Exception {
    template.apply(model, response.getWriter());
  }

  /**
   * Set the compiled template.
   *
   * @param template The compiled template. Required.
   */
  void setTemplate(final Template template) {
    Assert.notNull(template, "A handlebars template is required.");
    this.template = template;
  }
}
