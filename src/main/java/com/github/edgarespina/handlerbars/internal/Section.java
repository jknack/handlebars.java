package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.BuiltInHelpers;
import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

/**
 * Sections render blocks of text one or more times, depending on the value of
 * the key in the current context.
 * A section begins with a pound and ends with a slash. That is, {{#person}}
 * begins a "person" section while {{/person}} ends it.
 * The behavior of the section is determined by the value of the key.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
class Section extends HelperResolver {

  /**
   * The body template.
   */
  private BaseTemplate body;

  /**
   * The section's name.
   */
  private final String name;

  /**
   * True if it's inverted.
   */
  private final boolean inverted;

  /**
   * Section's description '#' or '^'.
   */
  private final String type;

  /**
   * The start delimiter.
   */
  private String startDelimiter;

  /**
   * The end delimiter.
   */
  private String endDelimiter;

  /**
   * Inverse section for if/else clauses.
   */
  private BaseTemplate inverse;

  /**
   * Creates a new {@link Section}.
   *
   * @param handlebars The handlebars object.
   * @param name The section's name.
   * @param inverted True if it's inverted.
   * @param params The parameter list.
   * @param hash The hash.
   */
  public Section(final Handlebars handlebars, final String name,
      final boolean inverted, final List<Object> params,
      final Map<String, Object> hash) {
    super(handlebars);
    this.name = checkNotNull(name, "Section's name is required.");
    this.inverted = inverted;
    this.type = inverted ? "^" : "#";
    params(params);
    hash(hash);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void merge(final Context scope,
      final Writer writer) throws IOException {
    Helper<Object> helper = helper(name);
    BaseTemplate template = body;
    Object context;
    Context currentScope = scope;
    if (helper == null) {
      context = transform(scope.get(name));
      if (inverted) {
        helper = BuiltInHelpers.UNLESS;
      } else if (context instanceof Iterable) {
        helper = BuiltInHelpers.EACH;
      } else if (context instanceof Boolean) {
        helper = BuiltInHelpers.IF;
      } else if (context instanceof Lambda) {
        helper = BuiltInHelpers.WITH;
        template = Lambdas
            .compile(handlebars,
                (Lambda<Object, Object>) context,
                scope, template,
                startDelimiter, endDelimiter);
      } else {
        helper = BuiltInHelpers.WITH;
        currentScope = DefaultContext.wrap(scope, context);
      }
    } else {
      context = determineContext(scope);
    }
    DefaultOptions options =
        new DefaultOptions(handlebars, template, inverse, currentScope,
            params(currentScope), hash(scope));
    CharSequence result = helper.apply(context, options);
    if (result != null) {
      writer.append(result);
    }
    options.destroy();
  }

  /**
   * The section's name.
   *
   * @return The section's name.
   */
  public String name() {
    return name;
  }

  /**
   * True if it's an inverted section.
   *
   * @return True if it's an inverted section.
   */
  public boolean inverted() {
    return inverted;
  }

  @Override
  public boolean remove(final Template child) {
    return body.remove(child);
  }

  /**
   * Set the template body.
   *
   * @param body The template body. Required.
   * @return This section.
   */
  public Section body(final BaseTemplate body) {
    this.body = checkNotNull(body, "The template's body is required.");
    return this;
  }

  /**
   * Set the inverse template.
   *
   * @param inverse The inverse template. Required.
   * @return This section.
   */
  public Template inverse(final BaseTemplate inverse) {
    this.inverse =
        checkNotNull(inverse, "The inverse template's body is required.");
    return this;
  }

  /**
   * The inverse template for else clauses.
   *
   * @return The inverse template for else clauses.
   */
  public Template inverse() {
    return inverse;
  }

  /**
   * Set the end delimiter.
   *
   * @param endDelimiter The end delimiter.
   * @return This section.
   */
  public Section endDelimiter(final String endDelimiter) {
    this.endDelimiter = endDelimiter;
    return this;
  }

  /**
   * Set the start delimiter.
   *
   * @param startDelimiter The start delimiter.
   * @return This section.
   */
  public Section startDelimiter(final String startDelimiter) {
    this.startDelimiter = startDelimiter;
    return this;
  }

  /**
   * The template's body.
   *
   * @return The template's body.
   */
  public Template body() {
    return body;
  }

  @Override
  public String text() {
    String content = body == null ? "" : body.toString();
    return "{{" + type + name + "}}" + content + "{{/" + name + "}}";
  }

  /**
   * The start delimiter.
   *
   * @return The start delimiter.
   */
  public String startDelimiter() {
    return startDelimiter;
  }

  /**
   * The end delimiter.
   *
   * @return The end delimiter.
   */
  public String endDelimiter() {
    return endDelimiter;
  }

}
