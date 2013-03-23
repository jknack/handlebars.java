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

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.jknack.handlebars.ValueResolver;

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
  private final Map<String, Object> cache =
      new ConcurrentHashMap<String, Object>();

  @Override
  public final Object resolve(final Object context, final String name) {
    String key = key(context, name);
    Object value = cache.get(key);
    if (value == UNRESOLVED) {
      return value;
    }
    @SuppressWarnings("unchecked")
    M member = (M) value;
    if (member == null) {
      member = find(context.getClass(), name);
      if (member == null) {
        // No luck, move to the next value resolver.
        cache.put(key, UNRESOLVED);
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
  protected final M find(final Class<?> clazz, final String name) {
    Set<M> members = membersFromCache(clazz);
    for (M member : members) {
      if (matches(member, name)) {
        return member;
      }
    }
    return null;
  }

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

  /**
   * List all the possible members for the given class.
   *
   * @param clazz The base class.
   * @return All the possible members for the given class.
   */
  protected abstract Set<M> members(Class<?> clazz);

  /**
   * List all the possible members for the given class.
   *
   * @param clazz The base class.
   * @return All the possible members for the given class.
   */
  protected Set<M> membersFromCache(final Class<?> clazz) {
    String key = clazz.getName();
    @SuppressWarnings("unchecked")
    Set<M> members = (Set<M>) cache.get(key);
    if (members == null) {
      members = members(clazz);
      cache.put(key, members);
    }
    return members;
  }

  @Override
  public Set<Entry<String, Object>> propertySet(final Object context) {
    notNull(context, "The context is required.");
    if (context instanceof Map) {
      return Collections.emptySet();
    } else if (context instanceof Collection) {
      return Collections.emptySet();
    }
    Set<M> members = membersFromCache(context.getClass());
    Map<String, Object> propertySet = new LinkedHashMap<String, Object>();
    for (M member : members) {
      String name = memberName(member);
      propertySet.put(name, resolve(context, name));
    }
    return propertySet.entrySet();
  }

  /**
   * Get the name for the given member.
   *
   * @param member A class member.
   * @return The member's name.
   */
  protected abstract String memberName(M member);
}
