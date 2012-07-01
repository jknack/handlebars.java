package com.github.edgarespina.handlebars.internal;

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
import org.parboiled.annotations.Label;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ActionException;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.matchers.Matcher;
import org.parboiled.matchervisitors.IsSingleCharMatcherVisitor;
import org.parboiled.parserunners.AbstractParseRunner;
import org.parboiled.parserunners.ErrorReportingParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Position;
import org.parboiled.support.StringVar;
import org.parboiled.support.ValueStack;
import org.parboiled.support.Var;

import com.github.edgarespina.handlebars.Handlebars;
import com.github.edgarespina.handlebars.HandlebarsException;
import com.github.edgarespina.handlebars.Template;
import com.github.edgarespina.handlebars.TemplateLoader;
import com.github.edgarespina.handlebars.internal.Variable.Type;

/**
 * The Handlebars parser.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Parser extends BaseParser<BaseTemplate> {

  static class Token {
    public String text;

    public Position position;

    public boolean text(final String text) {
      this.text = text;
      return true;
    }

    public boolean position(final Position position) {
      this.position = position;
      return true;
    }
  }

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

  protected final String filename;

  protected final LinkedList<Stacktrace> stacktraceList;

  protected int noffset = 0;

  Parser(final Handlebars handlebars, final String filename,
      final Map<String, Partial> partials, final String startDelimiter,
      final String endDelimiter, final LinkedList<Stacktrace> stacktrace) {
    this.handlebars = handlebars;
    this.filename =
        handlebars == null ? null : handlebars.getTemplateLoader().resolve(
            filename);
    this.partials =
        partials == null ? new HashMap<String, Partial>() : partials;
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
    this.stacktraceList = stacktrace;
  }

  private static Parser create(final Handlebars handlebars,
      final String filename, final Map<String, Partial> partials,
      final String startDelimiter, final String endDelimiter,
      final LinkedList<Stacktrace> stacktrace) {
    return Parboiled.createParser(Parser.class, handlebars, filename, partials,
        startDelimiter, endDelimiter, stacktrace);
  }

  public static Parser create(final Handlebars handlebars,
      final String filename,
      final String startDelimiter,
      final String endDelimiter) {
    return create(handlebars, filename, null, startDelimiter, endDelimiter,
        new LinkedList<Stacktrace>());
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
        ParseError error = result.parseErrors.get(0);
        String msg =
            ErrorFormatter.printParseError(filename, error, noffset,
                stacktraceList);
        throw new HandlebarsException(msg);
      }
      TemplateList sequence = (TemplateList) result.resultValue;
      removeBlanks(sequence);
      if (sequence.size() == 1) {
        return sequence.iterator().next();
      }
      return sequence;
    } catch (ParserRuntimeException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof HandlebarsException) {
        throw (HandlebarsException) cause;
      }
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
            block(),
            partial(),
            delimiters(),
            comment(),
            variable(),
            space(),
            nl(),
            text())));
  }

  Rule delimiters() {
    final StringVar newstartDelimiter = new StringVar();
    final StringVar newendDelimiter = new StringVar();
    return Sequence(startDelimiter(),
        '=',
        spacing(),
        newDelimiter(), newstartDelimiter.set(match()),
        OneOrMore(spaceNoAction()),
        newDelimiter(), newendDelimiter.set(match()),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            String start = newstartDelimiter.get();
            String end = newendDelimiter.get();
            if (start.length() != end.length()) {
              noffset = end.length();
              throw new ActionException("Unbalanced delimiters: '"
                  + start + "'.length != '" + end
                  + "'.length");
            }
            return true;
          }
        },
        spacing(),
        '=',
        endDelimiter(),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            endDelimiter = newendDelimiter.get();
            startDelimiter = newstartDelimiter.get();
            onlyWhites = false;
            return true;
          }
        });
  }

  @Label("delimiter")
  Rule newDelimiter() {
    return Sequence(delim(), Optional(delim()));
  }

  @Label("delimiter")
  Rule delim() {
    return Sequence(TestNot(AnyOf(" \t\r\n=")), ANY);
  }

  @Label("text")
  Rule text() {
    return Sequence(
        OneOrMore(
            TestNot(startDelimiter()),
            TestNot(spaceNoAction()),
            TestNot(nlNoAction()),
            ANY),
        add(new Text(match())));
  }

  @Label("variable")
  Rule variable() {
    return FirstOf(ampersandVar(), tripleVar(), var());
  }

  @Label("variable")
  Rule ampersandVar() {
    return Sequence(
        startDelimiter(),
        "&",
        spacing(),
        varName(Type.AMPERSAND_VAR),
        spacing(),
        endDelimiter());
  }

  @Label("variable")
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

  @Label("variable")
  Rule var() {
    return Sequence(
        startDelimiter(),
        spacing(),
        varName(Type.VAR),
        spacing(),
        endDelimiter());
  }

  @Label("variable")
  Rule varName(final Type type) {
    final List<Object> params = new ArrayList<Object>();
    final Map<String, Object> hash = new LinkedHashMap<String, Object>();
    final Var<Token> var = new Var<Token>();
    return Sequence(
        var.set(new Token()),
        var.get().position(position()),
        id(),
        var.get().text(match()),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHash(params, hash),
        add(new Variable(handlebars, var.get().text, type, params, hash)
            .filename(filename).position(var.get().position.line,
                var.get().position.column)));
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
            TemplateLoader loader = handlebars.getTemplateLoader();
            if (partial == null) {
              try {
                Position pos = context.getPosition();
                Stacktrace stacktrace =
                    new Stacktrace(pos.line, pos.column, filename);
                stacktraceList.addFirst(stacktrace);
                Reader reader = loader.load(URI.create(uri));
                Parser parser =
                    create(handlebars, uri, partials, startDelimiter,
                        endDelimiter, stacktraceList);
                // Avoid stack overflow exceptions
                partial = new Partial();
                partials.put(uri, partial);
                Template template = parser.parse(reader);
                partial.template(uri, template);
                stacktraceList.removeLast();
              } catch (IOException ex) {
                noffset = uri.length();
                throw new ActionException("The partial '" + loader.resolve(uri)
                    + "' could not be found", ex);
              }
            }
            return add(partial);
          }
        },
        spacing(), endDelimiter());
  }

  @Label("start-block")
  Rule block() throws IOException {
    final Var<Token> name = new Var<Token>();
    final Var<Boolean> inverted = new Var<Boolean>();
    final Var<BaseTemplate> section = new Var<BaseTemplate>();
    List<Object> params = new ArrayList<Object>();
    Map<String, Object> hash = new LinkedHashMap<String, Object>();
    return Sequence(
        reset(params),
        reset(hash),
        name.set(new Token()),
        FirstOf(
            blockStart('#', name, inverted, params, hash),
            blockStart('^', name, inverted, params, hash)
        ),
        section.set(
            new Block(handlebars, name.get().text, inverted.get(), params,
                hash)
                .startDelimiter(startDelimiter)
                .endDelimiter(endDelimiter)
                .position(name.get().position.line, name.get().position.column)
                .filename(filename)
            ),
        add(section.get()),
        body(),
        Optional(
            Sequence(
                startDelimiter(), spacing(), elseSection(), spacing(),
                endDelimiter()),
            body(),
            new Action<BaseTemplate>() {
              @Override
              public boolean run(final Context<BaseTemplate> context) {
                ValueStack<BaseTemplate> stack = context.getValueStack();
                if (stack.size() > 1) {
                  BaseTemplate body = pop();
                  ((Block) section.get()).inverse(body);
                }
                return addToline(section.get());
              }
            }
        ),
        blockEnd(name),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            ValueStack<BaseTemplate> stack = context.getValueStack();
            if (stack.size() > 1) {
              BaseTemplate body = pop();
              ((Block) section.get()).body(body);
            }
            return addToline(section.get());
          }
        }).label("block");
  }

  @Label("else")
  Rule elseSection() {
    return String("else");
  }

  @Label("start-block")
  Rule blockStart(final char type, final Var<Token> name,
      final Var<Boolean> inverted, final List<Object> params,
      final Map<String, Object> hash) {
    return Sequence(
        startDelimiter(), type, inverted.set(matchedChar() == '^'),
        spacing(),
        name.get().position(position()),
        id(), name.get().text(match()),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHash(params, hash),
        endDelimiter());
  }

  @Label("end-block")
  Rule blockEnd(final Var<Token> name) {
    return Sequence(
        startDelimiter(), '/', spacing(),
        id(), new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            String endName = context.getMatch();
            boolean match = name.get().text.equals(endName);
            if (!match) {
              noffset = endName.length();
              throw new ActionException(String.format(
                  "found: '%s', expected: '%s'", endName, name.get().text));
            }
            return match;
          }
        },
        spacing(),
        endDelimiter());
  }

  @Label("parameter::hash")
  Rule paramOrHash(final List<Object> params, final Map<String, Object> hash) {
    final Var<Object> var = new Var<Object>();
    return ZeroOrMore(FirstOf(
        Sequence(hash(hash), spacing()),
        Sequence(param(var), spacing(),
            new Action<BaseTemplate>() {
              @Override
              public boolean run(final Context<BaseTemplate> context) {
                if (!hash.isEmpty()) {
                  noffset = var.get().toString().length();
                  throw new ActionException("'" + var.get()
                      + "' is out of order, a 'hash' was found previously");
                }
                return true;
              }
            }, add(params, var.get()))));
  }

  boolean add(final List<Object> list, final Object value) {
    list.add(value);
    return true;
  }

  @Label("string")
  Rule string(final Var<Object> value) {
    return Sequence(stringLiteral(), value.set(match()));
  }

  @Label("string")
  Rule stringLiteral() {
    return Sequence('"',
        ZeroOrMore(TestNot(AnyOf("\"\r\n")), ANY), '"');
  }

  @Label("parameter::hash")
  Rule hash(final Map<String, Object> hash) {
    final StringVar name = new StringVar();
    final Var<Object> value = new Var<Object>();
    return Sequence(
        id(),
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

  @Label("parameter::hash")
  @MemoMismatches
  Rule param(final Var<Object> value) {
    return FirstOf(
        string(value),
        integer(value),
        bool(value),
        Sequence(id(), value.set(match())));
  }

  @MemoMismatches
  Rule id() {
    return Sequence(TestNot(startDelimiter()), TestNot(elseSection()),
        idStart(), ZeroOrMore(idEnd()));
  }

  @MemoMismatches
  Rule integer(final Var<Object> var) {
    return Sequence(OneOrMore(digit()), var.set(Integer.parseInt(match())));
  }

  @MemoMismatches
  @Label("boolean")
  Rule bool(final Var<Object> var) {
    return Sequence(FirstOf(String("true"), String("false")), var.set(match()));
  }

  @MemoMismatches
  Rule path() {
    return Sequence(
        TestNot(startDelimiter(), endDelimiter()),
        OneOrMore(pathSegment()));
  }

  @Label("ignore")
  Rule spacing() {
    return ZeroOrMore(FirstOf(
        // whitespace
        spaceNoAction(),
        // nl
        nlNoAction(),
        // Comment
        comment()));
  }

  @Label("ignore")
  Rule spaceNoAction() {
    return AnyOf(" \t\f");
  }

  @Label("ignore")
  Rule space() {
    return Sequence(spaceNoAction(), new Action<BaseTemplate>() {
      @Override
      public boolean run(final Context<BaseTemplate> context) {
        return add(new Blank(context.getMatch()));
      }
    });
  }

  @Label("ignore")
  Rule nlNoAction() {
    return Sequence(Optional('\r'), '\n');
  }

  @Label("ignore")
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

  @DontLabel
  Rule comment() {
    return Sequence(startDelimiter(), '!',
        ZeroOrMore(TestNot(endDelimiter()), ANY),
        endDelimiter(), new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            onlyWhites = false;
            return true;
          }
        });
  }

  @MemoMismatches
  @Label("id")
  Rule idStart() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '.', '_', '$', '@');
  }

  @MemoMismatches
  @Label("id")
  Rule idEnd() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), digit(), '_', '$',
        '.', '-', '@');
  }

  @MemoMismatches
  @Label("digit")
  Rule digit() {
    return CharRange('0', '9');
  }

  @MemoMismatches
  @Label("path")
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
