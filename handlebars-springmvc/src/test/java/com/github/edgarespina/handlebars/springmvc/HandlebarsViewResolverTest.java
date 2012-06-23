package com.github.edgarespina.handlebars.springmvc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.Template;

/**
 * Unit test for {@link HandlebarsViewResolver}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class HandlebarsViewResolverTest {

  @Test
  public void configureNoDash() throws Exception {
    Template template = createMock(Template.class);

    Handlebars handlebars = createMock(Handlebars.class);
    expect(handlebars.compile(URI.create("/home.html"))).andReturn(template);

    HandlebarsView view = createMock(HandlebarsView.class);
    expect(view.getUrl()).andReturn("home.html");
    view.setTemplate(template);
    expectLastCall();

    replay(handlebars, view, template);

    HandlebarsViewResolver viewResolver =
        new HandlebarsViewResolver(handlebars);
    viewResolver.configure(view);

    verify(handlebars, view, template);
  }

  @Test
  public void configureWithDash() throws Exception {
    Template template = createMock(Template.class);

    Handlebars handlebars = createMock(Handlebars.class);
    expect(handlebars.compile(URI.create("/home.html"))).andReturn(template);

    HandlebarsView view = createMock(HandlebarsView.class);
    expect(view.getUrl()).andReturn("/home.html");
    view.setTemplate(template);
    expectLastCall();

    replay(handlebars, view, template);

    HandlebarsViewResolver viewResolver =
        new HandlebarsViewResolver(handlebars);
    assertEquals(view, viewResolver.configure(view));

    verify(handlebars, view, template);
  }

}
