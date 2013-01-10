package com.github.jknack.handlebars;

import static org.junit.Assert.assertNotNull;

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
  public void testHelperAWithConextAndOptions() throws IOException {
    shouldCompileTo("{{helperAWithConextAndOptions}}", $(), "helperAWithConextAndOptions");
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
  public void testBlogTitle() throws IOException {
    shouldCompileTo("{{blogTitle this title}}", new Blog("awesome!", "body"),
        "blog:awesome!");
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

  public String helperAWithConextAndOptions(final Hash hash, final Options options) {
    assertNotNull(hash);
    assertNotNull(options);
    return "helperAWithConextAndOptions";
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

  public CharSequence blogTitle(final Blog blog, final String title, final Options options) {
    assertNotNull(blog);
    assertNotNull(options);
    return "blog:" + title;
  }
}
