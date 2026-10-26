/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;

public class SpringTemplateLoaderTest {

  private ResourceLoader resourceLoader;
  private SpringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    resourceLoader = mock(ResourceLoader.class);
    templateLoader = new SpringTemplateLoader(resourceLoader);
    templateLoader.setPrefix("classpath:/templates/");
    templateLoader.setSuffix(".hbs");
  }

  @Test
  public void sourceAt() throws IOException {
    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());

    TemplateSource source = loader.sourceAt("template");

    assertNotNull(source);
  }

  @Test
  public void fileNotFound() throws IOException {
    assertThrows(
        IOException.class,
        () -> new SpringTemplateLoader(new DefaultResourceLoader()).sourceAt("missingFile"));
  }

  @Test
  public void classpathPrefixResolvesExplodedResource() throws IOException {
    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());
    loader.setPrefix("classpath:/templates/");
    loader.setSuffix(".hbs");

    assertNotNull(loader.sourceAt("hello"));
  }

  @Test
  public void classpathPrefixAcceptsJarEntryUrls() throws IOException {
    Resource mockResource = mock(Resource.class);
    when(mockResource.exists()).thenReturn(true);
    when(mockResource.isFile()).thenReturn(false);

    URL jarUrl = new URL("jar:file:/application.jar!/templates/hello.hbs");
    when(mockResource.getURL()).thenReturn(jarUrl);

    when(resourceLoader.getResource("classpath:/templates/hello.hbs")).thenReturn(mockResource);

    assertNotNull(templateLoader.sourceAt("hello"));
  }

  @Test
  public void shouldBlockContainsBypassInClasspath() {
    // The logical check happens before resource resolution, so we don't need mock behavior.
    // It catches the traversal on the logical string level immediately.
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              templateLoader.sourceAt("../../com/myapp/secret/templates/keys");
            });

    assertTrue(exception.getMessage().contains("Logical path escapes classpath prefix"));
  }

  @Test
  public void shouldEnforceLogicalBoundaryAgainstTraversal() {
    // The logical check happens before resource resolution, so we don't need mock behavior.
    // It catches the traversal on the logical string level immediately.
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              templateLoader.sourceAt("../../application.properties");
            });

    assertTrue(exception.getMessage().contains("Logical path escapes classpath prefix"));
  }

  @Test
  public void shouldBlockUrlFragmentInjection() throws IOException {
    Resource mockResource = mock(Resource.class);
    when(mockResource.exists()).thenReturn(true);

    // Simulate Spring returning a URL that contains a fragment (#)
    URL maliciousUrl = new URL("file:///etc/passwd#.hbs");
    when(mockResource.getURL()).thenReturn(maliciousUrl);

    // Mock the exact valid string Handlebars will request after prefix/suffix append
    when(resourceLoader.getResource("classpath:/templates/hack.hbs")).thenReturn(mockResource);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              // Pass a clean string to bypass OS-level Paths.get() restrictions
              templateLoader.sourceAt("hack");
            });

    assertTrue(exception.getMessage().contains("Template URL must not contain a fragment"));
  }

  @Test
  public void shouldBlockUrlQueryInjection() throws IOException {
    Resource mockResource = mock(Resource.class);
    when(mockResource.exists()).thenReturn(true);

    // Simulate Spring returning a URL that contains a query (?)
    URL maliciousUrl = new URL("file:///etc/passwd?.hbs");
    when(mockResource.getURL()).thenReturn(maliciousUrl);

    // Mock the exact valid string Handlebars will request after prefix/suffix append
    when(resourceLoader.getResource("classpath:/templates/hack.hbs")).thenReturn(mockResource);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              // Pass a clean string to bypass OS-level Paths.get() restrictions
              templateLoader.sourceAt("hack");
            });

    assertTrue(exception.getMessage().contains("Template URL must not contain a query"));
  }

  @Test
  public void resolveShouldNotPreserveDynamicProtocols() {
    // The resolve method should simply append the prefix and suffix,
    // it should NOT extract "file:" and place it at the front of the string anymore.
    String resolved = templateLoader.resolve("file:/etc/passwd");

    // Prefix + Input + Suffix
    assertEquals("classpath:/templates/file:/etc/passwd.hbs", resolved);
  }

  @Test
  public void shouldBlockPercentEncodedPathTraversal() throws IOException {
    Path root = Files.createTempDirectory("handlebars-test");
    Path templates = Files.createDirectory(root.resolve("templates"));

    // Create a target file outside the template directory
    Files.writeString(root.resolve("secret.hbs"), "VULNERABLE");

    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());

    // Use an OS-agnostic absolute URI format to prevent Windows backslash parsing issues
    loader.setPrefix(templates.toUri().toString());
    loader.setSuffix(".hbs");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> loader.sourceAt("%2e%2e/secret"));

    assertTrue(exception.getMessage().contains("escapes Spring base"));
  }

  @Test
  public void shouldBlockPartialDirectoryMatch() throws IOException {
    Path root = Files.createTempDirectory("handlebars-test");
    Path templates = Files.createDirectory(root.resolve("templates"));
    Path templatesSecret = Files.createDirectory(root.resolve("templates-secret"));

    Files.writeString(templatesSecret.resolve("secret.hbs"), "VULNERABLE");

    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader());

    // Convert to URI, but explicitly strip the trailing slash to test the partial match
    // vulnerability
    String prefixUri = templates.toUri().toString();
    if (prefixUri.endsWith("/")) {
      prefixUri = prefixUri.substring(0, prefixUri.length() - 1);
    }

    loader.setPrefix(prefixUri);
    loader.setSuffix(".hbs");

    // Use "../" to back out of the Handlebars-enforced slash, traversing into the sibling
    // directory.
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> loader.sourceAt("../templates-secret/secret"));

    assertTrue(exception.getMessage().contains("escapes Spring base"));
  }

  @Test
  public void shouldResolveTemplatesAcrossMultiModuleClasspath() throws IOException {
    Path root = Files.createTempDirectory("handlebars-multimodule");

    // Simulate Module A (e.g., target/classes/)
    Path moduleA = Files.createDirectory(root.resolve("moduleA"));
    Path templatesA = Files.createDirectory(moduleA.resolve("templates"));
    Files.writeString(templatesA.resolve("template-a.hbs"), "Template A");

    // Simulate Module B (e.g., target/test-classes/ or a sibling module)
    Path moduleB = Files.createDirectory(root.resolve("moduleB"));
    Path templatesB = Files.createDirectory(moduleB.resolve("templates"));
    Files.writeString(templatesB.resolve("template-b.hbs"), "Template B");

    // Create a ClassLoader containing both roots
    URLClassLoader classLoader =
        new URLClassLoader(
            new URL[] {moduleA.toUri().toURL(), moduleB.toUri().toURL()},
            getClass().getClassLoader());

    // Use Spring's DefaultResourceLoader backed by our multi-root classloader
    SpringTemplateLoader loader = new SpringTemplateLoader(new DefaultResourceLoader(classLoader));
    loader.setPrefix("classpath:/templates/");
    loader.setSuffix(".hbs");

    // The loader should find templates in both roots flawlessly without canonical file mismatches
    assertNotNull(loader.sourceAt("template-a"));
    assertNotNull(loader.sourceAt("template-b"));
  }
}
