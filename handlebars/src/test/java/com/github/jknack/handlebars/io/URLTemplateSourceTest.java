package com.github.jknack.handlebars.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class URLTemplateSourceTest {
  @Test
  public void content() throws Exception {
    URLTemplateSource templateSource = new URLTemplateSource("template.hbs", getClass().getResource("/template.hbs"));

    assertEquals("template.hbs", templateSource.content(StandardCharsets.UTF_8));
  }

  @Test
  public void openConnectionThrowIOException() throws IOException {
    URL url = createThrowingMockUrl(new IOException());

    String filename = "home.hbs";

    assertEquals(-1, new URLTemplateSource(filename, url).lastModified());
  }

  @Test
  public void closeOpenedConnection() throws IOException {
    InputStream is = mock(InputStream.class);
    is.close();

    URLConnection uc = mock(URLConnection.class);
    when(uc.getLastModified()).thenReturn(123L);
    when(uc.getInputStream()).thenReturn(is);

    URL url = createMockUrl(uc);

    String filename = "home.hbs";

    assertEquals(123L, new URLTemplateSource(filename, url).lastModified());

    verify(uc).getLastModified();
    verify(uc).getInputStream();
  }

  @Test
  public void lastModifiedFromJar() throws IOException {
    String jarFilename = "app.jar";

    URL jarUrl = new URL("file", null, jarFilename);

    JarURLConnection juc = mock(JarURLConnection.class);
    when(juc.getJarFileURL()).thenReturn(jarUrl);

    URL url = createMockUrl(juc);

    String filename = "home.hbs";

    assertEquals(0, new URLTemplateSource(filename, url).lastModified());

    verify(juc).getJarFileURL();
  }

  private URL createThrowingMockUrl(IOException ioException) throws IOException {
    URLStreamHandler handler = new URLStreamHandler() {
      @Override
      protected URLConnection openConnection(final URL arg0) throws IOException {
        throw ioException;
      }
    };
    return new URL("http://foo.bar", "foo.bar", 80, "", handler);
  }

  private URL createMockUrl(URLConnection urlConnection) throws IOException {
    URLStreamHandler handler = new URLStreamHandler() {
      @Override
      protected URLConnection openConnection(final URL arg0) {
        return urlConnection;
      }
    };
    return new URL("http://foo.bar", "foo.bar", 80, "", handler);
  }
}
