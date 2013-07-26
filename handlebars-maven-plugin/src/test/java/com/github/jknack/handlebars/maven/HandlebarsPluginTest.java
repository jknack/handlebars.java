package com.github.jknack.handlebars.maven;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

public class HandlebarsPluginTest {

  @Test
  public void i18nJs() throws Exception {
    HandlebarsPlugin plugin = new HandlebarsPlugin();
    plugin.setPrefix("src/test/resources/i18nJs");
    plugin.setSuffix(".html");
    plugin.setOutput("target/helpers-i18njs.js");
    plugin.setProject(newProject());

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/helpers-i18njs.expected"),
        FileUtils.fileRead("target/helpers-i18njs.js"));
  }

  @Test
  public void i18nJsCustomLocale() throws Exception {
    HandlebarsPlugin plugin = new HandlebarsPlugin();
    plugin.setPrefix("src/test/resources/i18nJsCustomLocale");
    plugin.setSuffix(".html");
    plugin.setOutput("target/helpers-i18njs-custom.js");
    plugin.setProject(newProject(new File("src/test/messages").getAbsolutePath()));

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/helpers-i18njs-custom.expected"),
        FileUtils.fileRead("target/helpers-i18njs-custom.js"));
  }

  @Test
  public void partials() throws Exception {
    HandlebarsPlugin plugin = new HandlebarsPlugin();
    plugin.setPrefix("src/test/resources/partials");
    plugin.setSuffix(".html");
    plugin.setOutput("target/helpers.js");
    plugin.setProject(newProject());

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/helpers.expected"),
        FileUtils.fileRead("target/helpers.js"));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = createMock(MavenProject.class);
    expect(project.getRuntimeClasspathElements()).andReturn(Lists.newArrayList(classpath));
    replay(project);
    return project;
  }
}
