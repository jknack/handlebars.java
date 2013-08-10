package com.github.jknack.handlebars.maven;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

public class I18nJsPluginTest {

  @Test
  public void i18nJsNoMerge() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/messages.expected.js"),
        FileUtils.fileRead("target/messages.js"));

    assertEquals(FileUtils.fileRead("src/test/resources/messages_es_AR.expected.js"),
        FileUtils.fileRead("target/messages_es_AR.js"));
  }

  @Test
  public void i18nJsNoMergeDeepPath() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources/"));
    plugin.setBundle("deep.path.deep");
    plugin.setOutput("target");

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/deep.expected.js"),
        FileUtils.fileRead("target/deep.js"));
  }

  @Test
  public void i18nJsNoMergeAmd() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setAmd(true);

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/messages-amd.expected.js"),
        FileUtils.fileRead("target/messages.js"));

    assertEquals(FileUtils.fileRead("src/test/resources/messages_es_AR-amd.expected.js"),
        FileUtils.fileRead("target/messages_es_AR.js"));
  }

  @Test
  public void i18nJsWithMerge() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setMerge(true);

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/messages-merged.js"),
        FileUtils.fileRead("target/messages.js"));
  }

  @Test
  public void i18nJsWithMergeAmd() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setMerge(true);
    plugin.setAmd(true);

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/messages-merged-amd.js"),
        FileUtils.fileRead("target/messages.js"));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = createMock(MavenProject.class);
    expect(project.getRuntimeClasspathElements()).andReturn(Lists.newArrayList(classpath));
    replay(project);
    return project;
  }
}
