package com.github.jknack.handlebars.springmvc;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.MessageSource;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

public class MessageSourceHelperTest {
  @Test(expected = NullPointerException.class)
  public void nullMessageSource() {
    new MessageSourceHelper(null);
  }

  @Test
  public void messageSource() throws IOException {
    String message = "Hola";
    String code = "sayHi";
    String defaultMessage = null;

    // Options
    Object[] params = {};
    @SuppressWarnings("unchecked")
    Map<String, Object> hash = mock(Map.class);
    when(hash.get("default")).thenReturn(defaultMessage);

    Handlebars hbs = mock(Handlebars.class);
    Context ctx = mock(Context.class);
    Template fn = mock(Template.class);

    Options options = new Options.Builder(hbs, "messageSource", TagType.VAR, ctx, fn)
        .setParams(params)
        .setHash(hash)
        .build();

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class))).thenReturn(message);

    Object result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(hash).get("default");
    verify(messageSource).getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class));
  }

  @Test
  public void messageSourceWithParams() throws IOException {
    String message = "Hola";
    String code = "sayHi";
    String defaultMessage = null;

    // Options
    Object[] params = {1, 2, 3 };
    @SuppressWarnings("unchecked")
    Map<String, Object> hash = mock(Map.class);
    when(hash.get("default")).thenReturn(defaultMessage);

    Handlebars hbs = mock(Handlebars.class);
    Context ctx = mock(Context.class);
    Template fn = mock(Template.class);

    Options options = new Options.Builder(hbs, "messageSource", TagType.VAR, ctx, fn)
        .setParams(params)
        .setHash(hash)
        .build();

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class))).thenReturn(message);

    Object result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(hash).get("default");
    verify(messageSource).getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class));
  }

  @Test
  public void messageSourceWithDefaulMessage() throws IOException {
    String message = "Hola";
    String code = "sayHi";
    String defaultMessage = "Aca viene el 3";

    // Options
    Object[] params = {1, 2, 3 };
    @SuppressWarnings("unchecked")
    Map<String, Object> hash = mock(Map.class);
    when(hash.get("default")).thenReturn(defaultMessage);

    Handlebars hbs = mock(Handlebars.class);
    Context ctx = mock(Context.class);
    Template fn = mock(Template.class);

    Options options = new Options.Builder(hbs, "messageSource", TagType.VAR, ctx, fn)
        .setParams(params)
        .setHash(hash)
        .build();

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class))).thenReturn(message);

    Object result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(hash).get("default");
    verify(messageSource).getMessage(eq(code), eq(params), eq(defaultMessage),
        any(Locale.class));
  }
}
