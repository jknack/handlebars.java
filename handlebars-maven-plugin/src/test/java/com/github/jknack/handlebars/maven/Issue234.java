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
    plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");

    plugin.execute();

    assertEquals(FileUtils.fileRead("src/test/resources/issue234.expected.js"),
        FileUtils.fileRead("target/issue234.js"));
  }

  private MavenProject newProject(final String... classpath)
      throws DependencyResolutionRequiredException {
    MavenProject project = createMock(MavenProject.class);
    expect(project.getRuntimeClasspathElements()).andReturn(Lists.newArrayList(classpath));
    replay(project);
    return project;
  }
}
