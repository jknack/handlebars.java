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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.I18nHelper;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Convert {@link ResourceBundle} to JavaScript using the I18n.js API.
 *
 * @author edgar.espina
 * @since 1.1.2
 */
@Mojo(name = "i18njs", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class I18nJsPlugin extends HandlebarsPlugin {

  /**
   * The resource bundle name. Example:
   * <ul>
   * <li><code>messages</code></li>
   * <li><code>com.github.app.messages</code></li>
   * </ul>
   * Default is: <code>messages</code>.
   */
  @Parameter
  private String bundle = "messages";

  /**
   * The output directory. JavaScript files will be save here.
   */
  @Parameter(
      defaultValue = "${project.build.directory}/${project.build.finalName}/js")
  private String output;

  /**
   * True if all the messages bundle should be merge into a single file. Default: false.
   */
  @Parameter(defaultValue = "false")
  private boolean merge;

  /**
   * True, if the output should be in the AMD format. Default is anonymous function.
   */
  @Parameter
  private boolean amd;

  /**
   * Character encoding. Default is: UTF-8.
   */
  @Parameter(defaultValue = "UTF-8")
  private String encoding = "UTF-8";

  @Override
  protected void doExecute() throws Exception {
    notNull(bundle, "The bundle's name parameter is required.");
    notNull(output, "The output parameter is required.");

    Handlebars handlebars = new Handlebars();
    handlebars.setCharset(Charset.forName(encoding));
    Context context = Context.newContext(null);
    URL[] classpath = projectClasspath();

    new File(output).mkdirs();

    getLog().info("Converting bundles...");
    getLog().debug("Options:");
    getLog().debug("  output: " + output);
    getLog().debug("  merge: " + merge);
    getLog().debug("  amd: " + amd);
    getLog().debug("  classpath: " + join(classpath, File.pathSeparator));

    StringBuilder buffer = new StringBuilder();

    // 1. find all the bundles
    List<File> bundles = bundles(this.bundle, classpath);

    // 2. hash for i18njs helper
    Map<String, Object> hash = new HashMap<String, Object>();
    hash.put("wrap", false);
    if (classpath.length > 0) {
      hash.put("classLoader", new URLClassLoader(classpath, getClass().getClassLoader()));
    }
    // 3. get the base name from the bundle
    String basename = FileUtils.removePath(this.bundle.replace(".", FileUtils.FS));

    Collections.sort(bundles);
    for (File bundle : bundles) {
      // bundle name
      String bundleName = FileUtils.removeExtension(bundle.getName());
      getLog().debug("converting: " + bundle.getName());
      // extract locale from bundle name
      String locale = bundleName.substring(basename.length());
      if (locale.startsWith("_")) {
        locale = locale.substring(1);
      }
      // set bundle name
      hash.put("bundle", this.bundle);

      Options options = new Options.Builder(handlebars, I18nHelper.i18nJs.name(), TagType.VAR,
          context, Template.EMPTY)
          .setHash(hash)
          .build();
      // convert to JS
      buffer.append(I18nHelper.i18nJs.apply(locale, options));

      if (!merge) {
        FileUtils.fileWrite(new File(output, bundleName + ".js"), encoding,
            wrap(bundleName, buffer, amd));
        buffer.setLength(0);
        getLog().debug("  => " + bundleName + ".js");
      } else {
        buffer.append("\n");
      }
    }
    if (merge && buffer.length() > 0) {
      FileUtils.fileWrite(new File(output, basename + ".js"), wrap(basename, buffer, amd));
      getLog().debug("  =>" + basename + ".js");
    }
  }

  /**
   * Wrap javaScript code using an anonymous function or the AMD format.
   *
   * @param filename The javascript file name.
   * @param body The javascript code.
   * @param amd True, for AMD.
   * @return Some javascript code.
   */
  private String wrap(final String filename, final CharSequence body, final boolean amd) {
    if (amd) {
      return String.format("define('%s', ['i18n'], function (I18n) {\n%s});\n", filename, body);
    }
    return String.format("(function() {\n%s})();\n", body.toString());
  }

  /**
   * List all the resource bundle found in the classpath.
   *
   * @param bundle The bundle's name.
   * @param classpath The project classpath.
   * @return Some resource bundles.
   * @throws Exception If something goes wrong.
   */
  private List<File> bundles(final String bundle, final URL[] classpath) throws Exception {
    Set<File> bundles = new LinkedHashSet<File>();
    for (URL url : classpath) {
      File dir = new File(url.toURI());
      bundles.addAll(FileUtils.getFiles(dir, bundle.replace(".", FileUtils.FS) + "*.properties",
          null));
    }
    return new ArrayList<File>(bundles);
  }

  /**
   * Set the resource bundle name. Example:
   * <ul>
   * <li><code>messages</code></li>
   * <li><code>com.github.app.messages</code></li>
   * </ul>
   * Default is: <code>messages</code>.
   *
   * @param bundle The bundle name.
   */
  public void setBundle(final String bundle) {
    this.bundle = bundle;
  }

  /**
   * Set output directory. The generate javascript bundles will be saved here.
   *
   * @param output The output directory.
   */
  public void setOutput(final String output) {
    this.output = output;
  }

  /**
   * If true, all the bundles will be merge them into a single file. Default: false.
   *
   * @param merge True for merging bundles.
   */
  public void setMerge(final boolean merge) {
    this.merge = merge;
  }

  /**
   * True, if the output should be in the AMD format. Default is anonymous function.
   *
   * @param amd True, if the output should be in the AMD format. Default is anonymous function.
   */
  public void setAmd(final boolean amd) {
    this.amd = amd;
  }
}
