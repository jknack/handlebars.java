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
package com.github.jknack.handlebars.internal;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.parboiled.common.Preconditions.checkArgNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.MatchHandler;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.Label;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.buffers.DefaultInputBuffer;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.IntArrayStack;
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

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TemplateLoader;
import com.github.jknack.handlebars.internal.Variable.Type;

/**
 * The Handlebars parser.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class Parser extends BaseParser<Object> {

  static class PartialInputBuffer {

    public static InputBuffer build(final String input, final String indent) {
      if (isEmpty(indent) || !isEmpty(indent.trim())) {
        return new DefaultInputBuffer(input.toCharArray());
      }
      StringBuilder buffer = new StringBuilder(input.length() + indent.length());
      buffer.append(indent);
      int len = input.length();
      for (int idx = 0; idx < len; idx++) {
        char ch = input.charAt(idx);
        buffer.append(ch);
        if (ch == '\n' && idx < len - 1) {
          buffer.append(indent);
        }
      }
      return new DefaultInputBuffer(buffer.toString().toCharArray());
    }

  }

  static class Node {
    public TemplateList sequence = new TemplateList();

    public IntArrayStack spaces = new IntArrayStack();
  }

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
      ReportingParseRunner<Object> {
    public SafeReportingParseRunner(final Rule rule) {
      super(rule);
    }

    @Override
    protected ParsingResult<Object> runReportingMatch(
        final InputBuffer inputBuffer, final int errorIndex) {
      ParseRunner<Object> reportingRunner =
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
      AbstractParseRunner<Object> implements MatchHandler {
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
    public ParsingResult<Object> run(final InputBuffer inputBuffer) {
      checkArgNotNull(inputBuffer, "inputBuffer");
      resetValueStack();
      failedMatchers.clear();
      seeking = errorIndex > 0;

      // run without fast string matching to properly get to the error location
      MatcherContext<Object> rootContext =
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

  private String startDelimiterBack;

  private String endDelimiterBack;

  protected Handlebars handlebars;

  protected Map<String, Partial> partials;

  protected String filename;

  protected Boolean hasTag;

  protected StringBuilder line = new StringBuilder();

  protected Set<Node> nodeLine = new LinkedHashSet<Parser.Node>();

  protected LinkedList<Stacktrace> stacktraceList;

  protected int noffset = 0;

  private boolean rootParser;

  Parser(final Handlebars handlebars, final String filename,
      final Map<String, Partial> partials, final String startDelimiter,
      final String endDelimiter, final LinkedList<Stacktrace> stacktrace) {
    this.handlebars = handlebars;
    this.filename =
        handlebars == null ? null : handlebars.getTemplateLoader().resolve(
            filename);
    this.partials =
        partials == null ? new HashMap<String, Partial>() : partials;
    rootParser = partials == null;
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
    startDelimiterBack = startDelimiter;
    endDelimiterBack = endDelimiter;
    stacktraceList = stacktrace;
  }

  public Template parse(final String input) throws IOException {
    return parse(new DefaultInputBuffer(input.toCharArray()));
  }

  public Template parse(final InputBuffer input) throws IOException {
    try {
      ParseRunner<Object> runner =
          new SafeReportingParseRunner(template()) {
            @Override
            protected void resetValueStack() {
              startDelimiter = startDelimiterBack;
              endDelimiter = endDelimiterBack;
              hasTag = null;
              line.setLength(0);
              noffset = 0;
              if (rootParser) {
                partials.clear();
                stacktraceList.clear();
              }
              super.resetValueStack();
            }
          };
      ParsingResult<Object> result = runner.run(input);
      if (result.hasErrors()) {
        ParseError error = result.parseErrors.get(0);
        Collections.reverse(stacktraceList);
        HandlebarsError hbsError =
            ErrorFormatter.printParseError(filename, error, noffset,
                stacktraceList);
        throw new HandlebarsException(hbsError);
      }
      Node node = (Node) result.resultValue;
      TemplateList sequence = node.sequence;
      stripSpace(node);
      if (sequence.size() == 1) {
        return sequence.iterator().next();
      }
      return sequence;
    } catch (ParserRuntimeException ex) {
      Throwable cause = ex.getCause() == null ? ex : ex.getCause();
      if (cause instanceof HandlebarsException) {
        throw (HandlebarsException) cause;
      }
      HandlebarsException hex =
          new HandlebarsException(cause.getMessage(), cause);
      hex.setStackTrace(ex.getStackTrace());
      throw hex;
    } finally {
      endDelimiter = null;
      endDelimiterBack = null;
      filename = null;
      handlebars = null;
      hasTag = null;
      line = null;
      nodeLine.clear();
      nodeLine = null;
      if (rootParser) {
        partials.clear();
        stacktraceList.clear();
      }
      partials = null;
      stacktraceList = null;
      startDelimiter = null;
    }
  }

  Rule template() throws IOException {
    return Sequence(body(), EOI);
  }

  Rule body() throws IOException {
    return Sequence(
        push(new Node()),
        ZeroOrMore(
        FirstOf(
            space(),
            nl(),
            text(),
            Sequence(startDelimiter(),
                FirstOf(
                    // {{# }}
                    Sequence('#', spacing(), block(false)),
                    // {{^ }}
                    Sequence('^', spacing(), block(true)),
                    // {{> }}
                    Sequence('>', spacing(), partial()),
                    // {{& }}
                    Sequence('&', spacing(), ampersandVar()),
                    // {{{ }}}
                    Sequence('{', spacing(), tripleVar()),
                    // {{= }}
                    Sequence('=', hasTag(true), spacing(), delimiters()),
                    // {{! }}
                    comment(),
                    Sequence(spacing(), var())
                ))
        )));
  }

  boolean hasTag() {
    return hasTag == null ? false : hasTag.booleanValue();
  }

  boolean hasTag(final boolean hasTag) {
    if (this.hasTag != Boolean.FALSE) {
      this.hasTag = hasTag;
    }
    return true;
  }

  void resetHasTag() {
    hasTag = null;
  }

  Rule delimiters() {
    final StringVar newstartDelimiter = new StringVar();
    final StringVar newendDelimiter = new StringVar();
    return Sequence(
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
            if (startDelimiter.length() != endDelimiter.length()) {
              noffset = endDelimiter.length();
              throw new ActionException("unbalanced delimiters: '"
                  + startDelimiter + "'.length != '" + endDelimiter
                  + "'.length");
            }
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
            TestNot(nlNoAction()),
            ANY.label("text")),
        add(new Text(match())));
  }

  @Label("variable")
  Rule ampersandVar() {
    return Sequence(
        varName(Type.AMPERSAND_VAR),
        spacing(),
        endDelimiter());
  }

  @Label("variable")
  Rule tripleVar() {
    return Sequence(
        varName(Type.TRIPLE_VAR),
        spacing(),
        '}',
        endDelimiter());
  }

  @Label("variable")
  Rule var() {
    return Sequence(
        varName(Type.VAR),
        spacing(),
        endDelimiter());
  }

  @Label("variable")
  Rule varName(final Type type) {
    final List<Object> params = new ArrayList<Object>();
    final Map<String, Object> hash = new LinkedHashMap<String, Object>();
    final Var<Token> var = new Var<Token>();
    final Var<Integer> varIdx = new Var<Integer>();
    return Sequence(
        var.set(new Token()),
        var.get().position(position()),
        varIdx.set(currentIndex()),
        qualifiedId(),
        var.get().text(match()),
        hasTag(false),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHashList(params, hash),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            String helperName = var.get().text;
            Helper<Object> helper = handlebars.helper(helperName);
            if (helper == null && (params.size() > 0 || hash.size() > 0)) {
              Helper<Object> helperMissing =
                  handlebars.helper(Handlebars.HELPER_MISSING);
              if (helperMissing == null) {
                noffset = currentIndex() - varIdx.get();
                throw new ActionException("could not find helper: '" + helperName + "'");
              }
            }
            return true;
          }
        },
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

  TemplateList templateList() {
    return peekNode().sequence;
  }

  Node peekNode() {
    return (Node) peek();
  }

  Node popNode() {
    Node node = (Node) pop();
    nodeLine.add(node);
    return node;
  }

  boolean add(final BaseTemplate template) {
    TemplateList sequence = templateList();
    template.filename(filename);
    sequence.add(template);
    return true;
  }

  boolean add(final Text template) {
    Node node = peekNode();
    node.spaces.push(node.sequence.size());
    line.append(template.text());
    return add((BaseTemplate) template);
  }

  Action<Object> startDelimiter() {
    return new Action<Object>() {
      @Override
      public boolean run(final Context<Object> context) {
        Matcher matcher = (Matcher) String(startDelimiter);
        return matcher.match((MatcherContext<Object>) context);
      }
    };
  }

  Action<Object> endDelimiter() {
    return new Action<Object>() {
      @Override
      public boolean run(final Context<Object> context) {
        Matcher matcher = (Matcher) String(endDelimiter);
        return matcher.match((MatcherContext<Object>) context);
      }
    };
  }

  boolean isInStack(final List<Stacktrace> stacktrace, final String filename) {
    for (Stacktrace st : stacktrace) {
      if (st.getFilename().equals(filename)) {
        return true;
      }
    }
    return this.filename.equals(filename);
  }

  Rule partial() throws IOException {
    final StringVar uriVar = new StringVar();
    final StringVar partialContext = new StringVar();
    return Sequence(
        path(),
        hasTag(true),
        uriVar.set(match()),
        spacing(),
        Optional(Sequence(qualifiedId(), partialContext.set(match()))),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            String uri = uriVar.get();
            if (uri.startsWith("[") && uri.endsWith("]")) {
              uri = uri.substring(1, uri.length() - 1);
            }
            TemplateLoader loader = handlebars.getTemplateLoader();
            if (uri.startsWith("/")) {
              noffset = uri.length();
              throw new ActionException(
                  "found: '/', partial shouldn't start with '/'");
            }
            String partialPath = loader.resolve(uri);
            if (!handlebars.allowInfiniteLoops() && isInStack(stacktraceList, partialPath)) {
              noffset = uri.length();
              throw new ActionException("an infinite loop was detected, partial '" + partialPath
                  + "' was loaded previously");
            }
            Partial partial = partials.get(partialPath);
            if (partial == null) {
              try {
                Position pos = context.getPosition();
                Stacktrace stacktrace =
                    new Stacktrace(pos.line, pos.column, uri, filename);
                stacktraceList.addLast(stacktrace);
                String input = loader.loadAsString(URI.create(uri));
                Parser parser =
                    ParserFactory.create(handlebars, uri, partials,
                        startDelimiter, endDelimiter, stacktraceList);
                // Avoid stack overflow exceptions
                partial = new Partial();
                partials.put(partialPath, partial);
                Template template = parser.parse(PartialInputBuffer.build(input,
                    hasTag ? line.toString() : null));
                partial.template(uri, template, partialContext.get());
                stacktraceList.removeLast();
              } catch (IOException ex) {
                noffset = uri.length();
                throw new ActionException("The partial '" + partialPath
                    + "' could not be found", ex);
              }
            }
            return add(partial);
          }
        },
        spacing(), endDelimiter());
  }

  @Label("start-block")
  Rule block(final boolean inverted) throws IOException {
    final Var<Token> name = new Var<Token>();
    final Var<BaseTemplate> section = new Var<BaseTemplate>();
    List<Object> params = new ArrayList<Object>();
    Map<String, Object> hash = new LinkedHashMap<String, Object>();
    return Sequence(
        reset(params),
        reset(hash),
        name.set(new Token()),
        blockStart(name, params, hash),
        section.set(
            new Block(handlebars, name.get().text, inverted, params, hash)
                .startDelimiter(startDelimiter)
                .endDelimiter(endDelimiter)
                .position(name.get().position.line, name.get().position.column)
                .filename(filename)
            ),
        add(section.get()),
        body(),
        Optional(
            Sequence(startDelimiter(), spacing(), elseKey(), spacing(),
                endDelimiter()),
            body(),
            new Action<Object>() {
              @Override
              public boolean run(final Context<Object> context) {
                ValueStack<Object> stack = context.getValueStack();
                if (stack.size() > 1) {
                  BaseTemplate body = popNode().sequence;
                  ((Block) section.get()).inverse(body);
                }
                return true;
              }
            }
        ),
        blockEnd(name),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            ValueStack<Object> stack = context.getValueStack();
            if (stack.size() > 1) {
              BaseTemplate body = popNode().sequence;
              ((Block) section.get()).body(body);
            }
            return true;
          }
        }).label("block");
  }

  @Label("else")
  Rule elseKey() {
    return FirstOf("else", "^");
  }

  @Label("start-block")
  Rule blockStart(final Var<Token> name, final List<Object> params,
      final Map<String, Object> hash) {
    final Var<Integer> nameIdx = new Var<Integer>();
    return Sequence(
        name.get().position(position()),
        nameIdx.set(currentIndex()),
        qualifiedId(), name.get().text(match()),
        hasTag(true),
        spacing(),
        reset(params),
        reset(hash),
        paramOrHashList(params, hash),
        spacing(),
        endDelimiter(),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            String helperName = name.get().text;
            Helper<Object> helper = handlebars.helper(helperName);
            if (helper == null && (params.size() > 0 || hash.size() > 0)) {
              Helper<Object> helperMissing =
                  handlebars.helper(Handlebars.HELPER_MISSING);
              if (helperMissing == null) {
                noffset = currentIndex() - nameIdx.get();
                throw new ActionException("could not find helper: '" + helperName + "'");
              }
            }
            return true;
          }
        });
  }

  @Label("end-block")
  Rule blockEnd(final Var<Token> name) {
    return Sequence(
        startDelimiter(), '/', spacing(),
        qualifiedId(), new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
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
        endDelimiter(), hasTag(true));
  }

  @Label("parameter::hash")
  Rule paramOrHashList(final List<Object> params, final Map<String, Object> hash) {
    return Optional(paramOrHash(params, hash),
        ZeroOrMore(OneOrMore(spaces()), paramOrHash(params, hash)));
  }

  @Label("parameter::hash")
  Rule paramOrHash(final List<Object> params, final Map<String, Object> hash) {
    final Var<Object> var = new Var<Object>();
    return FirstOf(hash(hash), Sequence(param(var),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            if (!hash.isEmpty()) {
              noffset = var.get().toString().length();
              Entry<String, Object> firstHash = hash.entrySet().iterator().next();
              throw new ActionException("parameter is out of order, "
                  + "a '" + firstHash.getKey() + "=" + firstHash.getValue()
                  + "' was found previously");
            }
            return true;
          }
        }, add(params, var.get())));
  }

  boolean add(final List<Object> list, final Object value) {
    list.add(value);
    return true;
  }

  @Label("string")
  Rule stringLiteral(final Var<Object> value) {
    return Sequence(doubleQuotedString(), value.set(match().replace("\\\"", "\"")));
  }

  @Label("string")
  Rule singleStringLiteral(final Var<Object> value) {
    return Sequence(singleQuotedString(), value.set(match().replace("\\\'", "\'")));
  }

  @Label("string")
  Rule doubleQuotedString() {
    return Sequence('"',
        ZeroOrMore(
        FirstOf(
            String("\\\""),
            Sequence(TestNot(AnyOf("\"\r\n")), ANY.label("text")))),
        '"');
  }

  @Label("string")
  Rule singleQuotedString() {
    return Sequence("'",
        ZeroOrMore(
        FirstOf(
            String("\\\'"),
            Sequence(TestNot(AnyOf("'\r\n")), ANY.label("text")))),
        "'");
  }

  @Label("parameter::hash")
  Rule hash(final Map<String, Object> hash) {
    final StringVar name = new StringVar();
    final Var<Object> value = new Var<Object>();
    return Sequence(
        qualifiedId(),
        name.set(match()),
        spacing(),
        '=',
        spacing(),
        Sequence(hashValue(value), add(hash, name.get(), value.get())));
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
        stringLiteral(value),
        integer(value),
        bool(value),
        Sequence(qualifiedId(), value.set(match())));
  }

  @Label("parameter::hash")
  @MemoMismatches
  Rule hashValue(final Var<Object> value) {
    return FirstOf(
        stringLiteral(value),
        singleStringLiteral(value),
        integer(value),
        bool(value),
        Sequence(qualifiedId(), value.set(match())));
  }

  @MemoMismatches
  @Label("id")
  Rule qualifiedId() {
    return FirstOf(
        // ../id
        Sequence(dot(), dot(), '/', qualifiedId()),
        dot(),
        Sequence(id(), ZeroOrMore(FirstOf(dot(), '/'), id())));
  }

  @MemoMismatches
  @Label("id")
  Rule id() {
    return FirstOf(bracketId(), simpleId());
  }

  @MemoMismatches
  @Label("id")
  Rule bracketId() {
    return Sequence('[', simpleId(), ']');
  }

  @MemoMismatches
  @Label("id")
  Rule simpleId() {
    return Sequence(TestNot(startDelimiter()), TestNot(endDelimiter()), TestNot(elseKey()),
        Sequence(nameStart(), ZeroOrMore(idSuffix())));
  }

  @MemoMismatches
  @Label("id")
  Rule idSuffix() {
    return FirstOf(propertyAccess(), nameEnd());
  }

  @MemoMismatches
  Rule propertyAccess() {
    return Sequence(dot(), "[", spacing(), idx(), spacing(), "]");
  }

  @MemoMismatches
  Rule idx() {
    return OneOrMore(TestNot("]"), TestNot(startDelimiter()), TestNot(endDelimiter()), ANY);
  }

  @MemoMismatches
  @Label("id")
  Rule nameStart() {
    return Sequence(TestNot(dot()),
        FirstOf(
            CharRange('a', 'z'),
            CharRange('A', 'Z'),
            '_', '$', '@'));
  }

  @MemoMismatches
  @Label("id")
  Rule nameEnd() {
    return Sequence(TestNot(dot()), TestNot(endDelimiter()),
        FirstOf(
            CharRange('a', 'z'),
            CharRange('A', 'Z'),
            digit(),
            '_', '$', '/',
            '-', '@'));
  }

  @MemoMismatches
  @Label(".")
  Rule dot() {
    return Ch('.');
  }

  @MemoMismatches
  Rule integer(final Var<Object> var) {
    return Sequence(OneOrMore(digit()), var.set(Integer.parseInt(match())));
  }

  @MemoMismatches
  @Label("boolean")
  Rule bool(final Var<Object> var) {
    return Sequence(FirstOf(String("true"), String("false")),
        var.set(Boolean.valueOf(match())));
  }

  @MemoMismatches
  Rule path() {
    return Sequence(
        TestNot(startDelimiter()), TestNot(endDelimiter()),
        FirstOf(
            Sequence('[', OneOrMore(pathSegment()), ']'),
            OneOrMore(pathSegment())));
  }

  @Label("ignore")
  Rule spacing() {
    return ZeroOrMore(spaces());
  }

  @Label("ignore")
  Rule spaces() {
    return FirstOf(
        // whitespace
        spaceNoAction(),
        // nl
        nlNoAction(),
        // Comment
        comment());
  }

  @Label("ignore")
  Rule spaceNoAction() {
    return AnyOf(" \t\f");
  }

  @Label("ignore")
  Rule space() {
    return Sequence(OneOrMore(spaceNoAction()), new Action<Object>() {
      @Override
      public boolean run(final Context<Object> context) {
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
    return Sequence(nlNoAction(), new Action<Object>() {
      @Override
      public boolean run(final Context<Object> context) {
        add(new Blank(context.getMatch()));
        stripSpace(peekNode());
        return true;
      }
    });
  }

  void stripSpace(final Node node) {
    // strip space
    String line = this.line.toString();
    boolean emptyLine = line.trim().length() == 0;
    nodeLine.add(node);
    boolean hasTag = hasTag();
    for (Node n : nodeLine) {
      TemplateList tokens = n.sequence;
      IntArrayStack spaces = n.spaces;
      if (hasTag && emptyLine) {
        while (spaces.size() > 0) {
          tokens.remove(spaces.pop());
        }
      } else {
        spaces.clear();
      }
    }
    resetLineTrack();
  }

  private void resetLineTrack() {
    nodeLine.clear();
    resetHasTag();
    line.setLength(0);
  }

  @Override
  public boolean push(final Object value) {
    nodeLine.add((Node) value);
    return super.push(value);
  }

  @DontLabel
  Rule comment() {
    return Sequence(
        '!',
        hasTag(true),
        ZeroOrMore(TestNot(endDelimiter()), ANY),
        endDelimiter());
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
        CharRange('A', 'Z'), '_', '$', '/', '.', ':', '-');
  }

}
