/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.issues;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.v4Test;

public class Issue521 extends v4Test {

  @Test
  public void whiteSpaceControlShouldWorkOnElse() throws IOException {
    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>\n");
    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{else}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "\n<i>");

    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>");
    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{~else~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "<i>");

    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", true)), "<b>");
    shouldCompileTo(
        "\n{{~#if bold~}}\n<b>\n{{~^~}}\n<i>\n{{~/if~}}\n", $("hash", $("bold", false)), "<i>");
  }
}
