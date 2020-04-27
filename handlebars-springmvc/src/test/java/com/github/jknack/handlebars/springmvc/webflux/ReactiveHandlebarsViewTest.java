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

import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;

import reactor.core.publisher.Mono;

/**
 * @author OhChang Kwon(ohchang.kwon@navercorp.com)
 */
public class ReactiveHandlebarsViewTest {

  @Test
  public void shouldRenderInternal() throws Exception {
    // Given
    final Map<String, Object> model = new HashMap<>();

    final Template mockTemplate = createMock(Template.class);
    final Capture<Context> context = EasyMock.newCapture();
    mockTemplate.apply(capture(context), isA(OutputStreamWriter.class));

    final MockServerHttpRequest testRequest = MockServerHttpRequest.get("/").build();
    final MockServerWebExchange testExchange = MockServerWebExchange.from(testRequest);

    replay(mockTemplate);

    ReactiveHandlebarsView testCase = new ReactiveHandlebarsView();
    testCase.setValueResolvers(MapValueResolver.INSTANCE);
    testCase.setTemplate(mockTemplate);

    // When
    final Mono<Void> result = testCase.renderInternal(model, null, testExchange);

    // Then
    result.block(Duration.ofSeconds(10));
    assertNotNull(context.getValue());
    verify(mockTemplate);
  }
}
