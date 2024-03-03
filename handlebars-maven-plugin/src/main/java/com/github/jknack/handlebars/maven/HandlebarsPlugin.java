/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.maven;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Handlebars maven plugin base class.
 *
 * @author edgar.espina
 * @since 1.1.0
 */
public abstract class HandlebarsPlugin extends AbstractMojo {

  /** The maven project. */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  protected MavenProject project;

  /**
   * @param project The maven project.
   */
  public void setProject(final MavenProject project) {
    this.project = project;
  }

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    try {
      doExecute();
    } catch (MojoExecutionException | MojoFailureException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new MojoExecutionException(ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new MojoFailureException(ex.getMessage(), ex);
    }
  }

  /**
   * Execute the plugin without dealing with Maven exceptions.
   *
   * @throws Exception If something goes wrong.
   */
  protected abstract void doExecute() throws Exception;

  /**
   * Retrieve the project classpath.
   *
   * @return The project classpath.
   * @throws Exception If the classpath can't be resolved.
   */
  protected URL[] projectClasspath() throws Exception {
    @SuppressWarnings("unchecked")
    List<String> classpathElements = project.getRuntimeClasspathElements();
    final URL[] classpath = new URL[classpathElements.size()];
    for (int i = 0; i < classpath.length; i++) {
      classpath[i] = new File(classpathElements.get(i)).toURI().toURL();
    }
    return classpath;
  }
}
