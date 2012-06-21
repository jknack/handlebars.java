package com.github.edgarespina.handlerbars.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.junit.Test;

import com.github.edgarespina.handlerbars.TemplateLoader;

/**
 * Unit test for {@link ClassTemplateLoader}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileLocatorTest {

  @Test
  public void locate() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("template.html"));
    assertNotNull(reader);
  }

  @Test
  public void subFolder() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("specs/comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("/specs/comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void failLocate() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("notExist.html"));
    assertNotNull(reader);
    assertEquals("", reader.toString());
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources/specs"));
    Reader reader = locator.load(URI.create("comments.yml"));
    assertNotNull(reader);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    TemplateLoader<File> locator =
        new FileTemplateLoader(new File("src/test/resources/specs/"));
    Reader reader = locator.load(URI.create("comments.yml"));
    assertNotNull(reader);
  }

}
