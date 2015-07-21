package com.github.jknack.handlebars.io;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({URLTemplateSource.class })
public class URLTemplateSourceTest {

  @Test
  public void content() throws Exception {
    String content = "...";

    URLTemplateSource templateSource = PowerMock.createPartialMockForAllMethodsExcept(
        URLTemplateSource.class, "content");

    PowerMock.expectPrivate(templateSource, "reader").andReturn(new StringReader(content));

    Object[] mocks = {templateSource };

    replay(mocks);

    assertEquals(content, templateSource.content());

    verify(mocks);
  }

  @Test
  public void openConnectionThrowIOException() throws IOException {
    URL url = PowerMock.createMock(URL.class);
    expect(url.openConnection()).andThrow(new IOException());

    String filename = "home.hbs";

    Object[] mocks = {url };

    PowerMock.replay(mocks);

    assertEquals(-1, new URLTemplateSource(filename, url).lastModified());

    PowerMock.verify(mocks);
  }

  @Test
  public void closeOpenedConnection() throws IOException {
    InputStream is = PowerMock.createMock(InputStream.class);
    is.close();

    URLConnection uc = PowerMock.createMock(URLConnection.class);
    expect(uc.getLastModified()).andReturn(123L);
    expect(uc.getInputStream()).andReturn(is);

    URL url = PowerMock.createMock(URL.class);
    expect(url.openConnection()).andReturn(uc);

    String filename = "home.hbs";

    Object[] mocks = {url, uc, is };

    PowerMock.replay(mocks);

    assertEquals(123L, new URLTemplateSource(filename, url).lastModified());

    PowerMock.verify(mocks);
  }

  @Test
  public void lastModifiedFromJar() throws IOException {
    String jarFilename = "app.jar";

    URL jarUrl = PowerMock.createMock(URL.class);
    expect(jarUrl.getProtocol()).andReturn("file");
    expect(jarUrl.getFile()).andReturn(jarFilename);

    JarURLConnection juc = PowerMock.createMock(JarURLConnection.class);
    expect(juc.getJarFileURL()).andReturn(jarUrl);

    URL url = PowerMock.createMock(URL.class);
    expect(url.openConnection()).andReturn(juc);

    String filename = "home.hbs";

    Object[] mocks = {url, juc, jarUrl};

    PowerMock.replay(mocks);

    assertEquals(0, new URLTemplateSource(filename, url).lastModified());

    PowerMock.verify(mocks);
  }

}
