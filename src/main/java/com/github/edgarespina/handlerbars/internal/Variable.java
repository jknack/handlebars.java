package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

/**
 * The most basic tag type is the variable. A {{name}} tag in a basic template
 * will try to find the name key in the current context. If there is no name
 * key, nothing will be rendered.
 * All variables are HTML escaped by default. If you want to return unescaped
 * HTML, use the triple mustache: {{{name}}}.
 * You can also use & to unescape a variable: {{& name}}. This may be useful
 * when changing delimiters.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Variable extends HelperResolver {

  /**
   * The variable's type.
   *
   * @author edgar.espina
   * @since 0.1.0
   */
  enum Type {
    /**
     * Dont escape a variable.
     */
    TRIPLE_VAR {
      @Override
      public String start() {
        return "{{{";
      }

      @Override
      public String end() {
        return "}}}";
      }

      @Override
      public boolean escape() {
        return false;
      }
    },

    /**
     * Dont escape a variable.
     */
    AMPERSAND_VAR {
      @Override
      public String start() {
        return "{{&";
      }

      @Override
      public boolean escape() {
        return false;
      }
    },

    /**
     * Escape a variable.
     */
    VAR {
      @Override
      public boolean escape() {
        return true;
      }
    };

    /**
     * The start delimiter.
     *
     * @return The start delimiter.
     */
    public String start() {
      return "{{";
    }

    /**
     * The end delimiter.
     *
     * @return The end delimiter.
     */
    public String end() {
      return "}}";
    }

    /**
     * Return true if the value must be escaped.
     *
     * @return True if the value must be escaped.
     */
    public abstract boolean escape();

    /**
     * Format the variable's name.
     *
     * @param name The variable's name.
     * @return The variable's name with the start and end delimiters.
     */
    public String format(final String name) {
      StringBuilder buffer = new StringBuilder();
      return buffer.append(start()).append(name).append(end()).toString();
    }
  }

  /**
   * The variable's name. Required.
   */
  private final String name;

  /**
   * The variable's type. Required.
   */
  private final Type type;

  /**
   * Default value for a variable. If set, no lookup is executed. Optional.
   */
  private final Object constant;

  /**
   * Creates a new {@link Variable}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param type The variable's type. Required.
   * @param params The variable's parameters. Required.
   * @param hash The variable's hash. Required.
   */
  public Variable(final Handlebars handlebars, final String name,
      final Type type, final List<Object> params,
      final Map<String, Object> hash) {
    this(handlebars, name, null, type, params, hash);
  }

  /**
   * Creates a new {@link Variable}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param value The variable's value. Optional.
   * @param type The variable's type. Required.
   * @param params The variable's parameters. Required.
   * @param hash The variable's hash. Required.
   */
  public Variable(final Handlebars handlebars, final String name,
      final Object value, final Type type, final List<Object> params,
      final Map<String, Object> hash) {
    super(handlebars);
    this.name = name.trim();
    this.constant = value;
    this.type = type;
    params(params);
    hash(hash);
  }

  /**
   * Creates a new {@link Variable}.
   *
   * @param handlebars The handlebars instance.
   * @param name The variable's name. Required.
   * @param value The variable's value. Optional.
   * @param type The variable's type. Required.
   */
  @SuppressWarnings("unchecked")
  public Variable(final Handlebars handlebars, final String name,
      final Object value, final Type type) {
    this(handlebars, name, value, type, Collections.EMPTY_LIST,
        Collections.EMPTY_MAP);
  }

  /**
   * The variable's name.
   *
   * @return The variable's name.
   */
  public String name() {
    return name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void doApply(final Context scope, final Writer writer)
      throws IOException {
    Helper<Object> helper = helper(name);
    if (helper != null) {
      Object context = determineContext(scope);
      DefaultOptions options =
          new DefaultOptions(this, null, scope, params(scope), hash(scope));
      CharSequence result = helper.apply(context, options);
      if (escape(result)) {
        writer.append(Handlebars.Utils.escapeExpression(result));
      } else {
        writer.append(result);
      }
    } else {
      Object value = this.constant == null ? scope.get(name) : this.constant;
      if (value != null) {
        if (value instanceof Lambda) {
          value =
              Lambdas.merge(handlebars, (Lambda<Object, Object>) value, scope,
                  this);
        }
        String stringValue = value.toString();
        // TODO: Add formatter hook
        if (escape(value)) {
          writer.append(Handlebars.Utils.escapeExpression(stringValue));
        } else {
          // DON'T escape none String values.
          writer.append(stringValue);
        }
      }
    }
  }

  /**
   * True if the given value should be escaped.
   *
   * @param value The variable's value.
   * @return True if the given value should be escaped.
   */
  private boolean escape(final Object value) {
    if (value instanceof Handlebars.SafeString) {
      return false;
    }
    boolean isString =
        value instanceof CharSequence || value instanceof Character;
    if (isString) {
      return type.escape();
    } else {
      return false;
    }
  }

  @Override
  public boolean remove(final Template child) {
    return false;
  }

  @Override
  public String rawText() {
    return type.format(name);
  }
}
