package com.github.jknack.handlebars.issues;

import com.github.jknack.handlebars.v4Test;
import org.junit.Test;

import java.io.IOException;

public class Issue521 extends v4Test {

  @Test
  public void whiteSpaceControlShouldWorkOnElse() throws IOException {
    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>\n");
    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "\n<i>");

    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>");
    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "<i>");

    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>");
    shouldCompileTo("\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "<i>");
  }

}
