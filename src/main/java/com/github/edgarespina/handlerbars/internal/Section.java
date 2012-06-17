package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.github.edgarespina.handlerbars.BuiltInHelpers;
import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.Helper;
import com.github.edgarespina.handlerbars.Lambda;
import com.github.edgarespina.handlerbars.Template;

class Section extends HelperResolver {
  private BaseTemplate body;

  private final String name;

  private final boolean inverted;

  private String type;

  private String delimStart;

  private String delimEnd;

  private BaseTemplate inverse;

  public Section(final Handlebars handlebars, final String name,
      final boolean inverted, final List<Object> params,
      final Map<String, Object> hash) {
    super(handlebars);
    this.name = name;
    this.inverted = inverted;
    this.type = inverted ? "^" : "#";
    params(params);
    hash(hash);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void apply(final Scope scope,
      final Writer writer) throws IOException {
    Helper<Object> helper = helper(name);
    BaseTemplate template = body;
    Object context;
    Scope currentScope = scope;
    if (helper == null) {
      context = transform(scope.get(name));
      if (context instanceof DelimAware) {
        ((DelimAware) context).setDelimiters(delimStart, delimEnd);
      }
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
                (Lambda<Object>) context,
                scope,
                template,
                delimStart,
                delimEnd);
      } else {
        helper = BuiltInHelpers.WITH;
        currentScope = Scopes.scope(scope, context);
      }
    } else {
      context = param(scope, 0);
    }
    DefaultOptions options =
        new DefaultOptions(template, inverse, currentScope,
            params(currentScope), hash(scope));
    CharSequence result = helper.apply(context, options);
    writer.append(result);
    options.destroy();
  }

  public String name() {
    return name;
  }

  public boolean inverted() {
    return inverted;
  }

  @Override
  public boolean remove(final Template child) {
    return body.remove(child);
  }

  public Section body(final BaseTemplate body) {
    this.body = body;
    return this;
  }

  public Template inverse(final BaseTemplate inverse) {
    this.inverse = inverse;
    return this;
  }

  public Template inverse() {
    return inverse;
  }

  public Section delimEnd(final String delimEnd) {
    this.delimEnd = delimEnd;
    return this;
  }

  public Section delimStart(final String delimStart) {
    this.delimStart = delimStart;
    return this;
  }

  public Template body() {
    return body;
  }

  @Override
  public String text() {
    String content = body == null ? "" : body.toString();
    return "{{" + type + name + "}}" + content + "{{/" + name + "}}";
  }

  public String delimStart() {
    return delimStart;
  }

  public String delimEnd() {
    return delimEnd;
  }

}
