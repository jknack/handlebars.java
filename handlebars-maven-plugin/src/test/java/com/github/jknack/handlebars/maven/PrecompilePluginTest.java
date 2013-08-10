package com.github.jknack.handlebars.maven;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PrecompilePluginTest {

  @Test
  public void outputDirMustBeCreated() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/helpers");
    plugin.setSuffix(".html");
    plugin.setOutput("target/newdir/helpers.js");
    plugin.setProject(newProject());

    plugin.execute();
  }

  @Test
  public void missingHelperMustBeSilent() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/missing-helper");
    plugin.setSuffix(".html");
    plugin.setOutput("target/missing-helpers.js");
    plugin.setProject(newProject());

    plugin.execute();
  }

  @Test
  public void noFileMustBeCreatedIfNoTemplatesWereFound() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/no templates");
    plugin.setSuffix(".html");
    plugin.setOutput("target/no-helpers.js");
    plugin.setProject(newProject());

    plugin.execute();

    assertTrue(!new File("target/no-helpers.js").exists());
  }

  @Test(expected = MojoExecutionException.class)
  public void mustFailOnInvalidInputDirectory() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/missing");
    plugin.setSuffix(".html");
    plugin.setOutput("target/no-helpers.js");
    plugin.setProject(newProject());

    plugin.execute();
  }

  @Test(expected = MojoExecutionException.class)
  public void mustFailOnMissingFile() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/ioexception");
    plugin.setSuffix(".html");
    plugin.setOutput("target/no-helpers.js");
    plugin.setProject(newProject());

    plugin.execute();
  }

  @Test(expected = MojoFailureException.class)
  public void mustFailOnUnExpectedException() throws Exception {
    MavenProject project = createMock(MavenProject.class);
    Artifact artifact = createMock(Artifact.class);
    expect(project.getRuntimeClasspathElements()).andThrow(
        new DependencyResolutionRequiredException(artifact));
    replay(project, artifact);

    PrecompilePlugin plugin = new PrecompilePlugin();
    plugin.setPrefix("src/test/resources/no templates");
    plugin.setSuffix(".html");
    plugin.setOutput("target/no-helpers.js");
    plugin.setProject(project);

    plugin.execute();
  }

  @Test
  public void fileWithRuntimeMustBeLargerThanNormalFiles() throws Exception {
    PrecompilePlugin withoutRT = new PrecompilePlugin();
    withoutRT.setPrefix("src/test/resources/helpers");
    withoutRT.setSuffix(".html");
    withoutRT.setOutput("target/without-rt-helpers.js");
    withoutRT.setProject(newProject());

    withoutRT.execute();

    PrecompilePlugin withRT = new PrecompilePlugin();
    withRT.setPrefix("src/test/resources/helpers");
    withRT.setSuffix(".html");
    withRT.setOutput("target/with-rt-helpers.js");
    withRT.setIncludeRuntime(true);
    withRT.setProject(newProject());

    withRT.execute();

    assertTrue("File with runtime must be larger",
        FileUtils.fileRead("target/without-rt-helpers.js").length() <
        FileUtils.fileRead("target/with-rt-helpers.js").length());
  }

  @Test
  public void normalFileShouleBeLargerThanMinimizedFiles() throws Exception {
    PrecompilePlugin withoutRT = new PrecompilePlugin();
    withoutRT.setPrefix("src/test/resources/helpers");
    withoutRT.setSuffix(".html");
    withoutRT.setOutput("target/helpers-normal.js");
    withoutRT.setProject(newProject());

    withoutRT.execute();

    PrecompilePlugin withRT = new PrecompilePlugin();
    withRT.setPrefix("src/test/resources/helpers");
    withRT.setSuffix(".html");
    withRT.setOutput("target/helpers.min.js");
    withRT.setMinimize(true);
    withRT.setProject(newProject());

    withRT.execute();

    assertTrue("Normal file must be larger than minimized",
        FileUtils.fileRead("target/helpers-normal.js").length() >
        FileUtils.fileRead("target/helpers.min.js").length());
  }

  @Test
  public void partials() throws Exception {
    PrecompilePlugin plugin = new PrecompilePlugin();
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
