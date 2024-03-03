/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultipleClassLoadersMethodValueResolverTest {

  private static final String CLASS_NAME = "TestClass";

  Path path = Paths.get(System.getProperty("java.io.tmpdir"));

  private MethodValueResolver resolver = new MethodValueResolver();

  @BeforeEach
  public void compileTestClass() throws IOException, URISyntaxException {
    String sourceFileName = CLASS_NAME + ".java";
    File sourceFile = path.resolve(sourceFileName).toFile();

    URL sourceFileResourceUrl = getClass().getResource(sourceFileName).toURI().toURL();
    copyURLToFile(sourceFileResourceUrl, sourceFile);

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(
        null, null, null, "-d", path.toAbsolutePath().toString(), sourceFile.getAbsolutePath());
  }

  @Test
  public void canResolveMethodsFromTheSameClassLoadedByDistinctClassLoaders() throws Exception {
    assertEquals(
        "value",
        resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"));
    assertEquals(
        "value",
        resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"));
  }

  private Class<?> loadTestClassWithDistinctClassLoader() throws Exception {
    URL[] classpath = {path.toFile().toURI().toURL()};
    URLClassLoader loader = new URLClassLoader(classpath);
    Class<?> clazz = loader.loadClass(CLASS_NAME);
    loader.close();
    return clazz;
  }
}
