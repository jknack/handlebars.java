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
package com.github.jknack.handlebars.server;

import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
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
      Template template = handlebars.compile(removeExtension(requestURI(request)));

      Object model = model(request);

      String output = template.apply(model);
      response.setCharacterEncoding(args.encoding);
      response.setContentType(args.contentType);
      writer = response.getWriter();
      writer.write(output);
    } catch (HandlebarsException ex) {
      handlebarsError(ex, response);
    } catch (JsonParseException ex) {
      logger.error("Unexpected error", ex);
      jsonError(ex, request, response);
    } catch (FileNotFoundException ex) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
    String jsonFilename = jsonFilename(request);
    String ymlFilename = ymlFilename(request);

    Object data = json(jsonFilename);
    if (data == null) {
      data = yml(ymlFilename);
    }
    if (data == null) {
      String errorMessage = "file not found: {}";
      logger.error(errorMessage, jsonFilename);
      logger.error(errorMessage, ymlFilename);
      return Collections.emptyMap();
    }
    return data;
  }

  /**
   * Determines the data file to use from the requested URI or from the 'data'
   * HTTP parameter.
   *
   * @param request The current request.
   * @return The data file to use from the requested URI or from the 'data'
   *         HTTP parameter.
   */
  private String dataFile(final HttpServletRequest request) {
    String data = request.getParameter("data");
    String uri = StringUtils.isEmpty(data)
        ? request.getRequestURI().replace(request.getContextPath(), "")
        : data;
    if (!HbsServer.CONTEXT.equals(args.prefix)) {
      uri = args.prefix + uri;
    }
    if (!uri.startsWith("/")) {
      uri = "/" + uri;
    }
    return uri;
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
    int firstLine = 1;
    if (error != null) {
      if (ex.getCause() != null) {
        firstLine = error.line;
      } else {
        firstLine = Math.max(1, error.line - 1);
      }
    }
    fancyError(ex, firstLine, "Xml", response);
  }

  /**
   * Deal with a {@link HandlebarsException}.
   *
   * @param ex The handlebars exception.
   * @param request The http request.
   * @param response The http response.
   * @throws IOException If something goes wrong.
   */
  private void jsonError(final JsonParseException ex, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    Map<String, Object> root = new HashMap<String, Object>();
    Map<String, Object> error = new HashMap<String, Object>();
    String filename = jsonFilename(request);
    JsonLocation location = ex.getLocation();
    String reason = ex.getMessage();
    int atIdx = reason.lastIndexOf(" at ");
    if (atIdx > 0) {
      reason = reason.substring(0, atIdx);
    }
    error.put("filename", filename);
    error.put("line", location.getLineNr());
    error.put("column", location.getColumnNr());
    error.put("reason", reason);
    error.put("type", "JSON error");
    String json = read(filename);
    StringBuilder evidence = new StringBuilder();
    int i = (int) location.getCharOffset();
    int nl = 0;
    while (i >= 0 && nl < 2) {
      char ch = json.charAt(i);
      if (ch == '\n') {
        nl++;
      }
      evidence.insert(0, ch);
      i--;
    }
    i = (int) location.getCharOffset() + 1;
    nl = 0;
    while (i < json.length() && nl < 2) {
      char ch = json.charAt(i);
      if (ch == '\n') {
        nl++;
      }
      evidence.append(ch);
      i++;
    }
    error.put("evidence", evidence);

    root.put("error", error);
    int firstLine = Math.max(1, ex.getLocation().getLineNr() - 1);
    fancyError(root, firstLine, "JScript", response);
  }

  /**
   * Deal with a fancy errors.
   *
   * @param error An error.
   * @param firstLine The first line to report.
   * @param lang The lang to use.
   * @param response The http response.
   * @throws IOException If something goes wrong.
   */
  private void fancyError(final Object error, final int firstLine, final String lang,
      final HttpServletResponse response) throws IOException {

    Handlebars handlebars = new Handlebars();
    StringHelpers.register(handlebars);

    Template template = handlebars.compile("/error-pages/error");

    PrintWriter writer = null;
    writer = response.getWriter();
    template.apply(
        Context
            .newBuilder(error)
            .resolver(MapValueResolver.INSTANCE, FieldValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE)
            .combine("lang", lang)
            .combine("version", HbsServer.version)
            .combine("firstLine", firstLine).build()
        , writer);

    IOUtils.closeQuietly(writer);
  }

  /**
   * Try to load a <code>json</code> file that matches the given request.
   *
   * @return The associated data.
   * @throws IOException If the file isn't found.
   * @param filename the filename to read
   */
  private Object json(final String filename) throws IOException {
    try {
      String json = read(filename);
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
   * @return A yaml map.
   * @throws IOException If the file isn't found.
   * @param filename the filename to read
   */
  private Object yml(final String filename) throws IOException {
    try {
      String yml = read(filename);
      Object data = yaml.load(yml);
      return data;
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  /**
   * Construct the filename to parse json data from.
   * @param request the current request
   * @return filename to load json from
   */
  private String jsonFilename(final HttpServletRequest request) {
    return dataFilename(request, ".json");
  }

  /**
   * Construct the filename to parse yml data from.
   * @param request the current request
   * @return filename to load yml from
   */
  private String ymlFilename(final HttpServletRequest request) {
    return dataFilename(request, ".yml");
  }

  /**
   * Construct the filename to parse data from.
   * @param request the current request
   * @param extension the file extension to use, e.g. ".json"
   * @return filename to load data from
   */
  private String dataFilename(final HttpServletRequest request, final String extension) {
    return removeExtension(dataFile(request)) + extension;
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
      input = getServletContext().getResourceAsStream(uri);
      if (input == null) {
        throw new FileNotFoundException(args.dir + uri);
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
