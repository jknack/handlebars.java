/**
 * Copyright (c) 2012-2013 Edgar Espina
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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.PrecompileHelper;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;

/**
 * Compile Handlebars templates to JavaScript using Rhino.
 *
 * @author edgar.espina
 * @since 1.1.0
 */
@Mojo(name = "precompile", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class HandlebarsPlugin extends AbstractMojo {

  /**
   * The output file.
   */
  @Parameter(required = true)
  private String output;

  /**
   * True, if the handlebars.runtime.js file need to be included in the output. Default is: false.
   */
  @Parameter
  private boolean includeRuntime;

  /**
   * A prefix location, default is ${basedir}/src/main/webapp.
   */
  @Parameter(defaultValue = "${basedir}/src/main/webapp")
  private String prefix;

  /**
   * The file extension, default is: .hbs.
   */
  @Parameter(defaultValue = ".hbs")
  private String suffix;

  /**
   * Minimize the ouput. Default is: false.
   */
  @Parameter
  private boolean minimize;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    File basedir = new File(prefix);
    String realPrefix = basedir.getPath();
    TemplateLoader loader = new FileTemplateLoader(basedir, suffix);
    Handlebars handlebars = new Handlebars(loader);
    File output = new File(this.output);
    PrintWriter writer = null;
    boolean error = true;
    InputStream runtimeIS = getClass().getResourceAsStream("/handlebars.runtime.js");
    try {
      writer = new PrintWriter(output);
      if (includeRuntime) {
        IOUtil.copy(runtimeIS, writer);
      }
      List<File> files = FileUtils.getFiles(basedir, "**/*" + suffix, null);
      getLog().info("Compiling templates...");
      getLog().debug("Options:");
      getLog().debug("  output: " + output);
      getLog().debug("  prefix: " + realPrefix);
      getLog().debug("  suffix: " + suffix);
      getLog().debug("  minimize: " + minimize);
      getLog().debug("  includeRuntime: " + includeRuntime);

      Context nullContext = Context.newContext(null);
      for (File file : files) {
        String templateName = file.getPath().replace(realPrefix, "").replace(suffix, "");
        if (templateName.startsWith(File.separator)) {
          templateName = templateName.substring(File.separator.length());
        }

        templateName = templateName.replace(File.separator, "/");
        getLog().debug("compiling: " + templateName);
        Template template = handlebars.compileInline("{{precompile \"" + templateName + "\"}}");
        Options opts = new Options.Builder(handlebars, TagType.VAR, nullContext, template)
            .build();

        writer.append("// Source: ").append(file.getPath()).append("\n");
        writer.append(PrecompileHelper.INSTANCE.apply(templateName, opts)).append("\n\n");
      }
      writer.flush();
      IOUtil.close(writer);
      if (minimize) {
        minimize(output);
      }
      if (files.size() > 0) {
        getLog().info("  templates were saved in: " + output);
        error = false;
      } else {
        getLog().warn("  no templates were found");
      }
    } catch (IOException ex) {
      throw new MojoFailureException("Can't scan directory " + basedir, ex);
    } finally {
      IOUtil.close(runtimeIS);
      IOUtil.close(writer);
      if (error) {
        output.delete();
      }
    }
  }

  /**
   * Minimize the output using google closure compiler.
   *
   * @param output The input file to minimize.
   * @throws IOException If something goes wrong.
   * @throws MojoFailureException If something goes wrong.
   */
  private void minimize(final File output) throws IOException, MojoFailureException {
    final CompilerOptions options = new CompilerOptions();
    options.setCodingConvention(new ClosureCodingConvention());
    options.setOutputCharset("UTF-8");
    options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);
    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

    Compiler.setLoggingLevel(Level.SEVERE);
    Compiler compiler = new Compiler();
    compiler.disableThreads();
    compiler.initOptions(options);

    Result result = compiler.compile(Collections.<SourceFile> emptyList(),
        Arrays.asList(SourceFile.fromFile(output)), options);
    if (result.success) {
      FileUtils.fileWrite(output, compiler.toSource());
    } else {
      JSError[] errors = result.errors;
      throw new MojoFailureException(errors[0].toString());
    }
  }

}
