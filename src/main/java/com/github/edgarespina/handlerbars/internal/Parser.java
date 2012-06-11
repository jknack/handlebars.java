package com.github.edgarespina.handlerbars.internal;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.MatcherContext;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.matchers.Matcher;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringVar;
import org.parboiled.support.ValueStack;
import org.parboiled.support.Var;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Template;

public class Parser extends BaseParser<BaseTemplate> {

  protected String delimStart;

  protected String delimEnd;

  protected final List<BaseTemplate> line = new LinkedList<BaseTemplate>();

  protected final List<BaseTemplate> blanks = new LinkedList<BaseTemplate>();

  protected boolean onlyWhites = true;

  protected final Handlebars handlebars;

  protected final Map<String, Partial> partials;

  Parser(final Handlebars handlebars,
      final Map<String, Partial> partials, final String delimStart,
      final String delimEnd) {
    this.handlebars = handlebars;
    this.partials =
        partials == null ? new HashMap<String, Partial>() : partials;
    this.delimStart = delimStart;
    this.delimEnd = delimEnd;
  }

  public static Parser create(final Handlebars handlebars,
      final Map<String, Partial> partials, final String delimStart,
      final String delimEnd) {
    return Parboiled.createParser(Parser.class, handlebars, partials,
        delimStart, delimEnd);
  }

  public static void initialize() {
    create(null, null, null, null);
  }

  public Template parse(final Reader reader) throws IOException {
    try {
      return parse(toString(reader));
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          throw new IllegalStateException("Cannot close the input reader", ex);
        }
      }
    }
  }

  public Template parse(final String input) throws IOException {
    ParseRunner<Template> runner =
        new ReportingParseRunner<Template>(template());
    ParsingResult<Template> result = runner.run(input);
    if (result.hasErrors()) {
      throw new HandlebarsException(ErrorUtils.printParseErrors(result)
          .trim());
    }
    Sequence sequence = (Sequence) result.resultValue;
    if (sequence.size() == 1) {
      return sequence.iterator().next();
    }
    return sequence;
  }

  Rule template() throws IOException {
    return Sequence(body(), sync(), removeBlanks(), EOI);
  }

  Rule body() throws IOException {
    return Sequence(
        push(new Sequence()),
        ZeroOrMore(
        FirstOf(
            section(),
            partial(),
            setDelimiters(),
            comment(),
            variable(),
            space(),
            nl(),
            text())));
  }

  Rule setDelimiters() {
    final StringVar newDelimStart = new StringVar();
    final StringVar newDelimEnd = new StringVar();
    return Sequence(delimStart(),
        '=',
        spacing(),
        newDelimiter(), newDelimStart.set(match()),
        OneOrMore(spaceNoAction()),
        newDelimiter(), newDelimEnd.set(match()),
        spacing(),
        '=',
        delimEnd(),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            delimEnd = newDelimEnd.get();
            delimStart = newDelimStart.get();
            onlyWhites = false;
            return true;
          }
        });
  }

  Rule newDelimiter() {
    return Sequence(delim(), Optional(delim()));
  }

  Rule delim() {
    return Sequence(TestNot(AnyOf(" \t\r\n=")), ANY);
  }

  Rule text() {
    return Sequence(
        OneOrMore(
            TestNot(delimStart()),
            TestNot(spaceNoAction()),
            TestNot(nlNoAction()),
            ANY),
        add(new Text(match())));
  }

  Rule variable() {
    return FirstOf(ampersandVar(), tripleVar(), var());
  }

  boolean add(final BaseTemplate template) {
    Sequence sequence = (Sequence) peek();
    sequence.add(template);
    addToline(template);
    return true;
  }

  boolean addToline(final BaseTemplate template) {
    line.add(template);
    onlyWhites = onlyWhites && template instanceof Blank;
    return true;
  }

  Rule ampersandVar() {
    return Sequence(
        delimStart(),
        "&",
        spacing(),
        identifier(),
        add(new Variable(handlebars, match(), false)),
        spacing(),
        delimEnd());
  }

  Rule tripleVar() {
    return Sequence(
        delimStart(),
        '{',
        spacing(),
        identifier(),
        add(new Variable(handlebars, match(), false)),
        spacing(),
        '}',
        delimEnd());
  }

  Rule var() {
    return Sequence(
        delimStart(),
        spacing(),
        identifier(),
        add(new Variable(handlebars, match(), true)),
        spacing(),
        delimEnd());
  }

  Action<BaseTemplate> delimStart() {
    return new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        Matcher matcher = (Matcher) String(delimStart);
        return matcher.match((MatcherContext<BaseTemplate>) context);
      }
    };
  }

  Action<BaseTemplate> delimEnd() {
    return new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        Matcher matcher = (Matcher) String(delimEnd);
        return matcher.match((MatcherContext<BaseTemplate>) context);
      }
    };
  }

  Rule partial() throws IOException {
    final StringVar uriVar = new StringVar();
    return Sequence(delimStart(), '>', spacing(), path(),
        uriVar.set(match()),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            String uri = uriVar.get();
            Partial partial = partials.get(uri);
            if (partial == null) {
              try {
                ResourceLocator locator = handlebars.getResourceLocator();
                Reader reader = locator.locate(URI.create(uri));
                Parser parser =
                    create(handlebars, partials, delimStart, delimEnd);
                // Avoid stack overflow exceptions
                partial = new Partial();
                partials.put(uri, partial);
                Template template = parser.parse(reader);
                partial.template(template);
              } catch (IOException ex) {
                throw new HandlebarsException("Unable to read: " + uri, ex);
              }
            }
            return add(partial);
          }
        },
        spacing(), delimEnd());
  }

  Rule section() throws IOException {
    final StringVar name = new StringVar();
    final Var<Boolean> inverted = new Var<Boolean>();
    final Var<Section> section = new Var<Section>();
    return Sequence(
        FirstOf(
            sectionStart('#', name, inverted),
            sectionStart('^', name, inverted)),
        section.set(new Section(handlebars, name.get(), inverted.get())
            .delimStart(delimStart).delimEnd(delimEnd)),
        add(section.get()),
        body(),
        sectionEnd(),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            ValueStack<BaseTemplate> stack = context.getValueStack();
            if (stack.size() > 1) {
              BaseTemplate body = pop();
              section.get().body(body);
            }
            return addToline(section.get());
          }
        });
  }

  Rule sectionStart(final char type, final StringVar name,
      final Var<Boolean> inverted) {
    return Sequence(
        delimStart(), type, inverted.set(matchedChar() == '^'),
        spacing(),
        identifier(), name.set(match()),
        spacing(),
        delimEnd());
  }

  Rule sectionEnd() {
    return Sequence(delimStart(), '/', spacing(), identifier(), spacing(),
        delimEnd());
  }

  @MemoMismatches
  Rule identifier() {
    return Sequence(TestNot(delimStart()), letter(),
        ZeroOrMore(letterOrDigit()));
  }

  @MemoMismatches
  Rule path() {
    return Sequence(
        TestNot(delimStart(), delimEnd()),
        OneOrMore(pathSegment()));
  }

  Rule spacing() {
    return ZeroOrMore(FirstOf(
        // whitespace
        spaceNoAction(),
        // nl
        nlNoAction(),
        // Comment
        comment()));
  }

  Rule spaceNoAction() {
    return AnyOf(" \t\f");
  }

  Rule space() {
    return Sequence(spaceNoAction(), add(new Blank(match())));
  }

  Rule nlNoAction() {
    return FirstOf(String("\r\n"), String("\n"));
  }

  Rule nl() {
    return Sequence(nlNoAction(), add(new Blank(match())), sync());
  }

  boolean sync() {
    List<BaseTemplate> currentLine = this.line;
    if (!onlyWhites) {
      boolean ignore = true;
      for (BaseTemplate template : currentLine) {
        Class<? extends BaseTemplate> type = template.getClass();
        if (type == Text.class || type == Variable.class
            || type == Partial.class) {
          ignore = false;
          break;
        }
      }

      if (ignore) {
        for (BaseTemplate child : currentLine) {
          if (child instanceof Blank) {
            blanks.add(child);
          }
        }
      }
    }
    onlyWhites = true;
    currentLine.clear();
    return true;
  }

  boolean removeBlanks() {
    BaseTemplate head = peek();
    for (BaseTemplate blank : blanks) {
      head.remove(blank);
    }
    line.clear();
    blanks.clear();
    return true;
  }

  Rule comment() {
    return Sequence(delimStart(), '!', ZeroOrMore(TestNot(delimEnd()), ANY),
        delimEnd(), new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            onlyWhites = false;
            return true;
          }
        });
  }

  // JLS defines letters and digits as Unicode characters recognized
  // as such by special Java procedures.
  Rule letter() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '.', '_', '$');
  }

  @MemoMismatches
  Rule letterOrDigit() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0',
        '9'), '_', '$', '.');
  }

  Rule pathSegment() {
    return FirstOf(CharRange('0', '9'), CharRange('a', 'z'),
        CharRange('A', 'Z'), '_', '$', '/', '.', '-');
  }

  static String toString(final Reader reader)
      throws IOException {
    StringBuilder buffer = new StringBuilder(1024 * 4);
    int ch;
    while ((ch = reader.read()) != -1) {
      buffer.append((char) ch);
    }
    buffer.trimToSize();
    return buffer.toString();
  }
}
