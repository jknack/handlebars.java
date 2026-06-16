/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PathTraversalTest {
  private Path baseDir;
  private Path secretFile;

  @BeforeEach
  public void setUp() throws IOException {
    // Setup a secure base directory
    baseDir = Files.createTempDirectory("handlebars-templates");

    // Create a legitimate template inside the base dir
    Files.write(baseDir.resolve("valid.hbs"), "Hello {{name}}".getBytes());

    // Create a secret file OUTSIDE the base dir (simulating /etc/passwd or similar)
    secretFile = Files.createTempFile("secret", ".hbs");
    Files.write(secretFile, "SECRET_DATA".getBytes());
  }

  @AfterEach
  public void tearDown() throws IOException {
    Files.deleteIfExists(baseDir.resolve("valid.hbs"));
    if (Files.exists(baseDir)) {
      try (Stream<Path> walk = Files.walk(baseDir)) {
        walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      }
    }
    Files.deleteIfExists(baseDir);
    Files.deleteIfExists(secretFile);
  }

  @Test
  public void shouldBlockTraversalWithEmptySuffix() throws IOException {
    // Create a sensitive file outside the base directory with a specific extension
    Path sensitiveConfig = Files.createTempFile("database", ".json");
    Files.write(sensitiveConfig, "DB_PASS=supersecret".getBytes());

    // Configure the loader with an empty suffix
    FileTemplateLoader loader = new FileTemplateLoader(baseDir.toFile(), "");

    // Attempt to traverse up and access the exact file
    String traversalPath = "../../../../../../../../../.." + sensitiveConfig.toAbsolutePath();

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              loader.sourceAt(traversalPath);
            });

    assertTrue(exception.getMessage().contains("escapes base directory"));

    Files.deleteIfExists(sensitiveConfig);
  }

  @Test
  public void shouldLoadValidTemplate() throws IOException {
    FileTemplateLoader loader = new FileTemplateLoader(baseDir.toFile(), ".hbs");
    TemplateSource source = loader.sourceAt("valid");

    assertEquals("Hello {{name}}", source.content(StandardCharsets.UTF_8));
  }

  @Test
  public void shouldBlockDirectoryTraversalEscapingBaseDir() {
    FileTemplateLoader loader = new FileTemplateLoader(baseDir.toFile(), ".hbs");

    // Attempt to traverse up and access the secret file
    String traversalPath =
        "../../../../../../../../../.."
            + secretFile.toAbsolutePath().toString().replace(".hbs", "");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              loader.sourceAt(traversalPath);
            });

    assertTrue(exception.getMessage().contains("escapes base directory"));
  }

  @Test
  public void shouldAllowRelativeTraversalWithinBaseDir() throws IOException {
    // Setup a subdirectory structure: /baseDir/sub/
    File subDir = new File(baseDir.toFile(), "sub");
    subDir.mkdir();
    Files.write(new File(subDir, "partial.hbs").toPath(), "Partial".getBytes());

    FileTemplateLoader loader = new FileTemplateLoader(baseDir.toFile(), ".hbs");

    // Traversing "up" but still landing inside the base directory is legitimate
    TemplateSource source = loader.sourceAt("sub/../sub/partial");
    assertEquals("Partial", source.content(StandardCharsets.UTF_8));

    subDir.delete();
  }

  // ----------------------------------------------------------------------------------------------------------------
  // Classpath:
  // ----------------------------------------------------------------------------------------------------------------
  @Test
  public void shouldLoadValidClasspathResource() throws Exception {
    ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/", ".class");
    // Loading a known class file from the root classpath for testing purposes
    assertNotNull(loader.sourceAt(PathTraversalTest.class.getName().replace(".", "/")));
  }

  @Test
  public void shouldBlockLogicalClasspathTraversal() {
    // Restrict loader to a specific prefix
    ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/templates/", ".hbs");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              // Attempt to break out of /templates/
              loader.sourceAt("../application.properties");
            });

    assertTrue(exception.getMessage().contains("escapes base prefix"));
  }

  @Test
  public void shouldBlockLogicalClasspathTraversalWithMultipleDots() {
    ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/templates/", ".hbs");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              loader.sourceAt("../../etc/passwd");
            });

    assertTrue(exception.getMessage().contains("escapes base prefix"));
  }

  @Test
  public void shouldBlockClasspathTraversalWithEmptySuffix() {
    // Configure the loader with an empty suffix to allow exact filename matching
    ClassPathTemplateLoader loader = new ClassPathTemplateLoader("/templates/", "");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              // Attempt to break out of /templates/ to read a root configuration file
              loader.sourceAt("../application.properties");
            });

    assertTrue(exception.getMessage().contains("escapes base prefix"));
  }

  @Test
  public void shouldNotRootWithEmptySuffix() {
    assertThrows(IllegalArgumentException.class, () -> new ClassPathTemplateLoader("/", ""));
    assertThrows(IllegalArgumentException.class, () -> new ClassPathTemplateLoader("/", null));
  }
}
