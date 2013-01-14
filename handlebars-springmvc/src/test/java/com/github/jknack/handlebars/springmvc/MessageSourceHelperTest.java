package com.github.jknack.handlebars.springmvc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.internal.AbstractOptions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AbstractOptions.class, MessageSourceHelper.class, Context.class })
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
    Map<String, Object> hash = createMock(Map.class);
    expect(hash.get("default")).andReturn(defaultMessage);

    AbstractOptions options = OptionsMock.options(params, hash);

    MessageSource messageSource = createMock(MessageSource.class);
    expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        isA(Locale.class))).andReturn(message);

    replay(messageSource, hash);

    CharSequence result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(messageSource, hash);
  }

  @Test
  public void messageSourceWithParams() throws IOException {
    String message = "Hola";
    String code = "sayHi";
    String defaultMessage = null;

    // Options
    Object[] params = {1, 2, 3};
    @SuppressWarnings("unchecked")
    Map<String, Object> hash = createMock(Map.class);
    expect(hash.get("default")).andReturn(defaultMessage);

    AbstractOptions options = OptionsMock.options(params, hash);

    MessageSource messageSource = createMock(MessageSource.class);
    expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        isA(Locale.class))).andReturn(message);

    replay(messageSource, hash);

    CharSequence result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(messageSource, hash);
  }

  @Test
  public void messageSourceWithDefaulMessage() throws IOException {
    String message = "Hola";
    String code = "sayHi";
    String defaultMessage = "Aca viene el 3";

    // Options
    Object[] params = {1, 2, 3};
    @SuppressWarnings("unchecked")
    Map<String, Object> hash = createMock(Map.class);
    expect(hash.get("default")).andReturn(defaultMessage);

    AbstractOptions options = OptionsMock.options(params, hash);

    MessageSource messageSource = createMock(MessageSource.class);
    expect(messageSource.getMessage(eq(code), eq(params), eq(defaultMessage),
        isA(Locale.class))).andReturn(message);

    replay(messageSource, hash);

    CharSequence result =
        new MessageSourceHelper(messageSource).apply(code, options);
    assertEquals(message, result);

    verify(messageSource, hash);
  }
}
