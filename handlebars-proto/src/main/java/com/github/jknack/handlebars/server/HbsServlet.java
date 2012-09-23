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
package com.github.jknack.handlebars.server;

import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.StringHelpers;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.server.HbsServer.Options;

/**
 * Prepare, compile and merge handlebars templates.
 *
 * @author edgar.espina
 */
public class HbsServlet extends HttpServlet {

  /**
   * The default serial uid.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The logging system.
   */
  private static final Logger logger =
      LoggerFactory.getLogger(HbsServlet.class);

  /**
   * The handlebars object.
   */
  private final Handlebars handlebars;

  /**
   * The object mapper.
   */
  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * A yaml parser.
   */
  private final Yaml yaml = new Yaml();

  /**
   * The server options.
   */
  private final Options args;

  /**
   * Creates a new {@link HbsServlet}.
   *
   * @param handlebars The handlebars object.
   * @param args The server options.
   */
  public HbsServlet(final Handlebars handlebars, final Options args) {
    this.handlebars = handlebars;
    this.args = args;

    mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(Feature.ALLOW_COMMENTS, true);
  }

  @Override
  protected void doGet(final HttpServletRequest request,
      final HttpServletResponse response)
      throws ServletException, IOException {
    Writer writer = null;

    try {
      Template template =
          handlebars.compile(
              URI.create(removeExtension(requestURI(request))));

      Object model = model(request);

      writer = response.getWriter();
      String output = template.apply(model);
      writer.write(output);
      response.setContentType(args.contentType);
    } catch (HandlebarsException ex) {
      handlebarsError(ex, response);
    } catch (FileNotFoundException ex) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND,
          "NOT FOUND: " + ex.getMessage());
    } catch (IOException ex) {
      logger.error("Unexpected error", ex);
      throw ex;
    } catch (RuntimeException ex) {
      logger.error("Unexpected error", ex);
      throw ex;
    } catch (Exception ex) {
      logger.error("Unexpected error", ex);
      throw new ServletException(ex);
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  /**
   * Attempt to load a json or yml file.
   *
   * @param request The original request.
   * @return The associated model.
   * @throws IOException If something goes wrong.
   */
  private Object model(final HttpServletRequest request) throws IOException {
    Object data = json(request);
    if (data == null) {
      data = yml(request);
    }
    return data == null ? Collections.emptyMap() : data;
  }

  /**
   * Remove context path from the request's URI.
   *
   * @param request The current request.
   * @return Same as {@link HttpServletRequest#getRequestURI()} without context
   *         path.
   */
  private String requestURI(final HttpServletRequest request) {
    String requestURI =
        request.getRequestURI().replace(request.getContextPath(), "");
    return requestURI;
  }

  /**
   * Deal with a {@link HandlebarsException}.
   *
   * @param ex The handlebars exception.
   * @param response The http response.
   * @throws IOException If something goes wrong.
   */
  private void handlebarsError(final HandlebarsException ex,
      final HttpServletResponse response) throws IOException {

    HandlebarsError error = ex.getError();
    Handlebars handlebars = new Handlebars();
    StringHelpers.register(handlebars);

    Template template =
        handlebars.compile(URI.create("/error-pages/error"));

    int firstLine = 1;
    if (error != null) {
      if (ex.getCause() != null) {
        firstLine = error.line;
      } else {
        firstLine = Math.max(1, error.line - 1);
      }
    }
    PrintWriter writer = null;
    writer = response.getWriter();
    template.apply(
        Context
            .newBuilder(ex)
            .resolver(MapValueResolver.INSTANCE, FieldValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE)
            .combine("lang", "Xml")
            .combine("version", HbsServer.version)
            .combine("firstLine", firstLine).build()
        , writer);

    IOUtils.closeQuietly(writer);
  }

  /**
   * Try to load a <code>json</code> file that matches the given request.
   *
   * @param request The requested object.
   * @return The associated data.
   * @throws IOException If the file isn't found.
   */
  private Object json(final HttpServletRequest request) throws IOException {
    try {
      String json = read(removeExtension(requestURI(request)) + ".json");
      if (json.trim().startsWith("[")) {
        return mapper.readValue(json, List.class);
      }
      return mapper.readValue(json, Map.class);
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Try to load a <code>yml</code> file that matches the given request.
   *
   * @param request The requested object.
   * @return A yaml map.
   * @throws IOException If the file isn't found.
   */
  private Object yml(final HttpServletRequest request) throws IOException {
    try {
      String yml = read(removeExtension(requestURI(request)) + ".yml");
      Object data = yaml.load(yml);
      return data;
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Read a file from the servlet context.
   *
   * @param uri The requested file.
   * @return The string content.
   * @throws IOException If the file is not found.
   */
  private String read(final String uri) throws IOException {
    InputStream input = null;
    try {
      String absURI = uri;
      if (!HbsServer.CONTEXT.equals(args.prefix)) {
        absURI = args.prefix + absURI;
      }
      if (!absURI.startsWith("/")) {
        absURI = "/" + absURI;
      }
      input = getServletContext().getResourceAsStream(absURI);
      if (input == null) {
        throw new FileNotFoundException(args.dir + absURI);
      }
      return IOUtils.toString(input);
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req,
      final HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

}
