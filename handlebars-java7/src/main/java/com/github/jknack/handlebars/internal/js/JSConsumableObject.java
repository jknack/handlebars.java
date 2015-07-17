package com.github.jknack.handlebars.internal.js;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import sun.org.mozilla.javascript.internal.Scriptable;

/**
 * Better integration between java collections/arrays and js arrays. It check for data types
 * at access time and convert them when necessary.
 *
 * @author edgar
 */
@SuppressWarnings("serial")
class JSConsumableObject implements sun.org.mozilla.javascript.internal.Scriptable {

  /**
   * The logging system.
   */
  private static final Logger logger = getLogger(JSConsumableObject.class);
    
  private final boolean isArray;
  
  private Scriptable parentScope;
  
  private Scriptable prototype;
  
  /** 
   * For Map input: same as original map.
   * For collection or array input: input keyed by index.
   */
  @SuppressWarnings("rawtypes")
  private Map state;
  
  /**
   * A JS array.
   *
   * @param array Array.
   * @param context Handlebars context.
   */
  @SuppressWarnings("unchecked")
  public JSConsumableObject(final Object[] array) {
      state = new LinkedHashMap<Integer, Object>(); // try to preserve order of entries
      for (int i = 0; i<array.length; i++) {
        state.put(i, array[i]);
      }
      isArray = true;
  }
  
  public JSConsumableObject(final Map<?, ?> map) {
      state = map;
      isArray = false;
  }

  /**
   * A JS collection.
   *
   * @param collection collection.
   * @param context Handlebars context.
   */
  public JSConsumableObject(final Collection<Object> collection) {
    this(collection.toArray(new Object[collection.size()]));
  }

  @SuppressWarnings("unchecked")
  private Object translateAndReplaceIfRequired(final Object key) {
    Object value = state.get(key);
    // replace with translated object if necessary
    if (needsTranslation(value)) {
        value = translate(value);
        state.put(key, value);
    }
    return value;
  }

  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static JSConsumableObject translate(Object object) {
      if (Map.class.isInstance(object)) {
          return new JSConsumableObject((Map)object);
      } else if (Collection.class.isInstance(object)){
          return new JSConsumableObject((Collection)object);
      } else if (object.getClass().isArray()){
          return new JSConsumableObject((Object[]) object);
      }
      throw new RuntimeException("Handlebars internal error: cannot translate object, check whether translate() and needsTranslation() correspond!");
  }
  
  private static boolean needsTranslation(final Object object) {
    return Map.class.isInstance(object) || Collection.class.isInstance(object) || object.getClass().isArray();
  }

  public static Object translateIfNecessary(Object object) {
    if (needsTranslation(object)) {
      return translate(object);
    } else {
      return object;
    }
  }

  @Override
  public void delete(String arg0) {
    state.remove(arg0);
  }

  @Override
  public void delete(int arg0) {
    state.remove(arg0);
  }

  @Override
  public Object get(String arg0, Scriptable arg1) {
    return getOrNotFound(arg0);
  }

  private Object getOrNotFound(Object arg0) {
    if (state.containsKey(arg0)) {
      return translateAndReplaceIfRequired(arg0);
    } else {
      return Scriptable.NOT_FOUND;
    }
  }

  @Override
  public Object get(int arg0, Scriptable arg1) {
    return getOrNotFound(arg0);
  }

  @Override
  public String getClassName() {
    return this.getClass().getName();
  }

  @Override
  public Object getDefaultValue(Class<?> arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] getIds() {
    Set keySet = state.keySet();
    return keySet.toArray(new Object[keySet.size()]);
  }

  @Override
  public Scriptable getParentScope() {
    return parentScope;
  }

  @Override
  public Scriptable getPrototype() {
    return prototype;
  }

  @Override
  public boolean has(String arg0, Scriptable arg1) {
    return state.containsKey(arg0);
  }

  @Override
  public boolean has(int arg0, Scriptable arg1) {
    return state.containsKey(arg0);
  }

  @Override
  public boolean hasInstance(Scriptable arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void put(String arg0, Scriptable arg1, Object arg2) {
    state.put(arg0, arg2);
  }

  @Override
  public void put(int arg0, Scriptable arg1, Object arg2) {
    state.put(arg0, arg2);
  }

  @Override
  public void setParentScope(Scriptable arg0) {
    this.parentScope = arg0;
  }

  @Override
  public void setPrototype(Scriptable arg0) {
    this.prototype = arg0;
  }
}