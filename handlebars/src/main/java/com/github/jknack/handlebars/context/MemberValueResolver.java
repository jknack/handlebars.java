/*
 * Handlebars.java: https://github.com/jknack/handlebars.java
 * Apache License Version 2.0 http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (c) 2012 Edgar Espina
 */
package com.github.jknack.handlebars.context;

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.jknack.handlebars.ValueResolver;

/**
 * A specialization of {@link ValueResolver} that is built on top of reflections API. It use an
 * internal cache for saving {@link Member members}.
 *
 * @author edgar.espina
 * @param <M> The member type.
 * @since 0.1.1
 */
public abstract class MemberValueResolver<M extends Member> implements ValueResolver {

  /** A concurrent and thread-safe cache for {@link Member}. */
  private final Map<Class<?>, Map<String, M>> cache = new ConcurrentHashMap<>();

  @Override
  public final Object resolve(final Object context, final String name) {
    Class<?> key = context.getClass();
    Map<String, M> mcache = cache(key);
    M member = mcache.get(name);
    if (member == null) {
      return UNRESOLVED;
    } else {
      return invokeMember(member, context);
    }
  }

  @Override
  public Object resolve(final Object context) {
    return UNRESOLVED;
  }

  /**
   * Get or build a class member cache.
   *
   * @param clazz Owner/key.
   * @return A class cache.
   */
  private Map<String, M> cache(final Class<?> clazz) {
    Map<String, M> mcache = this.cache.get(clazz);
    if (mcache == null) {
      mcache = new HashMap<>();
      Set<M> members = members(clazz);
      for (M m : members) {
        // Mark as accessible.
        if (isUseSetAccessible(m) && m instanceof AccessibleObject) {
          ((AccessibleObject) m).setAccessible(true);
        }
        mcache.put(memberName(m), m);
      }
      this.cache.put(clazz, mcache);
    }
    return mcache;
  }

  /**
   * Determines whether or not to call {@link AccessibleObject#setAccessible(boolean)} on members
   * before they are cached.
   *
   * <p>Calling setAccessible on JDK 9 or later on private or protected declaring classes in modules
   * will result in errors so the default implementation checks to see if the declared class
   * cannonical name starts with "java." or "sun." to prevent most of these errors.
   *
   * <p>Modular applications should create their own resolvers and override this method to prevent
   * encapsulation violation errors.
   *
   * @param member Not null.
   * @return true will cause setAccessible(true) to be called.
   */
  protected boolean isUseSetAccessible(final M member) {
    Class<?> dc = member.getDeclaringClass();
    String dn = dc == null ? null : dc.getCanonicalName();
    if (dn != null && (dn.startsWith("java.") || dn.startsWith("sun."))) {
      return false;
    }
    return true;
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
   * @return True if the member is static.
   */
  protected boolean isStatic(final M member) {
    return Modifier.isStatic(member.getModifiers());
  }

  /**
   * List all the possible members for the given class.
   *
   * @param clazz The base class.
   * @return All the possible members for the given class.
   */
  protected abstract Set<M> members(Class<?> clazz);

  @Override
  public Set<Entry<String, Object>> propertySet(final Object context) {
    notNull(context, "The context is required.");
    if (context instanceof Map) {
      return Collections.emptySet();
    } else if (context instanceof Collection) {
      return Collections.emptySet();
    }
    Collection<M> members = cache(context.getClass()).values();
    Map<String, Object> propertySet = new LinkedHashMap<>();
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
