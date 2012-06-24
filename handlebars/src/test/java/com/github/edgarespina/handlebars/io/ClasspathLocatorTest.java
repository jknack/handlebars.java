package com.github.edgarespina.handlebars.io;

import static org.junit.Assert.assertNotNull;

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
public class ClasspathLocatorTest {

  @Test
  public void locate() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader();
    Reader reader = locator.load(URI.create("template"));
    assertNotNull(reader);
  }

  @Test
  public void subFolder() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader();
    locator.setSuffix(".yml");
    Reader reader = locator.load(URI.create("specs/comments"));
    assertNotNull(reader);
  }

  @Test
  public void subFolderwithDashAtBeginning() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader();
    locator.setSuffix(".yml");
    Reader reader = locator.load(URI.create("/specs/comments"));
    assertNotNull(reader);
  }

  @Test(expected = FileNotFoundException.class)
  public void failLocate() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader();
    locator.load(URI.create("notExist"));
  }

  @Test
  public void setBasePath() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader("/specs", ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }

  @Test
  public void setBasePathWithDashDash() throws IOException {
    TemplateLoader locator = new ClassTemplateLoader("/specs/", ".yml");
    Reader reader = locator.load(URI.create("comments"));
    assertNotNull(reader);
  }
}
