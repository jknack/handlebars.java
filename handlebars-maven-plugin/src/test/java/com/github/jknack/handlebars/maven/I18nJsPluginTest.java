package com.github.jknack.handlebars.maven;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class I18nJsPluginTest {

  @Test
  public void i18nJsNoMerge() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");

    plugin.execute();

    assertEquals(tokens("src/test/resources/messages.expected.js"),
        tokens("target/messages.js"));

    assertEquals(tokens("src/test/resources/messages_es_AR.expected.js"),
        tokens("target/messages_es_AR.js"));

    FileUtils.copyFile(new File("target/messages.js"), new File("target/messages-tests.js"));
  }

  @Test
  public void i18nJsNoMergeDeepPath() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources/"));
    plugin.setBundle("deep.path.deep");
    plugin.setOutput("target");

    plugin.execute();

    equalsToIgnoreBlanks("src/test/resources/deep.expected.js", "target/deep.js");
  }

  @Test
  public void i18nJsNoMergeAmd() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setAmd(true);

    plugin.execute();

    assertEquals(tokens("src/test/resources/messages-amd.expected.js"),
        tokens("target/messages.js"));

    assertEquals(tokens("src/test/resources/messages_es_AR-amd.expected.js"),
        tokens("target/messages_es_AR.js"));
  }

  @Test
  public void i18nJsWithMerge() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setMerge(true);

    plugin.execute();

    assertEquals(tokens("src/test/resources/messages-merged.js"),
        tokens("target/messages.js"));
  }

  @Test
  public void i18nJsWithMergeAmd() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");
    plugin.setMerge(true);
    plugin.setAmd(true);

    plugin.execute();

    assertEquals(tokens("src/test/resources/messages-merged-amd.js"),
        tokens("target/messages.js"));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = mock(MavenProject.class);
    when(project.getRuntimeClasspathElements()).thenReturn(Lists.newArrayList(classpath));
    return project;
  }

  /**
   * Matches on tokens and avoid errors between Java 6.x and Java 7.x.
   */
  private Set<String> tokens(final String filename) throws IOException {
    String content = FileUtils.fileRead(filename)
        .replace("\r", "")
        .replace("\t", " ");
    return Sets.newLinkedHashSet(Splitter.on(Pattern.compile("\\s|,")).split(content));
  }

  private void equalsToIgnoreBlanks(String expected, String found) throws IOException {
    assertEquals(replaceWhiteCharsWithSpace(FileUtils.fileRead(expected)),
        replaceWhiteCharsWithSpace(FileUtils.fileRead(found)));
  }

  private String replaceWhiteCharsWithSpace(String content) {
    return content
        .replace("\r", "")
        .replace("\t", " ")
        .trim();
  }
}
