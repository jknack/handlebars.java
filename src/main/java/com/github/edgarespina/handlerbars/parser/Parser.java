package com.github.edgarespina.handlerbars.parser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.matchers.Matcher;
import org.parboiled.support.StringVar;
import org.parboiled.support.ValueStack;
import org.parboiled.support.Var;

public class Parser extends BaseParser<BaseTemplate> {

  private class DelimAction implements Action<BaseTemplate> {

    private String delim;

    public DelimAction(final String delim) {
      this.delim = delim;
    }

    @Override
    public boolean run(final Context<BaseTemplate> context) {
      Matcher matcher = (Matcher) String(delim);
      return matcher.match((MatcherContext<BaseTemplate>) context);
    }
  }

  private String delimStart = "{{";

  private String delimEnd = "}}";

  protected List<BaseTemplate> line = new LinkedList<BaseTemplate>();

  protected List<BaseTemplate> blanks = new LinkedList<BaseTemplate>();

  public Parser() {
  }

  public Rule template() throws IOException {
    return Sequence(body(), sync(), removeBlanks(), EOI);
  }

  public Rule body() throws IOException {
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

  public Rule setDelimiters() {
    final StringVar newDelimStart = new StringVar();
    final StringVar newDelimEnd = new StringVar();
    return Sequence(delimStart(), '=', newDelimiter(),
        newDelimStart.set(match()),
        spacing(),
        newDelimiter(), newDelimEnd.set(match()), '=', delimEnd(),
        new Action<Object>() {
          @Override
          public boolean run(final Context<Object> context) {
            delimEnd = newDelimEnd.get();
            delimStart = newDelimStart.get();
            return true;
          }
        });
  }

  public Rule newDelimiter() {
    return Sequence(delim(), Optional(delim()));
  }

  public Rule delim() {
    return Sequence(TestNot(' ', '\t', '\r', '\n', '='), ANY);
  }

  public Rule text() {
    return Sequence(
        OneOrMore(
            TestNot(delimStart()),
            TestNot(spaceNoAction()),
            TestNot(nlNoAction()),
            ANY),
        add(new Text(match())));
  }

  public Rule variable() {
    return FirstOf(ampersandVar(), tripleVar(), var());
  }

  protected boolean add(final BaseTemplate template) {
    Sequence sequence = (Sequence) peek();
    sequence.add(template);
    line.add(template);
    return true;
  }

  public Rule ampersandVar() {
    return Sequence(
        delimStart(),
        "&",
        spacing(),
        identifier(),
        add(new Variable(match(), false)),
        spacing(),
        delimEnd());
  }

  public Rule tripleVar() {
    return Sequence(
        delimStart(),
        '{',
        spacing(),
        identifier(),
        add(new Variable(match(), false)),
        spacing(),
        '}',
        delimEnd());
  }

  public Rule var() {
    return Sequence(
        delimStart(),
        spacing(),
        identifier(),
        add(new Variable(match(), true)),
        spacing(),
        delimEnd());
  }

  public Action<BaseTemplate> delimStart() {
    return new DelimAction(delimStart);
  }

  public Action<BaseTemplate> delimEnd() {
    return new DelimAction(delimEnd);
  }

  public Rule partial() throws IOException {
    return Sequence(delimStart(), '>', spacing(), path(),
        add(new Partial(null, match())),
        spacing(), delimEnd());
  }

  public Rule section() throws IOException {
    final StringVar name = new StringVar();
    final Var<Boolean> inverted = new Var<Boolean>();
    final Var<Section> section = new Var<Section>();
    return Sequence(
        FirstOf(
            sectionStart('#', name, inverted),
            sectionStart('^', name, inverted)),
        section.set(new Section(name.get(), inverted.get())),
        body(),
        sectionEnd(),
        new Action<BaseTemplate>() {
          @Override
          public boolean run(final Context<BaseTemplate> context) {
            ValueStack<BaseTemplate> stack = context.getValueStack();
            if (stack.size() > 1) {
              BaseTemplate body = pop();
              add(section.get().body(body));
            }
            return true;
          }
        });
  }

  public Rule sectionStart(final char type, final StringVar name,
      final Var<Boolean> inverted) {
    return Sequence(
        delimStart(), type, inverted.set(matchedChar() == '^'),
        spacing(),
        identifier(), name.set(match()),
        spacing(),
        delimEnd());
  }

  public Rule sectionEnd() {
    return Sequence(delimStart(), '/', spacing(), identifier(), spacing(),
        delimEnd());
  }

  @SuppressSubnodes
  @MemoMismatches
  public Rule identifier() {
    return Sequence(TestNot(delimStart()), letter(),
        ZeroOrMore(letterOrDigit()));
  }

  @SuppressSubnodes
  @MemoMismatches
  public Rule path() {
    return Sequence(
        TestNot(delimStart(), delimEnd()),
        OneOrMore(pathSegment()));
  }

  public Rule spacing() {
    return ZeroOrMore(FirstOf(
        // whitespace
        spaceNoAction(),
        // nl
        nlNoAction(),
        // Comment
        comment()));
  }

  public Rule spaceNoAction() {
    return AnyOf(" \t\f");
  }

  public Rule space() {
    return Sequence(spaceNoAction(), add(new Blank(match())));
  }

  public Rule nlNoAction() {
    return FirstOf(String("\r\n"), String("\n"));
  }

  public Rule nl() {
    return Sequence(nlNoAction(), add(new Blank(match())), sync());
  }

  protected boolean sync() {
    boolean ignore = true;
    List<BaseTemplate> currentLine = this.line;
    for (BaseTemplate template : currentLine) {
      Class<? extends BaseTemplate> type = template.getClass();
      if (type == Text.class || type == Variable.class) {
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
    currentLine.clear();
    return true;
  }

  protected boolean removeBlanks() {
    BaseTemplate head = peek();
    for(BaseTemplate blank: blanks) {
      head.remove(blank);
    }
    return true;
  }

  public Rule comment() {
    return Sequence(delimStart(), '!', ZeroOrMore(TestNot(delimEnd()), ANY),
        delimEnd());
  }

  // JLS defines letters and digits as Unicode characters recognized
  // as such by special Java procedures.
  public Rule letter() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '.', '_', '$');
  }

  @MemoMismatches
  public Rule letterOrDigit() {
    return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0',
        '9'), '_', '$', '.');
  }

  public Rule pathSegment() {
    return FirstOf(CharRange('0', '9'), CharRange('a', 'z'),
        CharRange('A', 'Z'), '_', '$', '/', '.', '-');
  }
}
