/**
 * Copyright (c) 2012-2015 Edgar Espina
 *
 * This file is part of Handlebars.java.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.handlebars.springmvc.webflux;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.result.view.AbstractUrlBasedView;
import org.springframework.web.server.ServerWebExchange;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A handlebars reactive view implementation.
 *
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
public class ReactiveHandlebarsView extends AbstractUrlBasedView {

  /**
   * The compiled template.
   */
  private Template template;

  /**
   * The value's resolvers.
   */
  private ValueResolver[] valueResolvers;

  /**
   * Set the value resolvers.
   *
   * @param valueResolvers The value resolvers. Required.
   * @throws IllegalArgumentException If the value resolvers are null or empty.
   */
  void setValueResolvers(final ValueResolver... valueResolvers) {
    if (ArrayUtils.isEmpty(valueResolvers)) {
      throw new IllegalArgumentException("At least one value-resolver must be present.");
    } else {
      this.valueResolvers = valueResolvers;
    }
  }

  /**
   * @return The underlying template for this view.
   */
  public Template getTemplate() {
    return this.template;
  }

  /**
   * Set the compiled template.
   *
   * @param template The compiled template. Required.
   */
  void setTemplate(final Template template) {
    this.template = Objects.requireNonNull(template,
        "A handlebars template is required.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkResourceExists(final Locale locale) {
    return template != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Mono<Void> renderInternal(final Map<String, Object> renderAttributes,
                                      final MediaType contentType,
                                      final ServerWebExchange exchange) {
    final Context context = Context.newBuilder(renderAttributes)
        .resolver(valueResolvers)
        .build();

    final DataBuffer dataBuffer = exchange.getResponse().bufferFactory().allocateBuffer();
    final Charset charset = Optional.ofNullable(contentType).map(MimeType::getCharset)
        .orElse(getDefaultCharset());

    try (final Writer writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charset)) {
      template.apply(context, writer);
      writer.flush();
    } catch (IOException e) {
      DataBufferUtils.release(dataBuffer);
      return Mono.error(e);
    } finally {
      context.destroy();
    }

    return exchange.getResponse().writeWith(Flux.just(dataBuffer));
  }
}
