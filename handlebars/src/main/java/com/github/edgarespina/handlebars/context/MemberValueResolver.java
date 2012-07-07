/**
 * Copyright (c) 2012 Edgar Espina
 * This file is part of Handlebars.java.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.edgarespina.handlebars.context;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.edgarespina.handlebars.ValueResolver;

/**
 * A specialization of {@link ValueResolver} that is built on top of reflections
 * API. It use an internal cache for saving {@link Member members}.
 *
 * @author edgar.espina
 * @param <M> The member type.
 * @since 0.1.1
 */
public abstract class MemberValueResolver<M extends Member>
    implements ValueResolver {

  /**
   * A concurrent and thread-safe cache for {@link Member}.
   */
  private final Map<String, M> cache =
      new ConcurrentHashMap<String, M>();

  @Override
  public final Object resolve(final Object context, final String name) {
    String key = key(context, name);
    M member = cache.get(key);
    if (member == null) {
      member = find(context.getClass(), name);
      if (member == null) {
        // No luck, move to the next value resolver.
        return UNRESOLVED;
      }
      // Mark as accessible.
      if (member instanceof AccessibleObject) {
        ((AccessibleObject) member).setAccessible(true);
      }

      cache.put(key, member);
    }
    return invokeMember(member, context);
  }

  /**
   * Find a {@link Member} in the given class.
   *
   * @param clazz The context's class.
   * @param name The attribute's name.
   * @return A {@link Member} or null.
   */
  protected abstract M find(Class<?> clazz, String name);

  /**
   * Invoke the member in the given context.
   *
   * @param member The class member.
   * @param context The context object.
   * @return The resulting value.
   */
  protected abstract Object invokeMember(M member, Object context);

  /**
   * True, if the member matches the one we look for.
   *
   * @param member The class {@link Member}.
   * @param name The attribute's name.
   * @return True, if the member matches the one we look for.
   */
  public abstract boolean matches(M member, String name);

  /**
   * True if the member is public.
   *
   * @param member The member object.
   * @return True if the member is public.
   */
  protected boolean isPublic(final M member) {
    return Modifier.isPublic(member.getModifiers());
  }

  /**
   * True if the member is private.
   *
   * @param member The member object.
   * @return True if the member is private.
   */
  protected boolean isPrivate(final M member) {
    return Modifier.isPrivate(member.getModifiers());
  }

  /**
   * True if the member is protected.
   *
   * @param member The member object.
   * @return True if the member is protected.
   */
  protected boolean isProtected(final M member) {
    return Modifier.isProtected(member.getModifiers());
  }

  /**
   * True if the member is static.
   *
   * @param member The member object.
   * @return True if the member is statuc.
   */
  protected boolean isStatic(final M member) {
    return Modifier.isStatic(member.getModifiers());
  }

  /**
   * Creates a key using the context and the attribute's name.
   *
   * @param context The context object.
   * @param name The attribute's name.
   * @return A unique key from the given parameters.
   */
  private String key(final Object context, final String name) {
    return context.getClass().getName() + "#" + name;
  }

}
