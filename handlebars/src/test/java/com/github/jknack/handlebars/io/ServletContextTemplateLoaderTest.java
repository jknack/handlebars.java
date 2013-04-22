package com.github.jknack.handlebars.io;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.junit.Test;

public class ServletContextTemplateLoaderTest {

  @Test
  public void sourceAt() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("template");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void subFolder() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("mustache/specs/comments");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("/mustache/specs/comments");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test(expected = FileNotFoundException.class)
  public void fileNotFound() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("notExist");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void setBasePath() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/mustache/specs");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("comments");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/mustache/specs/");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("comments");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void nullSuffix() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", null)
        .sourceAt("noextension");
    assertNotNull(source);

    verify(servletContext);
  }

  @Test
  public void emotySuffix() throws IOException {
    ServletContext servletContext = createMock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/");

    replay(servletContext);

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", "")
        .sourceAt("noextension");
    assertNotNull(source);

    verify(servletContext);
  }

  private void expectGetResource(final ServletContext servletContext, final String prefix)
      throws IOException {
    final Capture<String> path = new Capture<String>();
    expect(servletContext.getResource(capture(path))).andAnswer(
        new IAnswer<URL>() {
          @Override
          public URL answer() throws Throwable {
            File file = new File(prefix, path.getValue());
            return file.exists() ? file.toURI().toURL() : null;
          }
        });
  }
}
