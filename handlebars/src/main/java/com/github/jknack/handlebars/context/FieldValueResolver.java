/**
 * Copyright (c) 2012-2013 Edgar Espina
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
package com.github.jknack.handlebars.context;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A specialization of {@link MemberValueResolver} with lookup and invocation
 * support for {@link Field}.
 * It matches private, protected, package, public and no-static field.
 *
 * @author edgar.espina
 * @since 0.1.1
 */
public class FieldValueResolver extends MemberValueResolver<Field> {

  /**
   * The default value resolver.
   */
  public static final ValueResolver INSTANCE = new FieldValueResolver();

  @Override
  public boolean matches(final Field field, final String name) {
    return !isStatic(field) && field.getName().equals(name);
  }

  @Override
  protected Object invokeMember(final Field field, final Object context) {
    try {
      return field.get(context);
    } catch (Exception ex) {
      throw new IllegalStateException(
          "Shouldn't be illegal to access field '" + field.getName()
              + "'", ex);
    }
  }

  @Override
  protected Set<Field> members(final Class<?> clazz) {
    Set<Field> members = new LinkedHashSet<Field>();
    Class<?> targetClass = clazz;
    do {
      Field[] fields = targetClass.getDeclaredFields();
      for (Field field : fields) {
        if (matches(field, memberName(field))) {
          members.add(field);
        }
      }
      targetClass = targetClass.getSuperclass();
    } while (targetClass != null && targetClass != Object.class);
    return members;
  }

  @Override
  protected String memberName(final Field member) {
    return member.getName();
  }
}
