[![Build Status](http://travis-ci.org/alexo/wro4j.png)](http://travis-ci.org/jknack/handlebars.java)

Handlebars.java - Logic-less and semantic templates with Java
===============
Handlebars.java is a Java port of [handlebars](http://handlebarsjs.com/).

Handlebars provides the power necessary to let you build semantic templates effectively with no frustration.

[Mustache](http://mustache.github.com/mustache.5.html) templates are compatible with Handlebars, so you can take a [Mustache](http://mustache.github.com) template, import it into Handlebars, and start taking advantage of the extra Handlebars features.

# Getting Started
 In general, the syntax of **Handlebars** templates is a superset of [Mustache](http://mustache.github.com) templates. For basic syntax, check out the [Mustache manpage](http://mustache.github.com).

## Maven
 Stable version: **0.4.2**

```xml
  <dependency>
    <groupId>com.github.jknack</groupId>
    <artifactId>handlebars</artifactId>
    <version>${handlebars-version}</version>
  </dependency>
```

## Hello Handlebars.java

```java
Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("Hello {{.}}!");

System.out.println(template.apply("Handlebars.java"));
```
Output:
```
Hello Handlebars.java!
```

## Helpers

### Built-in helpers:
 * **with**
 * **each**
 * **if**
 * **unless**
 * **log**
 * **block**
 * **partial**
 * **embedded**

### with, each, if, unless:
 See the [built-in helper documentation](http://handlebarsjs.com/block_helpers.html).

### block and partial
 Block and partial helpers work together to provide you [Template Inheritance](http://thejohnfreeman.com/blog/2012/03/23/template-inheritance-for-handlebars.html).

Usage:
```
  {{#block "title"}}
    ...
  {{/block}}
```
context: A string literal which define the region's name.

Usage:
```
  {{#partial "title"}}
    ...
  {{/partial}}
```
context: A string literal which define the region's name.

### embedded
 The embedded helper allow you to "embedded" a handlebars template inside a ```<script>``` HTML tag:

user.hbs

```html
<tr>
  <td>{{firstName}}</td>
  <td>{{lastName}}</td>
</tr>
```

home.hbs

```html
<html>
...
{{embedded "user"}}
...
</html>
```
Output:
```html
<html>
...
<script id="user-hbs" type="text/x-handlebars">
<tr>
  <td>{{firstName}}</td>
  <td>{{lastName}}</td>
</tr>
</script>
...
</html>
```

Usage:
```
{{embedded "template"}}
```
context: A template name. Required.

### Registering Helpers

```java
handlebars.registerHelper("blog", new Helper<Blog>() {
  public CharSequence apply(Blog blog, Options options) {
    return options.fn(blog);
  }
});
```

```java
handlebars.registerHelper("blog-list", new Helper<List<Blog>>() {
  public CharSequence apply(List<Blog> list, Options options) {
    String ret = "<ul>";
    for (Blog blog: list) {
      ret += "<li>" + options.fn(blog) + "</li>";
    }
    return new Handlebars.SafeString(ret + "</ul>");
  }
});
```

### Helper Options

#### Parameters
```java
handlebars.registerHelper("blog-list", new Helper<Blog>() {
  public CharSequence apply(List<Blog> list, Options options) {
    String p0 = options.param(0);
    assertEquals("param0", p0);
    Integer p1 = options.param(1);
    assertEquals(123, p1);
    ...
  }
});

Bean bean = new Bean();
bean.setParam1(123);

Template template = handlebars.compile("{{#blog-list blogs \"param0\" param1}}{{/blog-list}}");
template.apply(bean);
```

#### Default parameters
```java
handlebars.registerHelper("blog-list", new Helper<Blog>() {
  public CharSequence apply(List<Blog> list, Options options) {
    String p0 = options.param(0, "param0");
    assertEquals("param0", p0);
    Integer p1 = options.param(1, 123);
    assertEquals(123, p1);
    ...
  }
});

Template template = handlebars.compile("{{#blog-list blogs}}{{/blog-list}}");
```

#### Hash
```java
handlebars.registerHelper("blog-list", new Helper<Blog>() {
  public CharSequence apply(List<Blog> list, Options options) {
    String class = options.hash("class");
    assertEquals("blog-css", class);
    ...
  }
});

handlebars.compile("{{#blog-list blogs class=\"blog-css\"}}{{/blog-list}}");
```

#### Default hash
```java
handlebars.registerHelper("blog-list", new Helper<Blog>() {
  public CharSequence apply(List<Blog> list, Options options) {
    String class = options.hash("class", "blog-css");
    assertEquals("blog-css", class);
    ...
  }
});

handlebars.compile("{{#blog-list blogs}}{{/blog-list}}");
```
## Error reporting

### Syntax errors

```
file:line:column: message
   evidence
   ^
[at file:line:column]
```

Examples:

template.hbs
```
{{value
```

```
/templates.hbs:1:8: found 'eof', expected: 'id', 'parameter', 'hash' or '}'
    {{value
           ^
```

If a partial isn't found or if has errors, a call stack is added

```
/deep1.hbs:1:5: The partial '/deep2.hbs' could not be found
    {{> deep2
        ^
at /deep1.hbs:1:10
at /deep.hbs:1:10
```
### Helper/Runtime errors
Helper or runtime errors are similar to syntax errors, except for two thing:

1. The location of the problem may (or may not) be the correct one.
2. The stack-trace isn't available

Examples:

Block helper:

```java
public CharSequence apply(final Object context, final Options options) throws IOException {
  if (context == null) {
    throw new IllegalArgumentException(
        "found 'null', expected 'string'");
  }
  if (!(context instanceof String)) {
    throw new IllegalArgumentException(
        "found '" + context + "', expected 'string'");
  }
  ...
}
```

base.hbs
```

{{#block}} {{/block}}
```

Handlebars.java reports:
```
/base.hbs:2:4: found 'null', expected 'string'
    {{#block}} ... {{/block}}
```

In short from a helper you can throw an Exception and Handlebars.java will add the filename, line, column and the evidence.

## Advanced Usage

### Extending the context stack
 Let's say you need to access to the current logged-in user in every single view/page.
 You can publishing the current logged in user by hooking into the context-stack. See it in action:
 ```java
  hookContextStack(Object model, Template template) {
    User user = ....;// Get the logged-in user from somewhere
    Map moreData = ...;
    Context context = Context
      .newBuilder(model)
        .combine("user", user)
        .combine(moreData)
        .build();
    template.apply(user);
  }
 ```
 Where is the ```hookContextStack``` method? Well, that will depends on your application architecture.

### Using the ValueResolver
 By default, Handlebars.java use the JavaBean methods (i.e. public getXxx methods) and Map as value resolvers.
 
 You can choose a different value resolver. This section describe how to do it.
 
#### The JavaBeanValueResolver
 Resolves a value as public method prefixed with "get"

```java
Context context = Context
  .newBuilder(model)
  .resolver(JavaBeanValueResolver.INSTANCE)
  .build();
```

#### The FieldValueResolver
 Resolves a value as no-static field.

```java
Context context = Context
  .newBuilder(model)
  .resolver(FieldValueResolver.INSTANCE)
  .build();
```

#### The MapValueResolver
 It resolve values as map key.

```java
Context context = Context
  .newBuilder(model)
  .resolver(MapValueResolver.INSTANCE)
  .build();
```

#### The MethodValueResolver
 Resolves a value as public method.

```java
Context context = Context
  .newBuilder(model)
  .resolver(MethodValueResolver.INSTANCE)
  .build();
```

#### Using multiples value resolvers
 Finally, you can merge multiples value resolvers

```java
Context context = Context
  .newBuilder(model)
  .resolver(
      MapValueResolver.INSTANCE,
      JavaBeanValueResolver.INSTANCE,
      FieldValueResolver.INSTANCE)
  .build();
```

# Modules
## Jackson 1.x

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-json</artifactId>
   <version>${handlebars-version}</version>
 </dependency>

```
Usage:

```java
 handlebars.registerHelper("json", new JSONHelper());
```
```
 {{json context [view="foo.MyFullyQualifiedClassName"]}}
```

Alternative:
```java
 handlebars.registerHelper("json", new JSONHelper().viewAlias("myView",
   foo.MyFullyQualifiedClassName.class);
```
```
 {{json context [view="myView"]}}
```

context: An object, may be null.

view: The name of the [Jackson View](http://wiki.fasterxml.com/JacksonJsonViews). Optional.

## Jackson 2.x

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-jackson2</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```

Similar to Jackson1.x, except for the name of the helper: ```Jackson2Helper```

## Markdown

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-markdown</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```
Usage:

```java
 handlebars.registerHelper("md", new MarkdownHelper());
```
```
 {{md context}}
```
context: An object or null. Required.

## SpringMVC

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-springmvc</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```

Checkout the HandlebarsViewResolver.

## String Helpers
 Functions like abbreviate, capitalize, join, dateFormat, yesno, etc., are available from [StringHelpers] (https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/StringHelpers.java).
 
### Usage:
```java
 StringHelpers.register(handlebars);
```

# Architecture
 * Handlebars.java follows the JavaScript API with some minors exceptions due to the nature of the Java language.
 * The parser is built on top of [Parboiled] (https://github.com/sirthias/parboiled).
 * Data is provided as primitive types (int, boolean, double, etc.), strings, maps, list or JavaBeans objects.
 * Helpers are type-safe.
 * Handlebars.java is thread-safe.

## Differences between Handlebars.js and Handlebars.java
 * Paths aren't support in Handlebars.java and there isn't a plan for adding them.

## Status
### Mustache Spec
 * Passes 123 of 127 tests from the [Mustache Spec](https://github.com/mustache/spec).
 * The 4 missing tests are: "Standalone Line Endings", "Standalone Without Previous Line", "Standalone Without Newline", "Standalone Indentation" all them from partials.yml.
 * In short, partials works 100% if you ignore white spaces indentation.
 
## Dependencies
 Handlebars.java depends on:
 
 ```text
+- org.apache.commons:commons-lang3:jar:3.1:compile
+- org.parboiled:parboiled-java:jar:1.0.2:compile
|  +- asm:asm:jar:3.3.1:compile
|  +- asm:asm-util:jar:3.3.1:compile
|  +- asm:asm-tree:jar:3.3.1:compile
|  +- asm:asm-analysis:jar:3.3.1:compile
|  \- org.parboiled:parboiled-core:jar:1.0.2:compile
+- org.slf4j:slf4j-api:jar:1.6.4:compile
 ```

## FAQ

## Want to contribute?
* Fork the project on Github.
* Wandering what to work on? See task/bug list and pick up something you would like to work on.
* Create an issue or fix one from [issues list](https://github.com/jknack/handlebars.java/issues).
* If you know the answer to a question posted to our [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply.
* Share your ideas or ask questions on [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply - that helps us improve javadocs/FAQ.
* If you miss a particular feature - browse or ask on the [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply, show us a sample code and describe the problem.
* Write a blog post about how you use or extend handlebars.java.
* Please suggest changes to javadoc/exception messages when you find something unclear.
* If you have problems with documentation, find it non intuitive or hard to follow - let us know about it, we'll try to make it better according to your suggestions. Any constructive critique is greatly appreciated. Don't forget that this is an open source project developed and documented in spare time.

## Help and Support
 [Help and discussion](https://groups.google.com/forum/#!forum/handlebarsjava)

 [Bugs, Issues and Features](https://github.com/jknack/handlebars.java/issues)

## Related Projects
 * [Handlebars.js](http://handlebarsjs.com/)
 * [Try Handlebars.js](http://tryhandlebarsjs.com/)
 * [Mustache](mustache.github.com)

## Credits
 * [Mathias](https://github.com/sirthias): For the [parboiled](https://github.com/sirthias/parboiled) PEG library
 * [Handlebars.js](https://github.com/wycats/handlebars.js/)

## License
[Apache License 2](http://www.apache.org/licenses/LICENSE-2.0.html)
