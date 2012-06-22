package com.github.edgarespina.handlebars.springmvc;

import java.net.URI;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Template;

/**
 * A Handlebars {@link ViewResolver view resolver}.
 *
 * @author edgar.espina
 * @since 0.1
 */
public class HandlebarsViewResolver extends AbstractTemplateViewResolver {

  /**
   * The default content type.
   */
  public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";

  /**
   * The default view prefix.
   */
  public static final String DEFAULT_PREFIX = "/";

  /**
   * The default view suffix.
   */
  public static final String DEFAULT_SUFFIX = ".html";

  /**
   * The handlebars instance.
   */
  private Handlebars handlebars;

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   *
   * @param handlebars The handlebars object. Required.
   */
  public HandlebarsViewResolver(final Handlebars handlebars) {
    Assert.notNull(handlebars, "The handlebars object is required.");
    this.handlebars = handlebars;
    setViewClass(HandlebarsView.class);
    setContentType(DEFAULT_CONTENT_TYPE);
    setPrefix(DEFAULT_PREFIX);
    setSuffix(DEFAULT_SUFFIX);
  }

  /**
   * Creates a new {@link HandlebarsViewResolver}.
   */
  public HandlebarsViewResolver() {
    this(new Handlebars());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractUrlBasedView buildView(final String viewName)
      throws Exception {
    HandlebarsView view = (HandlebarsView) super.buildView(viewName);
    String url = view.getUrl();
    if (!url.startsWith("/")) {
      url = "/" + url;
    }
    URI uri = URI.create(url);
    logger.debug("Compiling: " + uri);
    Template template = handlebars.compile(uri);
    view.setTemplate(template);
    return view;
  }

  /**
   * The required view class.
   *
   * @return The required view class.
   */
  @Override
  protected Class<?> requiredViewClass() {
    return HandlebarsView.class;
  }

}
