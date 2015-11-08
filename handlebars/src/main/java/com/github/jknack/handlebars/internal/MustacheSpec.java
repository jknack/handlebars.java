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
package com.github.jknack.handlebars.internal;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;

import com.github.jknack.handlebars.internal.HbsParser.AmpvarContext;
import com.github.jknack.handlebars.internal.HbsParser.BlockContext;
import com.github.jknack.handlebars.internal.HbsParser.CommentContext;
import com.github.jknack.handlebars.internal.HbsParser.DelimitersContext;
import com.github.jknack.handlebars.internal.HbsParser.NewlineContext;
import com.github.jknack.handlebars.internal.HbsParser.PartialContext;
import com.github.jknack.handlebars.internal.HbsParser.SpacesContext;
import com.github.jknack.handlebars.internal.HbsParser.TemplateContext;
import com.github.jknack.handlebars.internal.HbsParser.TextContext;
import com.github.jknack.handlebars.internal.HbsParser.TvarContext;
import com.github.jknack.handlebars.internal.HbsParser.UnlessContext;
import com.github.jknack.handlebars.internal.HbsParser.VarContext;

/**
 * Remove space and lines according to the Mustache Spec.
 *
 * @author edgar.espina
 * @since 0.10.0
 */
public class MustacheSpec extends HbsParserBaseListener {

  /**
   * Track if the current line has real text (not spaces).
   */
  private boolean nonSpace = false;

  /**
   * Track if the current line has mustache instruction.
   */
  private Boolean hasTag;

  /**
   * Track the current line.
   */
  protected StringBuilder line = new StringBuilder();

  /**
   * Track the spaces/lines that need to be excluded.
   */
  private List<CommonToken> spaces = new ArrayList<CommonToken>();

  @Override
  public void enterSpaces(final SpacesContext ctx) {
    CommonToken space = (CommonToken) ctx.SPACE().getSymbol();
    line.append(space.getText());
    spaces.add(space);
  }

  @Override
  public void enterNewline(final NewlineContext ctx) {
    CommonToken newline = (CommonToken) ctx.NL().getSymbol();
    spaces.add(newline);
    stripSpaces();
  }

  @Override
  public void exitTemplate(final TemplateContext ctx) {
    stripSpaces();
  }

  /**
   * Move tokens to the hidden channel if necessary.
   */
  private void stripSpaces() {
    boolean hasTag = this.hasTag == null ? false : this.hasTag.booleanValue();
    if (hasTag && !nonSpace) {
      for (CommonToken space : spaces) {
        space.setChannel(Token.HIDDEN_CHANNEL);
      }
    } else {
      spaces.clear();
    }

    this.hasTag = null;
    nonSpace = false;
    line.setLength(0);
  }

  @Override
  public void enterText(final TextContext ctx) {
    nonSpace = true;
  }

  @Override
  public void enterBlock(final BlockContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitBlock(final BlockContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterComment(final CommentContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitPartial(final PartialContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterDelimiters(final DelimitersContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterUnless(final UnlessContext ctx) {
    hasTag(true);
  }

  @Override
  public void exitUnless(final UnlessContext ctx) {
    hasTag(true);
  }

  @Override
  public void enterAmpvar(final AmpvarContext ctx) {
    hasTag(false);
  }

  @Override
  public void enterTvar(final TvarContext ctx) {
    hasTag(false);
  }

  @Override
  public void enterVar(final VarContext ctx) {
    hasTag(false);
  }

  /**
   * Mark the current line with a mustache instruction.
   *
   * @param hasTag True, to indicate there is a mustache instruction.
   */
  private void hasTag(final boolean hasTag) {
    if (this.hasTag != Boolean.FALSE) {
      this.hasTag = hasTag;
    }
  }
}
