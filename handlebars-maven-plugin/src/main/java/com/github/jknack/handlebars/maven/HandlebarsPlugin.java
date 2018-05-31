/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This copy of Woodstox XML processor is licensed under the
 * Apache (Software) License, version 2.0 ("the License").
 * See the License for details about distribution rights, and the
 * specific rights regarding derivate works.
 *
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing Woodstox, in file "ASL2.0", under the same directory
 * as this file.
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

  /**
   * The maven project.
   */
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
