/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.springmvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;

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
  public void shouldEnforceLogicalBoundaryAgainstTraversal() throws IOException {
    // The attacker only injects the traversal sequence, not the prefix.
    String maliciousPath = "../../application.properties";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              // sourceAt triggers resolve() and then getResource()
              templateLoader.sourceAt(maliciousPath);
            });

    assertTrue(exception.getMessage().contains("escapes Spring base prefix"));
  }

  @Test
  public void shouldBlockUrlFragmentInjection() throws IOException {
    Resource mockResource = mock(Resource.class);
    when(mockResource.exists()).thenReturn(true);

    // Simulate Spring returning a URL that contains a fragment (#)
    URL maliciousUrl = new URL("file:///etc/passwd#.hbs");
    when(mockResource.getURL()).thenReturn(maliciousUrl);

    // We must mock the exact string that the template loader will ask Spring for
    when(resourceLoader.getResource(anyString())).thenReturn(mockResource);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              templateLoader.sourceAt("classpath:/templates/hack#.hbs");
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

    when(resourceLoader.getResource(anyString())).thenReturn(mockResource);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              templateLoader.sourceAt("classpath:/templates/hack?.hbs");
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
}
