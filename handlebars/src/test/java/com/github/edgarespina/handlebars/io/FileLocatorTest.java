package com.github.edgarespina.handlebars.io;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

import org.junit.Test;

import com.github.edgarespina.handlebars.TemplateLoader;

/**
 * Unit test for {@link ClassTemplateLoader}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class FileLocatorTest {

  @Test
  public void locate() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"));
    Reader reader = locator.load(URI.create("template"));
    assertNotNull(reader);
  }

  @Test
  public void subFolder() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"), ".yml");
    Reader reader = locator.load(URI.create("specs/comments"));
    assertNotNull(reader);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"), ".yml");
    Reader reader = locator.load(URI.create("/specs/comments"));
    assertNotNull(reader);
  }

  @Test(expected = FileNotFoundException.class)
  public void failLocate() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources"));
    locator.load(URI.create("notExist"));
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources/specs"), ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }

  @Test
  public void setBasePathWithDash() throws IOException {
    TemplateLoader locator =
        new FileTemplateLoader(new File("src/test/resources/specs/"), ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }

}
