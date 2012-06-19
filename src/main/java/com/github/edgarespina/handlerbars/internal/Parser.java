package com.github.edgarespina.handlerbars.internal;

import static org.parboiled.common.Preconditions.checkArgNotNull;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ActionException;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.IsSingleCharMatcherVisitor;
import org.parboiled.parserunners.AbstractParseRunner;
import org.parboiled.parserunners.ErrorReportingParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringVar;
import org.parboiled.support.ValueStack;
import org.parboiled.support.Var;

import com.github.edgarespina.handlerbars.Handlebars;
import com.github.edgarespina.handlerbars.HandlebarsException;
import com.github.edgarespina.handlerbars.ResourceLocator;
import com.github.edgarespina.handlerbars.Template;
import com.github.edgarespina.handlerbars.internal.Variable.Type;

/**
 * The template parser.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Parser extends BaseParser<BaseTemplate> {

  /**
   * Fix a NPE when asking for a matcher.
   *
   * @author edgar.espina
   * @since 0.1.0
   */
  private static class SafeReportingParseRunner extends
      ReportingParseRunner<BaseTemplate> {
    public SafeReportingParseRunner(final Rule rule) {
      super(rule);
    }

    @Override
    protected ParsingResult<BaseTemplate> runReportingMatch(
        final InputBuffer inputBuffer, final int errorIndex) {
      ParseRunner<BaseTemplate> reportingRunner =
          new SafeErrorReportingParseRunner(
              getRootMatcher(), errorIndex)
              .withParseErrors(getParseErrors())
              .withValueStack(getValueStack());
      return reportingRunner.run(inputBuffer);
    }
  }

  /**
   * Same as {@link ErrorReportingParseRunner} but deal with NPE at error
   * reporting.
   */
  private static class SafeErrorReportingParseRunner extends
      AbstractParseRunner<BaseTemplate> implements MatchHandler {
    private final IsSingleCharMatcherVisitor isSingleCharMatcherVisitor =
        new IsSingleCharMatcherVisitor();
    private final int errorIndex;
    private final MatchHandler inner;
    private final List<MatcherPath> failedMatchers =
        new ArrayList<MatcherPath>();
    private boolean seeking;

    /**
     * Creates a new ErrorReportingParseRunner instance for the given rule and
     * the given errorIndex.
     *
     * @param rule the parser rule
     * @param errorIndex the index of the error to report
     */
    public SafeErrorReportingParseRunner(final Rule rule, final int errorIndex) {
      this(rule, errorIndex, null);
    }

    /**
     * Creates a new ErrorReportingParseRunner instance for the given rule and
     * the given errorIndex.
     * The given MatchHandler is used as a delegate for the actual match
     * handling.
     *
     * @param rule the parser rule
     * @param errorIndex the index of the error to report
     * @param inner another MatchHandler to delegate the actual match handling
     *        to, can be null
     */
    public SafeErrorReportingParseRunner(final Rule rule, final int errorIndex,
        final MatchHandler inner) {
      super(rule);
      this.errorIndex = errorIndex;
      this.inner = inner;
    }

    @Override
    public ParsingResult<BaseTemplate> run(final InputBuffer inputBuffer) {
      checkArgNotNull(inputBuffer, "inputBuffer");
      resetValueStack();
      failedMatchers.clear();
      seeking = errorIndex > 0;

      // run without fast string matching to properly get to the error location
      MatcherContext<BaseTemplate> rootContext =
          createRootContext(inputBuffer, this, false);
      boolean matched = match(rootContext);
      if (!matched) {
        getParseErrors()
            .add(
                new InvalidInputError(inputBuffer, errorIndex, failedMatchers,
                    null));
      }
      return createParsingResult(matched, rootContext);
    }

    @Override
    public boolean match(final MatcherContext<?> context) {
      boolean matched =
          inner == null && context.getMatcher().match(context) || inner != null
              && inner.match(context);
      if (context.getCurrentIndex() == errorIndex) {
        if (matched && seeking) {
          seeking = false;
        }
        Matcher matcher = context.getMatcher();
        if (!matched && !seeking && matcher != null
            && matcher.accept(isSingleCharMatcherVisitor)) {
          failedMatchers.add(context.getPath());
        }
      }
      return matched;
    }
  }

  protected String startDelimiter;

  protected String endDelimiter;

  protected final List<BaseTemplate> line = new LinkedList<BaseTemplate>();

  protected final List<BaseTemplate> ignored = new LinkedList<BaseTemplate>();

  protected boolean onlyWhites = true;

  protected final Handlebars handlebars;

  protected final Map<String, Partial> partials;

  Parser(final Handlebars handlebars,
      final Map<String, Partial> partials, final String startDelimiter,
      final String endDelimiter) {
    this.handlebars = handlebars;
    this.partials =
        partials == null ? new HashMap<String, Partial>() : partials;
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
  }

  private static Parser create(final Handlebars handlebars,
      final Map<String, Partial> partials, final String startDelimiter,
      final String endDelimiter) {
    return Parboiled.createParser(Parser.class, handlebars, partials,
        startDelimiter, endDelimiter);
  }

  public static Parser create(final Handlebars handlebars,
      final String startDelimiter,
      final String endDelimiter) {
    return create(handlebars, null, startDelimiter, endDelimiter);
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
    try {
      ParseRunner<BaseTemplate> runner =
          new SafeReportingParseRunner(template());
      ParsingResult<BaseTemplate> result = runner.run(input);
      if (result.hasErrors()) {
        System.out.println(ErrorFormatter.printParseError(
            result.parseErrors.get(0)));
        throw new HandlebarsException(ErrorFormatter.printParseError(
            result.parseErrors.get(0)));
      }
      TemplateList sequence = (TemplateList) result.resultValue;
      removeBlanks(sequence);
      if (sequence.size() == 1) {
        return sequence.iterator().next();
      }
      return sequence;
    } catch (ParserRuntimeException ex) {
      HandlebarsException hex = new HandlebarsException(ex.getMessage());
      hex.initCause(ex.getCause() == null ? ex : ex.getCause());
      throw hex;
    }
  }

  Rule template() throws IOException {
    return Sequence(body(), sync(), EOI);
  }

  Rule body() throws IOException {
    return Sequence(
        push(new TemplateList()),
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
    final StringVar newstartDelimiter = new StringVar();
    final StringVar newendDelimiter = new StringVar();
    return Sequence(startDelimiter(),
        '=',
        spacing(),
        newDelimiter(), newstartDelimiter.set(match()),
        OneOrMore(spaceNoAction()),
        newDelimiter(), newendDelimiter.set(match()),
        spacing(),
        '=',
        endDelimiter(),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            endDelimiter = newendDelimiter.get();
            startDelimiter = newstartDelimiter.get();
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

  @DontLabel
  Rule text() {
    return Sequence(
        OneOrMore(
            TestNot(startDelimiter()),
            TestNot(spaceNoAction()),
            TestNot(nlNoAction()),
            ANY),
        add(new Text(match())));
  }

  Rule variable() {
    return FirstOf(ampersandVar(), tripleVar(), var());
  }

  Rule ampersandVar() {
    return Sequence(
        startDelimiter(),
        "&",
        spacing(),
        varName(Type.AMPERSAND_VAR),
        spacing(),
        endDelimiter());
  }

  Rule tripleVar() {
    return Sequence(
        startDelimiter(),
        '{',
        spacing(),
        varName(Type.TRIPLE_VAR),
        spacing(),
        '}',
        endDelimiter());
  }

  Rule var() {
    return Sequence(
        startDelimiter(),
        spacing(),
        varName(Type.VAR),
        spacing(),
        endDelimiter());
  }

  Rule varName(final Type type) {
    final List<Object> params = new ArrayList<Object>();
    final Map<String, Object> hash = new LinkedHashMap<String, Object>();
    final StringVar var = new StringVar();
    return Sequence(identifier(),
        var.set(match()),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHash(params, hash),
        add(new Variable(handlebars, var.get(), type, params, hash)));
  }

  boolean reset(final List<Object> list) {
    list.clear();
    return true;
  }

  boolean reset(final Map<String, Object> map) {
    map.clear();
    return true;
  }

  boolean add(final BaseTemplate template) {
    TemplateList sequence = (TemplateList) peek();
    sequence.add(template);
    addToline(template);
    return true;
  }

  boolean addToline(final BaseTemplate template) {
    line.add(template);
    onlyWhites = onlyWhites && template instanceof Blank;
    return true;
  }

  Action<BaseTemplate> startDelimiter() {
    return new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        Matcher matcher = (Matcher) String(startDelimiter);
        return matcher.match((MatcherContext<BaseTemplate>) context);
      }
    };
  }

  Action<BaseTemplate> endDelimiter() {
    return new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        Matcher matcher = (Matcher) String(endDelimiter);
        return matcher.match((MatcherContext<BaseTemplate>) context);
      }
    };
  }

  Rule partial() throws IOException {
    final StringVar uriVar = new StringVar();
    return Sequence(startDelimiter(), '>', spacing(), path(),
        uriVar.set(match()),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            String uri = uriVar.get();
            Partial partial = partials.get(uri);
            if (partial == null) {
              try {
                ResourceLocator<?> locator = handlebars.getResourceLocator();
                Reader reader = locator.locate(URI.create(uri));
                Parser parser =
                    create(handlebars, partials, startDelimiter, endDelimiter);
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
        spacing(), endDelimiter());
  }

  Rule section() throws IOException {
    final StringVar name = new StringVar();
    final Var<Boolean> inverted = new Var<Boolean>();
    final Var<Section> section = new Var<Section>();
    List<Object> params = new ArrayList<Object>();
    Map<String, Object> hash = new LinkedHashMap<String, Object>();
    return Sequence(
        reset(params),
        reset(hash),
        FirstOf(
            sectionStart('#', name, inverted, params, hash),
            sectionStart('^', name, inverted, params, hash)
        ),
        section.set(
            new Section(handlebars, name.get(), inverted.get(), params, hash)
                .startDelimiter(startDelimiter).endDelimiter(endDelimiter)),
        add(section.get()),
        body(),
        Optional(
            Sequence(
                startDelimiter(), spacing(), elseSection(), spacing(), endDelimiter()),
            body(),
            new Action<BaseTemplate>() {
              @Override
              public boolean run(final Context<BaseTemplate> context) {
                ValueStack<BaseTemplate> stack = context.getValueStack();
                if (stack.size() > 1) {
                  BaseTemplate body = pop();
                  section.get().inverse(body);
                }
                return addToline(section.get());
              }
            }
        ),
        sectionEnd(name),
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
        }).label("section");
  }

  Rule elseSection() {
    return String("else");
  }

  Rule sectionStart(final char type, final StringVar name,
      final Var<Boolean> inverted, final List<Object> params,
      final Map<String, Object> hash) {
    return Sequence(
        startDelimiter(), type, inverted.set(matchedChar() == '^'),
        spacing(),
        identifier(), name.set(match()),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHash(params, hash),
        endDelimiter());
  }

  @DontLabel
  Rule sectionEnd(final StringVar name) {
    return Sequence(
        startDelimiter(), '/', spacing(),
        identifier(), new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            String endName = context.getMatch();
            boolean match = name.get().equals(endName);
            if (!match) {
              throw new ActionException(String.format(
                  "found: '%s', expected: '%s'", endName, name.get()));
            }
            return match;
          }
        },
        spacing(),
        endDelimiter());
  }

  @DontLabel
  Rule paramOrHash(final List<Object> params, final Map<String, Object> hash) {
    final Var<Object> var = new Var<Object>();
    return ZeroOrMore(FirstOf(
        Sequence(hash(hash), spacing()),
        Sequence(param(var), spacing(),
            new Action<BaseTemplate>() {
              @Override
              public boolean run(final Context<BaseTemplate> context) {
                if (!hash.isEmpty()) {
                  throw new ActionException("parameter out of order: '"
                      + var.get() + "'");
                }
                return true;
              }
            }, add(params, var.get()))));
  }

  boolean add(final List<Object> list, final Object value) {
    list.add(value);
    return true;
  }

  Rule string(final Var<Object> value) {
    return Sequence(stringLiteral(), value.set(match()));
  }

  @DontLabel
  Rule stringLiteral() {
    return Sequence('"',
        ZeroOrMore(TestNot('"'), ANY), '"');
  }

  Rule hash(final Map<String, Object> hash) {
    final StringVar name = new StringVar();
    final Var<Object> value = new Var<Object>();
    return Sequence(
        identifier(),
        name.set(match()),
        spacing(),
        '=',
        spacing(),
        Sequence(param(value), add(hash, name.get(), value.get())));
  }

  boolean add(final Map<String, Object> hash, final String name,
      final Object value) {
    hash.put(name, value);
    return true;
  }

  @DontLabel
  @MemoMismatches
  Rule param(final Var<Object> value) {
    return FirstOf(
        string(value),
        integer(value),
        bool(value),
        Sequence(identifier(), value.set(match())));
  }

  @MemoMismatches
  Rule identifier() {
    return Sequence(TestNot(startDelimiter()), TestNot(elseSection()),
        idStart(), ZeroOrMore(idEnd()));
  }

  @MemoMismatches
  Rule integer(final Var<Object> var) {
    return Sequence(OneOrMore(digit()), var.set(Integer.parseInt(match())));
  }

  @MemoMismatches
  Rule bool(final Var<Object> var) {
    return Sequence(FirstOf(String("true"), String("false")), var.set(match()));
  }

  @MemoMismatches
  Rule path() {
    return Sequence(
        TestNot(startDelimiter(), endDelimiter()),
        OneOrMore(pathSegment()));
  }

  @DontLabel
  Rule spacing() {
    return ZeroOrMore(FirstOf(
        // whitespace
        spaceNoAction(),
        // nl
        nlNoAction(),
        // Comment
        comment()));
  }

  @DontLabel
  Rule spaceNoAction() {
    return AnyOf(" \t\f");
  }

  @DontLabel
  Rule space() {
    return Sequence(spaceNoAction(), new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        return add(new Blank(context.getMatch()));
      }
    });
  }

  @DontLabel
  Rule nlNoAction() {
    return Sequence(Optional('\r'), '\n');
  }

  @DontLabel
  Rule nl() {
    return Sequence(nlNoAction(), new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        return add(new Blank(context.getMatch()));
      }
    }, sync());
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
            ignored.add(child);
          }
        }
      }
    }
    onlyWhites = true;
    currentLine.clear();
    return true;
  }

  void removeBlanks(final BaseTemplate head) {
    for (BaseTemplate blank : ignored) {
      head.remove(blank);
    }
    line.clear();
    ignored.clear();
  }

  Rule comment() {
    return Sequence(startDelimiter(), '!', ZeroOrMore(TestNot(endDelimiter()), ANY),
        endDelimiter(), new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            onlyWhites = false;
            return true;
          }
        });
  }

  @DontLabel
  Rule idStart() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '.', '_', '$');
  }

  @DontLabel
  @MemoMismatches
  Rule idEnd() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), digit(), '_', '$',
        '.', '-');
  }

  @DontLabel
  @MemoMismatches
  Rule digit() {
    return CharRange('0', '9');
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
