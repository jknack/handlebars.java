/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.io.FileTemplateLoader;

/**
 * A handlebars web server.
 *
 * @author edgar.espina
 */
public class HbsServer {

  /**
   * The logging system.
   */
  public static final Logger logger = LoggerFactory.getLogger(HbsServer.class);

  public static String version;

  static {
    InputStream in = null;
    try {
      in = HbsServer.class.getResourceAsStream("/hbs.properties");
      Properties properties = new Properties();
      properties.load(in);
      version = properties.getProperty("version");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(in);
    }

  }

  /**
   * A command line option
   *
   * @author edgar.espina
   */
  public static class Option {

    /**
     * The option's name.
     */
    private String name;

    /**
     * The default value.
     */
    private String defaultValue;

    /**
     * The option's value.
     */
    private String value;

    /**
     * A description.
     */
    private String description;

    /**
     * Creates a new option.
     *
     * @param name The option's name.
     * @param description The option's description.
     * @param defaultValue The option's default value.
     */
    public Option(final String name, final String description,
        final String defaultValue) {
      this.name = name;
      this.description = description;
      this.defaultValue = defaultValue;
    }

    /**
     * The option's value.
     *
     * @return The option's value.
     */
    public String getValue() {
      return StringUtils.trim(value == null ? defaultValue : value);
    }

    @Override
    public String toString() {
      return name + ": " + description;
    }
  }

  /**
   * The command line options.
   */
  private static Map<String, Option> options =
      new LinkedHashMap<String, Option>() {
        /**
         * The generated UID.
         */
        private static final long serialVersionUID = -8537770378811876135L;

        {
          put("-dir", new Option("-dir", "set the template directory", null));
          put("-suffix", new Option("-suffix",
              "set the template's suffix, default is .hbs", ".hbs"));
          put("-prefix", new Option("-prefix",
              "set the template's prefix, default is /", "/"));
          put("-context", new Option("-context",
              "set the context's path, default is /", "/"));
          put("-port", new Option("-port", "set port number, default is 6780",
              "6780"));
          put("-content-type", new Option("-content-type",
              "set the content-type header, default is text/html", "text/html"));
        }
      };

  /**
   * Parse the command line argument as a map of options. This method has side
   * effects, it change the default and static {@link #options} variable.
   *
   * @param args The command line arguments.
   * @return A option map.
   */
  public static Map<String, Option> parse(final String[] args) {
    for (int i = 0; i < args.length; i += 2) {
      String opt = args[i];
      Option option = options.get(opt);
      if (option == null) {
        System.out.println("Unknown parameter: " + opt);
        usage(options);
      }
      try {
        String value = args[i + 1];
        option.value = value;
      } catch (ArrayIndexOutOfBoundsException ex) {
        System.out.println("Missing value for parameter: " + opt);
        usage(options);
      }
    }
    for (Entry<String, Option> entry : options.entrySet()) {
      Option option = entry.getValue();
      if (option.getValue() == null) {
        System.out.println("Missing value for parameter: " + entry.getKey());
        usage(options);
      }
    }
    return options;
  }

  /**
   * Start a Handlebars server.
   *
   * @param args The command line arguments.
   * @throws Exception If something goes wrong.
   */
  public static void main(final String[] args) throws Exception {
    Map<String, Option> options = parse(args);
    String dir = options.get("-dir").getValue();
    if (!new File(dir).exists()) {
      System.out.println("File not found: " + dir);
      usage(options);
    }
    logger.info("Welcome to the Handlebars.java server");
    final int port = Integer.parseInt(options.get("-port").getValue());
    final String prefix = options.get("-prefix").getValue();
    final String suffix = options.get("-suffix").getValue();
    final String contextPath = options.get("-context").getValue();

    TemplateLoader loader = new FileTemplateLoader(new File(dir));
    loader.setPrefix(new File(dir, prefix).getAbsolutePath());
    loader.setSuffix(suffix);
    Handlebars handlebars = new Handlebars(loader);
    handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
    handlebars.registerHelper("md", new MarkdownHelper());
    // String helpers
    StringHelpers.register(handlebars);
    // Humanize helpers
    HumanizeHelper.register(handlebars);

    final Server server = new Server(port);
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        logger.info("Hope you enjoy it! bye!");
        try {
          server.stop();
        } catch (Exception ex) {
          logger.info("Enable to stop server", ex);
        }
      }
    }));

    server.addLifeCycleListener(new AbstractLifeCycleListener() {
      @Override
      public void lifeCycleStarted(final LifeCycle event) {
        logger.info("Open a browser and type:");
        logger.info("  http://localhost:{}{}/[page]{}", new Object[] {port,
            contextPath.equals("/") ? "" : contextPath, suffix });
      }
    });

    WebAppContext root = new WebAppContext();
    root.setContextPath(contextPath);
    root.setResourceBase(dir);
    root.addServlet(new ServletHolder(new HbsServlet(handlebars, options)),
        "*" + suffix);

    root.setParentLoaderPriority(true);

    // prevent jetty from loading the webapp web.xml
    root.setConfigurations(new Configuration[] { new WebXmlConfiguration() {
        @Override
        protected Resource findWebXml(WebAppContext context)
                throws IOException, MalformedURLException {
            return null;
        }
    } });

    server.setHandler(root);

    server.start();
    server.join();
  }

  /**
   * print a usage message.
   *
   * @param options The handlebars server options.
   */
  private static void usage(final Map<String, Option> options) {
    System.out.println("Usage:");
    System.out
        .println("  java -jar handlebars-proto-*.jar -dir value [-option " +
            "value]");
    System.out.println("    options:");
    for (Entry<String, Option> entry : options.entrySet()) {
      System.out.println("      " + entry.getValue());
    }
    System.exit(0);
  }

}
