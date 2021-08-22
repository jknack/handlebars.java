package com.github.jknack.handlebars.maven;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

public class Issue234 {

  @Test
  public void withAmdOutput() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/templates");
    plugin.setSuffix(".hbs");
    plugin.setOutput("target/issue234.js");
    plugin.addTemplate("a");
    plugin.addTemplate("c");
    plugin.setAmd(true);
    plugin.setProject(newProject());
    plugin.setHandlebarsJsFile("/handlebars-v4.7.7.js");

    plugin.execute();

    equalsToIgnoreBlanks("src/test/resources/issue234.expected", "target/issue234.js");
  }

  private void equalsToIgnoreBlanks(String expected, String found) throws IOException {
    assertEquals(replaceWhiteCharsWithSpace(FileUtils.fileRead(expected)),
        replaceWhiteCharsWithSpace(FileUtils.fileRead(found)));
  }

  private String replaceWhiteCharsWithSpace(String content) {
    return content.trim()
        .replace("\\r\\n", "\\n")
        .replace("\r", "")
        .replace("\t", " ");
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = mock(MavenProject.class);
    when(project.getRuntimeClasspathElements()).thenReturn(Lists.newArrayList(classpath));
    return project;
  }
}
