package com.github.jknack.handlebars.io;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jakarta.servlet.ServletContext;

import org.junit.Test;

public class ServletContextTemplateLoaderTest {
  @Test
  public void sourceAt() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("template");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void subFolder() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("mustache/specs/comments");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("/mustache/specs/comments");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test(expected = FileNotFoundException.class)
  public void fileNotFound() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources");

    TemplateSource source = new ServletContextTemplateLoader(servletContext).sourceAt("notExist");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void setBasePath() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/mustache/specs");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("comments");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/mustache/specs/");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", ".yml")
        .sourceAt("comments");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void nullSuffix() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", null)
        .sourceAt("noextension");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  @Test
  public void emotySuffix() throws IOException {
    ServletContext servletContext = mock(ServletContext.class);
    expectGetResource(servletContext, "src/test/resources/");

    TemplateSource source = new ServletContextTemplateLoader(servletContext, "/", "")
        .sourceAt("noextension");
    assertNotNull(source);

    verify(servletContext).getResource(anyString());
  }

  private void expectGetResource(final ServletContext servletContext, final String prefix)
      throws IOException {
    when(servletContext.getResource(anyString())).thenAnswer(invocation -> {
        File file = new File(prefix, invocation.getArgument(0));
        return file.exists() ? file.toURI().toURL() : null;
    });
  }
}
