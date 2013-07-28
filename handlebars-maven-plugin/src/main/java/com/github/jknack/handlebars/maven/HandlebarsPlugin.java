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

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.I18nHelper;
import com.github.jknack.handlebars.helper.PrecompileHelper;
import com.github.jknack.handlebars.io.FileTemplateLoader;
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
@Mojo(name = "precompile", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
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
  private String suffix = ".hbs";

  /**
   * Minimize the output. Default is: false.
   */
  @Parameter
  private boolean minimize;

  /**
   * The maven project.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    notNull(prefix, "The prefix parameter is required.");
    notNull(output, "The output parameter is required.");

    File basedir = new File(prefix);
    File output = new File(this.output);
    boolean error = true;
    PrintWriter writer = null;
    InputStream runtimeIS = null;

    try {
      String realPrefix = basedir.getPath();

      Handlebars handlebars = new Handlebars(new FileTemplateLoader(basedir, suffix));

      final List<CharSequence> extras = new ArrayList<CharSequence>();

      @SuppressWarnings("unchecked")
      List<String> classpathElements = project.getRuntimeClasspathElements();
      final URL[] classpath = new URL[classpathElements.size()];
      for (int i = 0; i < classpath.length; i++) {
        classpath[i] = new File(classpathElements.get(i)).toURI().toURL();
      }

      i18nJs(handlebars, extras, classpath);

      i18n(handlebars);

      /**
       * Silent any missing helper.
       */
      silentHelpers(handlebars);

      File parent = output.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }

      runtimeIS = getClass().getResourceAsStream("/handlebars.runtime.js");

      writer = new PrintWriter(output);
      if (includeRuntime) {
        IOUtil.copy(runtimeIS, writer);
      }
      List<File> files = FileUtils.getFiles(basedir, "**/*" + suffix, null);
      Collections.sort(files);
      getLog().info("Compiling templates...");
      getLog().debug("Options:");
      getLog().debug("  output: " + output);
      getLog().debug("  prefix: " + realPrefix);
      getLog().debug("  suffix: " + suffix);
      getLog().debug("  minimize: " + minimize);
      getLog().debug("  includeRuntime: " + includeRuntime);

      writer.append("(function () {\n");
      Context nullContext = Context.newContext(null);
      for (File file : files) {
        String templateName = file.getPath().replace(realPrefix, "").replace(suffix, "");
        if (templateName.startsWith(File.separator)) {
          templateName = templateName.substring(File.separator.length());
        }
        templateName = templateName.replace(File.separator, "/");
        getLog().debug("compiling: " + templateName);

        handlebars.compile(templateName).apply(nullContext);

        Template template = handlebars.compileInline("{{precompile \"" + templateName + "\"}}");
        Map<String, Object> hash = new HashMap<String, Object>();
        hash.put("wrapper", "none");
        Options opts = new Options
            .Builder(handlebars, TagType.VAR, nullContext, template)
                .setHash(hash)
                .build();

        writer.append("// Source: ").append(file.getPath()).append("\n");
        writer.append(PrecompileHelper.INSTANCE.apply(templateName, opts)).append("\n\n");
      }
      // extras
      for (CharSequence extra : extras) {
        writer.append(extra).append("\n");
      }
      writer.append("\n})();");
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
    } catch (RuntimeException ex) {
      throw new MojoExecutionException(ex.getMessage(), ex);
    } catch (Exception ex) {
      throw new MojoFailureException(ex.getMessage(), ex);
    } finally {
      IOUtil.close(runtimeIS);
      IOUtil.close(writer);
      if (error) {
        output.delete();
      }
    }
  }

  /**
   * Silent any missing helper.
   *
   * @param handlebars The handlebars object.
   */
  private void silentHelpers(final Handlebars handlebars) {
    handlebars.registerHelper(Handlebars.HELPER_MISSING, new Helper<Object>() {
      @Override
      public CharSequence apply(final Object context, final Options options) throws IOException {
        return null;
      }
    });
  }

  /**
   * Override i18n helper.
   *
   * @param handlebars The handlebars object.
   */
  private void i18n(final Handlebars handlebars) {
    handlebars.registerHelper(I18nHelper.i18n.name(), new Helper<String>() {
      @Override
      public CharSequence apply(final String context, final Options options) throws IOException {
        return null;
      }

      @Override
      public String toString() {
        return I18nHelper.i18n.name() + "-maven-plugin";
      }
    });
  }

  /**
   * Override i18nJs helper.
   *
   * @param handlebars The handlebars object
   * @param extras Append output here.
   * @param classpath The project classpath.
   */
  private void i18nJs(final Handlebars handlebars, final List<CharSequence> extras,
      final URL[] classpath) {
    handlebars.registerHelper(I18nHelper.i18nJs.name(), new Helper<String>() {
      @Override
      public CharSequence apply(final String context, final Options options) throws IOException {
        Map<String, Object> hash = options.hash;
        hash.put("wrap", false);
        if (classpath.length > 0) {
          hash.put("classLoader",
              new URLClassLoader(classpath, getClass().getClassLoader()));
        }

        Options opts = new Options.Builder(options.handlebars, options.tagType, options.context,
            options.fn)
            .setHash(hash)
            .setInverse(options.inverse)
            .setParams(options.params)
            .build();

        StringBuilder output = new StringBuilder();
        output.append("// i18nJs output:\n");
        output.append("// register an empty i18nJs helper:\n");
        output.append(registerHelper(I18nHelper.i18nJs.name(), "if (arguments.length > 1) {\n"
            + "  I18n.locale=arguments[0];\n"
            + "}\n"
            + "return '';", "arguments"));
        output.append("// redirect i18n helper to i18n.js:\n");
        output.append(registerHelper(I18nHelper.i18n.name(), "var key = arguments[0],\n"
            + "  i18nOpts = {},\n"
            + "  len = arguments.length - 1,"
            + "  options = arguments[len];\n"
            + "for(var i = 1; i < len; i++) {\n"
            + "  i18nOpts['arg' + (i - 1)] = arguments[i];\n"
            + "}\n"
            + "i18nOpts.locale = options.hash.locale;\n"
            + "return I18n.t(key, i18nOpts);"));
        output.append(I18nHelper.i18nJs.apply(context, opts));
        extras.add(output);
        return null;
      }

      @Override
      public String toString() {
        return I18nHelper.i18nJs.name() + "-maven-plugin";
      }
    });
  }

  /**
   * JavaScript code for registering helpers.
   *
   * @param name The helper name.
   * @param body The helper body/
   * @param args The helper arguments.
   * @return JS code.
   */
  private CharSequence registerHelper(final String name, final String body, final String... args) {
    return String.format("Handlebars.registerHelper('%s', function (%s) {\n%s\n});\n\n", name,
        join(args, ", "), body);
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

  /**
   * @param includeRuntime True, if the handlebars.runtime.js file need to be included in the
   *        output. Default is: false.
   */
  public void setIncludeRuntime(final boolean includeRuntime) {
    this.includeRuntime = includeRuntime;
  }

  /**
   * @param minimize Minimize the output. Default is: false.
   */
  public void setMinimize(final boolean minimize) {
    this.minimize = minimize;
  }

  /**
   * @param output The output file.
   */
  public void setOutput(final String output) {
    this.output = output;
  }

  /**
   * @param prefix A prefix location, default is ${basedir}/src/main/webapp.
   */
  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  /**
   * @param suffix The file extension, default is: .hbs.
   */
  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }

  /**
   * @param project The maven project.
   */
  public void setProject(final MavenProject project) {
    this.project = project;
  }
}
