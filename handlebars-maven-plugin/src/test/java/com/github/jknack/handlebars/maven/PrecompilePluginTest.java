/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class PrecompilePluginTest {

  @Test
  public void i18nJs() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/i18nJs");
    plugin.setSuffix(".html");
    plugin.setOutput("target/helpers-i18njs.js");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();

    equalsToIgnoreBlanks("src/test/resources/helpers-i18njs.expected", "target/helpers-i18njs.js");
  }

  private void equalsToIgnoreBlanks(String expected, String found) throws IOException {
    assertEquals(
        replaceWhiteCharsWithSpace(FileUtils.fileRead(expected)),
        replaceWhiteCharsWithSpace(FileUtils.fileRead(found)));
  }

  private String replaceWhiteCharsWithSpace(String content) {
    return content.replace("\\r\\n", "\\n").replace("\r", "").replace("\t", " ").trim();
  }

  @Test
  public void chooseSpecificFiles() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/templates");
    plugin.setSuffix(".hbs");
    plugin.setOutput("target/specific-files.js");
    plugin.addTemplate("a");
    plugin.addTemplate("c");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();

    equalsToIgnoreBlanks("src/test/resources/specific-files.expected", "target/specific-files.js");
  }

  @Test
  public void outputDirMustBeCreated() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/helpers");
    plugin.setSuffix(".html");
    plugin.setOutput("target/newdir/helpers.js");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();
  }

  @Test
  public void missingHelperMustBeSilent() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/missing-helper");
    plugin.setSuffix(".html");
    plugin.setOutput("target/missing-helpers.js");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();
  }

  @Test
  public void noFileMustBeCreatedIfNoTemplatesWereFound() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/no templates");
    plugin.setSuffix(".html");
    plugin.setOutput("target/no-helpers.js");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();

    assertTrue(!new File("target/no-helpers.js").exists());
  }

  @Test
  public void mustFailOnInvalidInputDirectory() throws Exception {
    Assertions.assertThrows(
        MojoExecutionException.class,
        () -> {
          PrecompilePlugin plugin = new PrecompilePlugin();
          plugin.setPrefix("src/test/resources/missing");
          plugin.setSuffix(".html");
          plugin.setOutput("target/no-helpers.js");
          plugin.setProject(newProject());

          plugin.execute();
        });
  }

  @Test
  public void mustFailOnMissingFile() throws Exception {
    Assertions.assertThrows(
        MojoExecutionException.class,
        () -> {
          PrecompilePlugin plugin = new PrecompilePlugin();
          plugin.setPrefix("src/test/resources/ioexception");
          plugin.setSuffix(".html");
          plugin.setOutput("target/no-helpers.js");
          plugin.setProject(newProject());

          plugin.execute();
        });
  }

  @Test
  public void mustFailOnUnExpectedException() throws Exception {
    Assertions.assertThrows(
        MojoFailureException.class,
        () -> {
          MavenProject project = mock(MavenProject.class);
          when(project.getRuntimeClasspathElements())
              .thenThrow(new DependencyResolutionRequiredException(null));

          PrecompilePlugin plugin = new PrecompilePlugin();
          plugin.setPrefix("src/test/resources/no templates");
          plugin.setSuffix(".html");
          plugin.setOutput("target/no-helpers.js");
          plugin.setProject(project);
          plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

          plugin.execute();
        });
  }

  @Test
  public void fileWithRuntimeMustBeLargerThanNormalFiles() throws Exception {
    PrecompilePlugin withoutRT = new PrecompilePlugin();
    withoutRT.setPrefix("src/test/resources/helpers");
    withoutRT.setSuffix(".html");
    withoutRT.setOutput("target/without-rt-helpers.js");
    withoutRT.setProject(newProject());
    withoutRT.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    withoutRT.execute();

    PrecompilePlugin withRT = new PrecompilePlugin();
    withRT.setPrefix("src/test/resources/helpers");
    withRT.setSuffix(".html");
    withRT.setOutput("target/with-rt-helpers.js");
    withRT.setRuntime("src/test/resources/handlebars.runtime.js");
    withRT.setProject(newProject());
    withRT.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    withRT.execute();

    assertTrue(
        FileUtils.fileRead("target/without-rt-helpers.js").length()
            < FileUtils.fileRead("target/with-rt-helpers.js").length(),
        "File with runtime must be larger");
  }

  @Test
  public void normalFileShouleBeLargerThanMinimizedFiles() throws Exception {
    PrecompilePlugin withoutRT = new PrecompilePlugin();
    withoutRT.setPrefix("src/test/resources/helpers");
    withoutRT.setSuffix(".html");
    withoutRT.setOutput("target/helpers-normal.js");
    withoutRT.setProject(newProject());
    withoutRT.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    withoutRT.execute();

    PrecompilePlugin withRT = new PrecompilePlugin();
    withRT.setPrefix("src/test/resources/helpers");
    withRT.setSuffix(".html");
    withRT.setOutput("target/helpers.min.js");
    withRT.setMinimize(true);
    withRT.setProject(newProject());
    withRT.setHandlebarsJsFile("/handlebars-v4.7.7.js");
    withRT.execute();

    assertTrue(
        FileUtils.fileRead("target/helpers-normal.js").length()
            > FileUtils.fileRead("target/helpers.min.js").length(),
        "Normal file must be larger than minimized");
  }

  @Test
  public void partials() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/partials");
    plugin.setSuffix(".html");
    plugin.setOutput("target/helpers.js");
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();

    equalsToIgnoreBlanks("src/test/resources/helpers.expected", "target/helpers.js");
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = mock(MavenProject.class);
    when(project.getRuntimeClasspathElements()).thenReturn(Lists.newArrayList(classpath));
    return project;
  }
}
