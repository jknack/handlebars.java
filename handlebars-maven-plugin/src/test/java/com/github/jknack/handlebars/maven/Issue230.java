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

public class Issue230 {

  @Test
  public void issue230() throws Exception {
    I18nJsPlugin plugin = new I18nJsPlugin();
    plugin.setBundle("i230");
    plugin.setProject(newProject("src/test/resources"));
    plugin.setOutput("target");

    plugin.execute();

    assertEquals("(function() {\n" +
        "  /* English (United States) */\n" +
        "  I18n.translations = I18n.translations || {};\n" +
        "  I18n.translations['en_US'] = {\n" +
        "    \"pagination_top_of_page\": \"Inicio de la pagina\"\n" +
        "  };\n" +
        "})();", replaceWhiteCharsWithSpace(FileUtils.fileRead("target/i230.js")));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = mock(MavenProject.class);
    when(project.getRuntimeClasspathElements()).thenReturn(Lists.newArrayList(classpath));
    return project;
  }

  private String replaceWhiteCharsWithSpace(String content) {
    return content
        .replace("\r", "")
        .replace("\t", " ")
        .trim();
  }
}
