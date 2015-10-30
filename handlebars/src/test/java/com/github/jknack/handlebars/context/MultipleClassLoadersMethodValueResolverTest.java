package com.github.jknack.handlebars.context;

import static org.apache.commons.io.FileUtils.copyURLToFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MultipleClassLoadersMethodValueResolverTest {

  private final static String CLASS_NAME = "TestClass";

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private MethodValueResolver resolver = new MethodValueResolver();

  @Before
  public void compileTestClass() throws IOException, URISyntaxException {
    String sourceFileName = CLASS_NAME + ".java";
    File sourceFile = temp.newFile(sourceFileName);

    URL sourceFileResourceUrl = getClass().getResource(sourceFileName).toURI().toURL();
    copyURLToFile(sourceFileResourceUrl, sourceFile);

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, "-d", temp.getRoot().getAbsolutePath(), sourceFile.getAbsolutePath());
  }

  @Test
  public void canResolveMethodsFromTheSameClassLoadedByDistinctClassLoaders() throws Exception {
    Assert.assertEquals(resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"), "value");
    Assert.assertEquals(resolver.resolve(loadTestClassWithDistinctClassLoader().newInstance(), "getField"), "value");
  }

  private Class<?> loadTestClassWithDistinctClassLoader() throws Exception {
    URL[] classpath = {temp.getRoot().toURI().toURL()};
    URLClassLoader loader = new URLClassLoader(classpath);
    Class<?> clazz = loader.loadClass(CLASS_NAME);
    loader.close();
    return clazz;
  }
}
