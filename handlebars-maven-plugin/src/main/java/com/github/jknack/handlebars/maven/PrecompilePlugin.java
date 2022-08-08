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

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HelperRegistry;
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
public class PrecompilePlugin extends HandlebarsPlugin {

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
   * The template files to precompile.
   */
  @Parameter
  private List<String> templates;

  /**
   * The output file.
   */
  @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}/js/helpers.js")
  private String output;

  /**
   * The handlebars js file.
   */
  @Parameter(defaultValue = "/handlebars-v4.7.7.js")
  private String handlebarsJsFile;

  /**
   * True, if the handlebars.runtime.js file need to be included in the output. Default is: false.
   */
  @Parameter
  private String runtime;

  /**
   * Minimize the output. Default is: false.
   */
  @Parameter
  private boolean minimize;

  /**
   * True, if the output should be in the AMD format. Default is false.
   */
  @Parameter
  private boolean amd;

  /**
   * The encoding char set. Default is: UTF-8.
   */
  @Parameter
  private String encoding = "UTF-8";

  @Override
  protected void doExecute() throws Exception {
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
      handlebars.handlebarsJsFile(handlebarsJsFile);
      handlebars.setCharset(Charset.forName(encoding));

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
      if (parent != null && !parent.exists()) {
        parent.mkdirs();
      }

      writer = new PrintWriter(output, encoding);
      if (runtime != null) {
        runtimeIS = new FileInputStream(new File(runtime));
        IOUtil.copy(runtimeIS, writer);
      }

      List<File> files;
      if (templates != null && templates.size() > 0) {
        files = new ArrayList<File>();
        for (String templateName : templates) {
          File file = FileUtils.getFile(basedir + File.separator + templateName + suffix);
          if (file.exists()) {
            files.add(file);
          }
        }
      } else {
        files = FileUtils.getFiles(basedir, "**/*" + suffix, null);
      }
      Collections.sort(files);
      getLog().info("Compiling templates...");
      getLog().debug("Options:");
      getLog().debug("  output: " + output);
      getLog().debug("  prefix: " + realPrefix);
      getLog().debug("  suffix: " + suffix);
      getLog().debug("  minimize: " + minimize);
      getLog().debug("  runtime: " + runtime);

      if (!amd) {
        writer.append("(function () {\n");
      }
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
        hash.put("wrapper", amd ? "amd" : "none");
        Options opts = new Options
            .Builder(handlebars, PrecompileHelper.NAME, TagType.VAR, nullContext, template)
                .setHash(hash)
                .build();

        writer.append(PrecompileHelper.INSTANCE.apply(templateName, opts).toString())
            .append("\n\n");
      }
      // extras
      for (CharSequence extra : extras) {
        writer.append(extra).append("\n");
      }
      if (!amd) {
        writer.append("\n})();");
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
    handlebars.registerHelper(HelperRegistry.HELPER_MISSING, (context, options) -> null);
  }

  /**
   * Override i18n helper.
   *
   * @param handlebars The handlebars object.
   */
  private void i18n(final Handlebars handlebars) {
    handlebars.registerHelper(I18nHelper.i18n.name(), new Helper<String>() {
      @Override
      public Object apply(final String context, final Options options) throws IOException {
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
      public Object apply(final String context, final Options options) throws IOException {
        StringBuilder output = new StringBuilder();
        output.append("// i18nJs output:\n");
        output.append("// register an empty i18nJs helper:\n");
        output.append(registerHelper(I18nHelper.i18nJs.name(),
            "I18n.locale = arguments[0] || \"" + Locale.getDefault() + "\";\n"
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
    options.setOutputCharset(Charset.forName(encoding));
    options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.WARNING);
    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

    Compiler.setLoggingLevel(Level.SEVERE);
    Compiler compiler = new Compiler();
    compiler.disableThreads();
    compiler.initOptions(options);

    Result result = compiler.compile(Collections.<SourceFile> emptyList(),
        Arrays.asList(SourceFile.fromFile(output.getAbsolutePath())), options);
    if (result.success) {
      FileUtils.fileWrite(output, compiler.toSource());
    } else {
      List<JSError> errors = result.errors;
      throw new MojoFailureException(errors.get(0).toString());
    }
  }

  /**
   * @param runtime Location of the handlebars.js runtime.
   */
  public void setRuntime(final String runtime) {
    this.runtime = runtime;
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
   * @param amd True, if the output should be in the AMD format. Default is false.
   */
  public void setAmd(final boolean amd) {
    this.amd = amd;
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
   *
   * @param template the template filename
   */
  public void addTemplate(final String template) {
    if (templates == null) {
      this.templates = new ArrayList<String>();
    }
    this.templates.add(template);
  }

  /**
   * Set the handlebars.js location used it to compile/precompile template to JavaScript.
   * <p>
   * Using handlebars.js 2.x:
   * </p>
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v2.0.0.js");
   * </pre>
   * <p>
   * Using handlebars.js 1.x:
   * </p>
   * <pre>
   *   Handlebars handlebars = new Handlebars()
   *      .handlebarsJsFile("handlebars-v1.3.0.js");
   * </pre>
   *
   * Default handlebars.js is <code>handlebars-v4.0.4.js</code>.
   *
   * @param handlebarsJsFile A classpath location of the handlebar.js file.
   */
  public void setHandlebarsJsFile(final String handlebarsJsFile) {
    this.handlebarsJsFile = handlebarsJsFile;
  }

}
