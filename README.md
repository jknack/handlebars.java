Handlebars.java - Logic-less and semantic templates with Java
===============
Handlebars.java is a Java port of [handlebars](http://handlebarsjs.com/).

Handlebars provides the power necessary to let you build semantic templates effectively with no frustration.

[Mustache](http://mustache.github.com/mustache.5.html) templates are compatible with Handlebars, so you can take a [Mustache](http://mustache.github.com/mustache.5.html) template, import it into Handlebars, and start taking advantage of the extra Handlebars features.

# Getting Started
## Maven

```xml
  <dependency>
    <groupId>com.github.edgarespina</groupId>
    <artifactId>handlebars</artifactId>
    <version>0.2.0</version>
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
 * 'with'
 * 'each'
 * 'if'
 * 'unless'
 * 'log'
 * 'dateFormat'
 * 'block'
 * 'partial'
 * 'embedded'

### with, each, if, unless:
 See the [built-in helper documentation](http://handlebarsjs.com/block_helpers.html).

### dateFormat:

Usage:
```
  {{dateFormat date ["format"] ["locale"]}}
```

context: a java.util.Date object. Required.

param(0): one of "full", "long", "medium", "sort" or a date pattern like ("MM/dd/yyyy", etc.). Default is "medium".

param(1): a locale representation ("es_AR", "en", "es", etc). Default is platform specific.

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
 The embedded helper allow you to "embedded" a handlebars template inside a ```<script>``` HTML tag. See it in action:

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

### Type Safety:

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
 Where is the ```hookContextStack``` method? Well, that will depends on our current application architecture.

### Using the ValueResolver
 By default, Handlebars.java use the JavaBean methods (i.e. public getXxx methods) and Map as value resolvers.
 
 You can choose a different value resolver and this section describe how to do it.
 
#### The JavaBeanValueResolver
 It resovle values as public method prefixed with "get"

```java
Context context = Context
  .newBuilder(model)
  .resolver(JavaBeanValueResolver.INSTANCE)
  .build();
```

#### The FieldValueResolver
 It resolve values as no-static fields.

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
 It resolve values as public methods.

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
## JSON

Maven:
```xml
 <dependency>
   <groupId>com.github.edgarespina</groupId>
   <artifactId>handlebars-json</artifactId>
   <version>0.2.0</version>
 </dependency>
```
Usage:

```java
 handlebars.registerHelper("json", new JSONHelper());
```
```
 {{json context}}
```
context: An object or null. Required.

## Markdown

Maven:
```xml
 <dependency>
   <groupId>com.github.edgarespina</groupId>
   <artifactId>handlebars-markdown</artifactId>
   <version>0.2.0</version>
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
   <groupId>com.github.edgarespina</groupId>
   <artifactId>handlebars-springmvc</artifactId>
   <version>0.2.0</version>
 </dependency>
```

Checkout the HandlebarsViewResolver.

# Architecture
 * Handlebars.java follows the JavaScript API with some minors exceptions due to the nature of the Java language.
 * The parser is built on top of [Parboiled] (https://github.com/sirthias/parboiled).
 * Data is provided as primitive types (int, boolean, double, etc.), strings, maps, list or JavaBeans objects.
 * Helpers are type-safe.
 * Handlebars.java is thread-safe.

## Status
### Mustache Spec
 * Passes 123 of 127 tests from the [Mustache Spec](https://github.com/mustache/spec).
 * The 4 missing tests are: "Standalone Line Endings", "Standalone Without Previous Line", "Standalone Without Newline", "Standalone Indentation" all them from partials.yml.
 * In short, partials works 100% if you ignore white spaces indentation.
 
## Dependencies
 Handlebars.java depends on:
 
 ```text
  +- org.parboiled:parboiled-java:jar:1.0.2:compile
  |  +- asm:asm:jar:3.3.1:compile
  |  +- asm:asm-util:jar:3.3.1:compile
  |  +- asm:asm-tree:jar:3.3.1:compile
  |  +- asm:asm-analysis:jar:3.3.1:compile
  |  \- org.parboiled:parboiled-core:jar:1.0.2:compile
  \- org.slf4j:slf4j-api:jar:1.6.4:compile
 ```

## FAQ

## Help and Support
 [Help and discussion](https://groups.google.com/forum/#!forum/handlebarsjava)

 [Bugs, Issues and Features](https://github.com/edgarespina/handlebars.java/issues)

## Credits
 * [Mathias](https://github.com/sirthias): For the [parboiled](https://github.com/sirthias/parboiled) PEG library

## License
[Apache License 2](http://www.apache.org/licenses/LICENSE-2.0.html)