package com.github.jknack.handlebars;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public interface Options {

	/**
	 * Apply the {@link #fn} template using the default context.
	 *
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence fn() throws IOException;

	/**
	 * Apply the {@link #fn} template using the provided context.
	 *
	 * @param context The context to use.
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence fn(Object context) throws IOException;

	/**
	 * Apply the {@link #inverse} template using the default context.
	 *
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence inverse() throws IOException;

	/**
	 * Apply the {@link #inverse} template using the provided context.
	 *
	 * @param context The context to use.
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence inverse(Object context) throws IOException;

	/**
	 * Apply the given template to the provided context. The context stack is
	 * propagated allowing the access to the whole stack.
	 *
	 * @param template The template.
	 * @param context The context object.
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence apply(Template template, Object context) throws IOException;

	/**
	 * Apply the given template to the default context. The context stack is
	 * propagated allowing the access to the whole stack.
	 *
	 * @param template The template.
	 * @return The resulting text.
	 * @throws IOException If a resource cannot be loaded.
	 */
	CharSequence apply(Template template) throws IOException;

	/**
	 * <p>
	 * Return a parameter at given index. This is analogous to:
	 * </p>
	 * <code>
	 *  Object param = options.params[index]
	 * </code>
	 * <p>
	 * The only difference is the type safe feature:
	 * </p>
	 * <code>
	 *  MyType param = options.param(index)
	 * </code>
	 *
	 * @param <T> The runtime type.
	 * @param index The parameter position.
	 * @return The paramater's value.
	 */
	<T> T param(int index);

	/**
	 * <p>
	 * Return a parameter at given index. This is analogous to:
	 * </p>
	 * <code>
	 *  Object param = options.params[index]
	 * </code>
	 * <p>
	 * The only difference is the type safe feature:
	 * </p>
	 * <code>
	 *  MyType param = options.param(index)
	 * </code>
	 *
	 * @param <T> The runtime type.
	 * @param index The parameter position.
	 * @param defaultValue The default value to return if the parameter is not
	 *        present or if null.
	 * @return The paramater's value.
	 */
	<T> T param(int index, T defaultValue);

	/**
	 * Look for a value in the context's stack.
	 *
	 * @param <T> The runtime type.
	 * @param name The property's name.
	 * @param defaultValue The default value to return if the attribute is not
	 *        present or if null.
	 * @return The associated value or <code>null</code> if it's not found.
	 */
	<T> T get(String name, T defaultValue);

	/**
	 * Look for a value in the context's stack.
	 *
	 * @param <T> The runtime type.
	 * @param name The property's name.
	 * @return The associated value or <code>null</code> if it's not found.
	 */
	<T> T get(String name);

	/**
	 * Return a previously registered partial in the current execution context.
	 *
	 * @param path The partial's path. Required.
	 * @return A previously registered partial in the current execution context.
	 *         Or <code> null</code> if not found.
	 */
	Template partial(String path);

	/**
	 * Store a partial in the current execution context.
	 *
	 * @param path The partial's path. Required.
	 * @param partial The partial template. Required.
	 */
	void partial(String path, Template partial);

	/**
	 * <p>
	 * Find a value inside the {@link #hash} attributes. This is analogous to:
	 * </p>
	 * <code>
	 *  Object myClass = options.hash.get("class");
	 * </code>
	 * <p>
	 * This mehtod works as a shorthand and type safe call:
	 * </p>
	 * <code>
	 *  String myClass = options.hash("class");
	 * </code>
	 *
	 * @param <T> The runtime type.
	 * @param name The hash's name.
	 * @return The hash value or null.
	 */
	<T> T hash(String name);

	/**
	 * <p>
	 * Find a value inside the {@link #hash} attributes. This is analogous to:
	 * </p>
	 * <code>
	 *  Object myClass = options.hash.get("class");
	 * </code>
	 * <p>
	 * This method works as a shorthand and type safe call:
	 * </p>
	 * <code>
	 *  String myClass = options.hash("class");
	 * </code>
	 *
	 * @param <T> The runtime type.
	 * @param name The hash's name.
	 * @param defaultValue The default value to returns.
	 * @return The hash value or null.
	 */
	<T> T hash(String name, Object defaultValue);

	/**
	 * Returns false if its argument is false, null or empty list/array (a "falsy"
	 * value).
	 *
	 * @param value A value.
	 * @return False if its argument is false, null or empty list/array (a "falsy"
	 *         value).
	 */
	boolean isFalsy(Object value);

	/**
	 * Creates a {@link Context} from the given model. If the object is a context
	 * already the same object will be returned.
	 *
	 * @param model The model object.
	 * @return A context representing the model or the same model if it's a
	 *         context already.
	 */
	Context wrap(Object model);

	/**
	 * Read the attribute from the data storage.
	 *
	 * @param name The attribute's name.
	 * @param <T> Data type.
	 * @return The attribute value or null.
	 */
	<T> T data(String name);

	/**
	 * Set an attribute in the data storage.
	 *
	 * @param name The attribute's name. Required.
	 * @param value The attribute's value. Required.
	 */
	void data(String name, Object value);

	/**
	 * List all the properties and their values for the given object.
	 *
	 * @param context The context object. Not null.
	 * @return All the properties and their values for the given object.
	 */
	Set<Entry<String, Object>> propertySet(Object context);

	Object[] getParams();

	Handlebars getHandlebars();

	Template getFn();

	Map<String, Object> getHash();

}