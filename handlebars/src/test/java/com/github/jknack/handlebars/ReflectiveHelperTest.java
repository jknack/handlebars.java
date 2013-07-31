package com.github.jknack.handlebars;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.custom.Blog;

public class ReflectiveHelperTest extends AbstractTest {

  @Override
  protected Handlebars newHandlebars() {
    Handlebars handlebars = new Handlebars();
    handlebars.registerHelpers(this);
    return handlebars;
  }

  @Test
  public void testHelperA() throws IOException {
    shouldCompileTo("{{helperA}}", $, "helperA");
  }

  @Test
  public void testHelperAWithContext() throws IOException {
    shouldCompileTo("{{helperAWithContext}}", $(), "helperAWithContext");
  }

  @Test
  public void testHelperAWithContextAndOptions() throws IOException {
    shouldCompileTo("{{helperAWithContextAndOptions}}", $(), "helperAWithContextAndOptions");
  }

  @Test
  public void testHelperAWithOptions() throws IOException {
    shouldCompileTo("{{helperAWithOptions}}", $, "helperAWithOptions");
  }

  @Test
  public void testHelperWithParams() throws IOException {
    shouldCompileTo("{{helperWithParams \"string\" true 4}}", $, "helperWithParams:string:true:4");
  }

  @Test
  public void testHelperWithParamsAndOptions() throws IOException {
    shouldCompileTo("{{helperWithParamsAndOptions \"string\" true 4}}", $,
        "helperWithParamsAndOptions:string:true:4");
  }

  @Test
  public void testBlog() throws IOException {
    shouldCompileTo("{{blog this}}", new Blog("title", "body"),
        "blog:title");
  }

  @Test
  public void testNullBlog() throws IOException {
    shouldCompileTo("{{nullBlog this}}", null, "blog:null");
  }

  @Test
  public void testBlogTitle() throws IOException {
    shouldCompileTo("{{blogTitle this title}}", new Blog("awesome!", "body"),
        "blog:awesome!");
  }

  @Test
  public void params() throws IOException {
    shouldCompileTo("{{params this l d f c b s}}",
        $("l", 1L, "d", 2.0D, "f", 3.0f, "c", '4', "b", (byte) 5, "s", (short) 6),
        "1, 2.0, 3.0, 4, 5, 6");
  }

  public CharSequence params(final Object context, final long l, final double d, final float f,
      final char c, final byte b, final short s) {
    return join(new Object[]{l, d, f, c, b, s }, ", ");
  }

  @Test
  public void testRuntimeException() throws IOException {
    try {
      shouldCompileTo("{{runtimeException}}", $, "");
      fail("A runtime exception is expeced");
    } catch (HandlebarsException ex) {
      assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }
  }

  @Test
  public void testCheckedException() throws IOException {
    try {
      shouldCompileTo("{{checkedException}}", $, "");
      fail("A checked exception is expeced");
    } catch (HandlebarsException ex) {
      assertTrue(ex.getCause() instanceof IllegalStateException);
    }
  }

  @Test
  public void testIOException() throws IOException {
    try {
      shouldCompileTo("{{ioException}}", $, "");
      fail("A io exception is expeced");
    } catch (HandlebarsException ex) {
      assertTrue(ex.getCause() instanceof IOException);
    }
  }

  public static String runtimeException() {
    throw new IllegalArgumentException();
  }

  public static String ioException() throws IOException {
    throw new IOException();
  }

  public static String checkedException() throws Exception {
    throw new Exception();
  }

  public static String helperA() {
    return "helperA";
  }

  public String helperAWithContext(final Hash hash) {
    assertNotNull(hash);
    return "helperAWithContext";
  }

  public String helperAWithOptions(final Options options) {
    assertNotNull(options);
    return "helperAWithOptions";
  }

  public String helperAWithContextAndOptions(final Hash hash, final Options options) {
    assertNotNull(hash);
    assertNotNull(options);
    return "helperAWithContextAndOptions";
  }

  public StringBuilder helperWithParams(final String context, final boolean p0, final int p1) {
    return new StringBuilder(String.format("helperWithParams:%s:%s:%s", context, p0, p1));
  }

  public SafeString helperWithParamsAndOptions(final String context, final boolean p0,
      final int p1,
      final Options options) {
    assertNotNull(options);
    return new SafeString(String.format("helperWithParamsAndOptions:%s:%s:%s", context, p0, p1));
  }

  public CharSequence blog(final Blog blog, final Options options) {
    assertNotNull(options);
    return "blog:" + blog.toString();
  }

  public CharSequence nullBlog(final Blog blog, final Options options) {
    assertNull(blog);
    assertNotNull(options);
    return "blog:null";
  }

  public CharSequence nullParameter(final Object context, final String param) {
    return "blog:" + param;
  }

  public CharSequence blogTitle(final Blog blog, final String title, final Options options) {
    assertNotNull(blog);
    assertNotNull(options);
    return "blog:" + title;
  }

}
