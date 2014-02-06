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
        "    \"pagination_top_of_page\": \"Inicio de la página\"\n" +
        "  };\n" +
        "})();\n", FileUtils.fileRead("target/i230.js"));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = createMock(MavenProject.class);
    expect(project.getRuntimeClasspathElements()).andReturn(Lists.newArrayList(classpath));
    replay(project);
    return project;
  }
}
