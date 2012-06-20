package com.github.edgarespina.handlerbars.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.junit.Test;

import com.github.edgarespina.handlerbars.ResourceLocator;

/**
 * Unit test for {@link ClasspathLocator}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileLocatorTest {

  @Test
  public void locate() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources"));
    Reader reader = locator.locate(URI.create("template.html"));
    assertNotNull(reader);
  }

  @Test
  public void subFolder() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources"));
    Reader reader = locator.locate(URI.create("specs/comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources"));
    Reader reader = locator.locate(URI.create("/specs/comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void failLocate() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources"));
    Reader reader = locator.locate(URI.create("notExist.html"));
    assertNotNull(reader);
    assertEquals("", reader.toString());
  }

  @Test
  public void setBasePath() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources/specs"));
    Reader reader = locator.locate(URI.create("comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    ResourceLocator<File> locator =
        new FileLocator(new File("src/test/resources/specs/"));
    Reader reader = locator.locate(URI.create("comments.yml"));
    assertNotNull(reader);
  }

}
