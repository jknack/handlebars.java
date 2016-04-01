[![Build Status](https://secure.travis-ci.org/jknack/handlebars.java.png?branch=master)](https://travis-ci.org/jknack/handlebars.java)
[![Coverage Status](https://coveralls.io/repos/jknack/handlebars.java/badge.png)](https://coveralls.io/r/jknack/handlebars.java)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars)

Handlebars.java
===============
## Logic-less and semantic Mustache templates with Java

Handlebars.java is a Java port of [handlebars](http://handlebarsjs.com/).

Handlebars provides the power necessary to let you build semantic templates effectively with no frustration.

[Mustache](http://mustache.github.com/mustache.5.html) templates are compatible with Handlebars, so you can take a [Mustache](http://mustache.github.com) template, import it into Handlebars, and start taking advantage of the extra Handlebars features.

# Performance

Handlebars.java is a modern and full featured template engine, but also has a very good performance (Hbs):

![Template Comparison](http://jknack.github.io/handlebars.java/images/bench.png)

Benchmark source code is available at: https://github.com/mbosecke/template-benchmark

# Getting Started
 In general, the syntax of **Handlebars** templates is a superset of [Mustache](http://mustache.github.com) templates. For basic syntax, check out the [Mustache manpage](http://mustache.github.com).
 
 The [Handlebars.java blog](http://jknack.github.io/handlebars.java) is a good place for getting started too.

## Maven
#### Stable version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jknack/handlebars)


```xml
  <dependency>
    <groupId>com.github.jknack</groupId>
    <artifactId>handlebars</artifactId>
    <version>${handlebars-version}</version>
  </dependency>
```
 
#### Development version

SNAPSHOT versions are NOT synchronized to Central. If you want to use a snapshot version you need to add the https://oss.sonatype.org/content/repositories/snapshots/ repository to your pom.xml.

## Hello Handlebars.java

```java
Handlebars handlebars = new Handlebars();

Template template = handlebars.compileInline("Hello {{this}}!");

System.out.println(template.apply("Handlebars.java"));
```

Output:
```
Hello Handlebars.java!
```

### Loading templates
Templates are loaded using the ```TemplateLoader``` class. Handlebars.java provides three implementations of a ```TemplateLoader```:
 * ClassPathTemplateLoader (default)
 * FileTemplateLoader
 * SpringTemplateLoader (see the [handlebars-springmvc](https://github.com/jknack/handlebars.java/tree/master/handlebars-springmvc) module)

This example loads ```mytemplate.hbs``` from the root of the classpath:

mytemplate.hbs:
```
Hello {{this}}!
```

```java
Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("mytemplate");

System.out.println(template.apply("Handlebars.java"));
```

Output:
```
Hello Handlebars.java!
```

You can specify a different ```TemplateLoader``` by:

```java
TemplateLoader loader = ...;
Handlebars handlebars = new Handlebars(loader);
```

#### Templates prefix and suffix
A ```TemplateLoader``` provides two important properties:
 * ```prefix```: useful for setting a default prefix where templates are stored.
 * ```suffix```: useful for setting a default suffix or file extension for your templates. Default is: ```.hbs```

Example:
```java
TemplateLoader loader = new ClassPathTemplateLoader();
loader.setPrefix("/templates");
loader.setSuffix(".html");
Handlebars handlebars = new Handlebars(loader);

Template template = handlebars.compile("mytemplate");

System.out.println(template.apply("Handlebars.java"));
```

Handlebars.java will resolve ```mytemplate``` to ```/templates/mytemplate.html``` and load it.

## The Handlebars.java Server
The handlebars.java server is small application where you can write Mustache/Handlebars template and merge them with data.

It is a useful tool for Web Designers.

Download from Maven Central:

1. Go [here](http://search.maven.org/#search%7Cga%7C1%7Chandlebars-proto)
2. Under the **Download** section click on **jar**

Maven:
```xml
<dependency>
  <groupId>com.github.jknack</groupId>
  <artifactId>handlebars-proto</artifactId>
  <version>${current-version}</version>
</dependency>
```

Usage:
```java -jar handlebars-proto-${current-version}.jar -dir myTemplates```

Example:

**myTemplates/home.hbs**

```
<ul>
 {{#items}}
 {{name}}
 {{/items}}
</ul>
```

**myTemplates/home.json**

```json
{
  "items": [
    {
      "name": "Handlebars.java rocks!"
    }
  ]
}
```

or if you prefer YAML **myTemplates/home.yml**:

```yml
list:
  - name: Handlebars.java rocks!
```

### Open a browser a type:
```
http://localhost:6780/home.hbs
```
enjoy it!


### Additional options:

* -dir: set the template directory
* -prefix: set the template's prefix, default is /
* -suffix: set the template's suffix, default is .hbs
* -context: set the context's path, default is /
* -port: set port number, default is 6780
* -content-type: set the content-type header, default is text/html

### Multiple data sources per template
Sometimes you need or want to test multiple datasets over a single template, you can do that by setting a ```data``` parameter in the request URI.

Example:

```
http://localhost:6780/home.hbs?data=mytestdata
```
Please note you don't have to specify the extension file.

## Helpers

### Built-in helpers:
 * **with**
 * **each**
 * **if**
 * **unless**
 * **log**
 * **block**
 * **partial**
 * **precompile**
 * **embedded**
 * **i18n** and **i18nJs** 
 * **string helpers**

### with, each, if, unless:
 See the [built-in helper documentation](http://handlebarsjs.com/block_helpers.html).

### block and partial
 Block and partial helpers work together to provide you [Template Inheritance](http://jknack.github.io/handlebars.java/reuse.html).

Usage:
```
  {{#block "title"}}
    ...
  {{/block}}
```
context: A string literal which defines the region's name.

Usage:
```
  {{#partial "title"}}
    ...
  {{/partial}}
```
context: A string literal which defines the region's name.

### precompile
 Precompile a Handlebars.java template to JavaScript using handlebars.js

user.hbs

```html
Hello {{this}}!
```

home.hbs

```html
<script type="text/javascript">
{{precompile "user"}}
</script>
```

Output:
```html
<script type="text/javascript">
(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['user'] = template(function (Handlebars,depth0,helpers,partials,data) {
  helpers = helpers || Handlebars.helpers;
  var buffer = "", functionType="function", escapeExpression=this.escapeExpression;


  buffer += "Hi ";
  depth0 = typeof depth0 === functionType ? depth0() : depth0;
  buffer += escapeExpression(depth0) + "!";
  return buffer;});
})();
</script>
```
You can access the precompiled template with:

```js
var template = Handlebars.templates['user']
```

By default it uses: ```/handlebars-v1.3.0.js``` to compile the template. Since handlebars.java 2.x it is also possible to use handlebars.js 2.x

```java
Handlebars handlebars = new Handlebars();
handlebars.handlebarsJsFile("/handlebars-v2.0.0.js");
```


For more information have a look at the [Precompiling Templates](https://github.com/wycats/handlebars.js/) documentation. 

Usage:
```
{{precompile "template" [wrapper="anonymous, amd or none"]}}
```
context: A template name. Required.

wrapper: One of "anonymous", "amd" or "none". Default is: "anonymous" 

There is a [maven plugin](https://github.com/jknack/handlebars.java/tree/master/handlebars-maven-plugin) available too.

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

### i18n
 A helper built on top of a {@link ResourceBundle}. A {@link ResourceBundle} is the most well known mechanism for internationalization (i18n) in Java.

Usage:

```html
{{i18n "hello"}}
```
This require a ```messages.properties``` in the root of classpath.

Using a locale:

```html
{{i18n "hello" locale="es_AR"}}
```

This requires a ```messages_es_AR.properties``` in the root of classpath.

Using a different bundle:

```html
{{i18n "hello" bundle="myMessages"}}
```
This requires a ```myMessages.properties``` in the root of classpath.

Using a message format:

```html
{{i18n "hello" "Handlebars.java"}}
```

Where ```hello``` is ```Hola {0}!```, results in ```Hola Handlebars.java!```.

### i18nJs
 Translate a ```ResourceBundle``` into JavaScript code. The generated code assumes you have the [I18n](https://github.com/fnando/i18n-js) in your application.

Usage:

```
{{i18nJs [locale] [bundle=messages]}}
```

If the locale argument is present it will translate that locale to JavaScript. Otherwise, it will use the default locale.

The generated code looks like this:

```javascript
<script type="text/javascript">
  I18n.defaultLocale = 'es_AR';
  I18n.locale = 'es_AR';
  I18n.translations = I18n.translations || {};
  // Spanish (Argentina)
  I18n.translations['es_AR'] = {
    "hello": "Hi {{arg0}}!"
  }
</script>
```

Finally, it converts message patterns like: ```Hi {0}``` into ```Hi {{arg0}}```. This make possible for the [I18n](https://github.com/fnando/i18n-js) JS library to interpolate variables.

### string helpers
 Functions like abbreviate, capitalize, join, dateFormat, yesno, etc., are available from [StringHelpers] (https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java).


### TypeSafe Templates
 TypeSafe templates are created by extending the ```TypeSafeTemplate``` interface. For example:

```java

// 1
public static interface UserTemplate extends TypeSafeTemplate<User> {

  // 2
  public UserTemplate setAge(int age);

  public UserTemplate setRole(String role);

}

// 3
UserTemplate userTmpl = handlebars.compileInline("{{name}} is {{age}} years old!")
  .as(UserTemplate.class);

userTmpl.setAge(32);

assertEquals("Edgar is 32 years old!", userTmpl.apply(new User("Edgar")));
```

 1. You extend the ```TypeSafeTemplate``` interface.
 2. You add all the set method you need. The set method can returns ```void``` or ```TypeSafeTemplate``` object.
 3. You create a new type safe template using the: ```as()``` method.

### Registering Helpers

There are two ways of registering helpers.

#### Using the ```Helper``` interface
 
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

#### Using a ```HelperSource```
A helper source is any class with public methods returning an instance of a ```CharSequence```.

```java
  public static? CharSequence methodName(context?, parameter*, options?) {
  }
```

Where: 

* A method can/can't be static
* The method's name becomes the helper's name
* Context, parameters and options are all optionals
* If context and options are present they must be the **first** and **last** arguments of the method

All these are valid definitions of helper methods:

```java
public class HelperSource {
  public String blog(Blog blog, Options options) {
    return options.fn(blog);
  }

  public static String now() {
    return new Date().toString();
  }

  public String render(Blog context, String param0, int param1, boolean param2, Options options) {
    return ...
  }
}

...

handlebars.registerHelpers(new HelperSource());

```

Or, if you prefer static methods only:


```java
handlebars.registerHelpers(HelperSource.class);

```

#### With plain ```JavaScript```
That's right since ```1.1.0``` you can write helpers in JavaScript:

helpers.js:

```javascript
Handlebars.registerHelper('hello', function (context) {
 return 'Hello ' + context;
})
```

```java
handlebars.registerHelpers(new File("helpers.js"));
```
Cool, isn't?


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

Template template = handlebars.compileInline("{{#blog-list blogs \"param0\" param1}}{{/blog-list}}");
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

Template template = handlebars.compileInline("{{#blog-list blogs}}{{/blog-list}}");
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

handlebars.compileInline("{{#blog-list blogs class=\"blog-css\"}}{{/blog-list}}");
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

handlebars.compileInline("{{#blog-list blogs}}{{/blog-list}}");
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

If a partial isn't found or if it has errors, a call stack is added:

```
/deep1.hbs:1:5: The partial '/deep2.hbs' could not be found
    {{> deep2
        ^
at /deep1.hbs:1:10
at /deep.hbs:1:10
```
### Helper/Runtime errors
Helper or runtime errors are similar to syntax errors, except for two things:

1. The location of the problem may (or may not) be the correct one
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

In short, from a helper you can throw an Exception and Handlebars.java will add the filename, line, column and the evidence.

## Advanced Usage

### Extending the context stack
 Let's say you need to access to the current logged-in user in every single view/page.
 You can publish the current logged in user by hooking into the context-stack. See it in action:
 ```java
  hookContextStack(Object model, Template template) {
    User user = ....;// Get the logged-in user from somewhere
    Map moreData = ...;
    Context context = Context
      .newBuilder(model)
        .combine("user", user)
        .combine(moreData)
        .build();
    template.apply(context);
    context.destroy();
  }
 ```
 Where is the ```hookContextStack``` method? Well, it depends on your application architecture.

### Using the ValueResolver
 By default, Handlebars.java use the JavaBean methods (i.e. public getXxx and isXxx methods) and Map as value resolvers.
 
 You can choose a different value resolver. This section describe how to do this.
 
#### The JavaBeanValueResolver
 Resolves values from public methods prefixed with "get/is"

```java
Context context = Context
  .newBuilder(model)
  .resolver(JavaBeanValueResolver.INSTANCE)
  .build();
```

#### The FieldValueResolver
 Resolves values from no-static fields.

```java
Context context = Context
  .newBuilder(model)
  .resolver(FieldValueResolver.INSTANCE)
  .build();
```

#### The MapValueResolver
 Resolves values from a ```java.util.Map``` objects.

```java
Context context = Context
  .newBuilder(model)
  .resolver(MapValueResolver.INSTANCE)
  .build();
```

#### The MethodValueResolver
 Resolves values from public methods.

```java
Context context = Context
  .newBuilder(model)
  .resolver(MethodValueResolver.INSTANCE)
  .build();
```

#### The JsonNodeValueResolver
 Resolves values from ```JsonNode``` objects.

```java
Context context = Context
  .newBuilder(model)
  .resolver(JsonNodeValueResolver.INSTANCE)
  .build();
```

Available in [Jackson 1.x](https://github.com/jknack/handlebars.java/tree/master/handlebars-json) and [Jackson 2.x](https://github.com/jknack/handlebars.java/tree/master/handlebars-jackson2) modules.

#### Using multiples value resolvers

```java
Context context = Context
  .newBuilder(model)
  .resolver(
      MapValueResolver.INSTANCE,
      JavaBeanValueResolver.INSTANCE,
      FieldValueResolver.INSTANCE
  ).build();
```

### The Cache System
 The cache system is designed to provide scalability and flexibility. Here is a quick view of the ```TemplateCache``` system:

```java
 public interface TemplateCache {

  /**
   * Remove all mappings from the cache.
   */
  void clear();

  /**
   * Evict the mapping for this source from this cache if it is present.
   *
   * @param source the source whose mapping is to be removed from the cache
   */
  void evict(TemplateSource source);

  /**
   * Return the value to which this cache maps the specified key.
   *
   * @param source source whose associated template is to be returned.
   * @param parser The Handlebars parser.
   * @return A template.
   * @throws IOException If input can't be parsed.
   */
  Template get(TemplateSource source, Parser parser) throws IOException;
}
```

As you can see, there isn't a ```put``` method. All the hard work is done in the ```get``` method, which is basically the core of the cache system.

By default, Handlebars.java uses a ```null``` cache implementation (a.k.a. no cache at all) which looks like:

```
Template get(TemplateSource source, Parser parser) throws IOException {
  return parser.parse(source);
}
```

In addition to the ```null``` cache, Handlebars.java provides three more implementations:

1. ```ConcurrentMapTemplateCache```: a template cache implementation built on top of a ```ConcurrentMap``` that detects changes in files automatically.
This implementation works very well in general, but there is a small window where two or more threads can compile the same template. This isn't a huge problem with Handlebars.java because the compiler is very very fast.
But if for some reason you don't want this, you can use the ```HighConcurrencyTemplateCache``` template cache.

2. ```HighConcurrencyTemplateCache```: a template cache implementation built on top of ```ConcurrentMap``` that detects changes in files automatically.
This cache implementation eliminates the window created by ```ConcurrentMapTemplateCache``` to ```zero```.
It follows the patterns described in [Java Concurrency in Practice](http://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601) and ensures that a template will be compiled just once regardless of the number of threads.


3. ```GuavaTemplateCache```: a template cache implementation built on top of [Google Guava](https://code.google.com/p/guava-libraries/wiki/CachesExplained). Available in [handlebars-guava-cache module](https://github.com/jknack/handlebars.java/tree/master/handlebars-guava-cache)

You can configure Handlebars.java to use a cache by:

```
Handlebars hbs = new Handlebars()
  .with(new MyCache());
```

### Using a MissingValueResolver (@deprecated)
NOTE: MissingValueResolver is available in ```<= 1.3.0```. For ```> 1.3.0``` use [Helper Missing](https://github.com/jknack/handlebars.java#helper-missing).

 A ```MissingValueResolver``` let you use default values for ```{{variable}}``` expressions resolved to ```null```.
 
```java
  MissingValueResolver missingValueResolver = new MissingValueResolver() {
    public String resolve(Object context, String name) {
      //return a default value or throw an exception
      ...;
    }
  };
  Handlebars handlebars = new Handlebars().with(missingValueResolver);
```

### Helper Missing
 By default, Handlebars.java throws an ```java.lang.IllegalArgumentException()``` if a helper cannot be resolved.
 You can override the default behaviour by providing a special helper: ```helperMissing```. Example:

```java
  handlebars.registerHelperMissing(new Helper<Object>() {
    @Override
    public CharSequence apply(final Object context, final Options options) throws IOException {
      return options.fn.text();
    }
  });
```

### String form parameters
 You can access a parameter name if you set the: ```stringParams: true```. Example:
 
```html
{{sayHi this edgar}}
```

```java
  Handlebars handlebars = new Handlebars()
    .stringParams(true);
  
  handlebars.registerHelper("sayHi", new Helper<Object>() {
    public Object apply(Object context, Options options) {
      return "Hello " + options.param(0) + "!";
    }
  });
```

results in:
```
Hello edgar!
```
 How does this work? ```stringParams: true``` instructs Handlebars.java to resolve a parameter to it's name if the value isn't present in the context stack.

### Allow Infinite loops
 By default, Handlebars.java doesn't allow a partial to call itself (directly or indirectly).
 You can change this by setting the: ```Handlebars.inifiteLoops(true)```, but watch out for a ```StackOverflowError```.

### Pretty Print
 The Mustache Spec has some rules for removing spaces and new lines. This feature is disabled by default.
 You can turn this on by setting the: ```Handlebars.prettyPrint(true)```.


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
 handlebars.registerHelper("json", JacksonHelper.INSTANCE);
```
```
 {{json context [view="foo.MyFullyQualifiedClassName"] [escapeHTML=false] [pretty=false]}}
```

Alternative:
```java
 handlebars.registerHelper("json", new JacksonHelper().viewAlias("myView",
   foo.MyFullyQualifiedClassName.class);
```
```
 {{json context [view="myView"] [escapeHTML=false] [pretty=false]}}
```

context: An object, may be null.

view: The name of the [Jackson View](http://wiki.fasterxml.com/JacksonJsonViews). Optional.

escapeHTML: True, if the JSON content contains HTML chars and you need to escaped them. Default is: false.

pretty: True, if the JSON content must be formatted. Default is: false.

## Jackson 2.x

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-jackson2</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```

Same as Jackson1.x, except for the name of the helper: ```Jackson2Helper```

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

## Humanize

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-humanize</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```
Usage:

```java
 // Register all the humanize helpers.
 HumanizeHelper.register(handlebars);
```

See the JavaDoc of the [HumanizeHelper] (https://github.com/jknack/handlebars.java/blob/master/handlebars-humanize/src/main/java/com/github/jknack/handlebars/HumanizeHelper.java) for more information.

## SpringMVC

Maven:
```xml
 <dependency>
   <groupId>com.github.jknack</groupId>
   <artifactId>handlebars-springmvc</artifactId>
   <version>${handlebars-version}</version>
 </dependency>
```

Using value resolvers:

```java
 HandlebarsViewResolver viewResolver = ...;

 viewResolver.setValueResolvers(...);
```

In addition, the HandlebarsViewResolver add a ```message``` helper that uses the Spring ```MessageSource``` class:

```
{{message "code" [arg]* [default="default message"]}}
```

where:
* code: the message's code. Required.
* arg:  the message's argument. Optional.
* default: the default's message. Optional.

Checkout the [HandlebarsViewResolver](https://github.com/jknack/handlebars.java/blob/master/handlebars-springmvc/src/main/java/com/github/jknack/handlebars/springmvc/HandlebarsViewResolver.java).

# Architecture and API Design
 * Handlebars.java follows the JavaScript API with some minors exceptions due to the nature of the Java language.
 * The parser is built on top of [ANTLR v4] (http://www.antlr.org/).
 * Data is provided as primitive types (int, boolean, double, etc.), strings, maps, list or JavaBeans objects.
 * Helpers are type-safe.
 * Handlebars.java is thread-safe.

## Differences between Handlebars.java and Handlebars.js
 Handlebars.java scope resolution follows the Mustache Spec. For example:

Given:

```json
{
  "value": "parent",
  "child": {
  }
}
```
and

```html
Hello {{#child}}{{value}}{{/child}}
```

will be:

```html
Hello parent
```

Now, the same model and template with Handlebars.js is:

```html
Hello 
```
That is because Handlebars.js doesn't look in the context stack for missing attributes in the current scope (this is consistent with the Mustache Spec).

Hopefully, you can turn-off the context stack lookup in Handlebars.java by qualifying the attribute with ```this.```:

```html
Hello {{#child}}{{this.value}}{{/child}}
```

## Differences between Handlebars.java and Mustache.js
 * Handlebars.java throws a ```java.io.FileNotFoundException``` if a partial cannot be loaded.

## Status
### Mustache 1.0 Compliant
 * Passes the 123 tests from the [Mustache Spec](https://github.com/mustache/spec).
 * Tests can be found here [comments.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/CommentsTest.java), [delimiters.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/DelimitersTest.java), [interpolation.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/InterpolationTest.java), [inverted.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/InvertedTest.java), [lambdas.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/LambdasTest.java), [partials.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/PartialsTest.java), [sections.yml](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/specs/SectionsTest.java)

### Handlebars.js Compliant
  * Passes all the [Handlebars.js tests](https://github.com/wycats/handlebars.js/blob/master/spec/qunit_spec.js)
  * Tests can be found here [basic context](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/BasicContextTest.java), [string literals](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/StringLiteralParametersTest.java), [inverted sections](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/InvertedSectionTest.java), [blocks](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/BlockTest.java), [block helper missing](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/BlockHelperMissingTest.java), [helper hash](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/HelperHashTest.java), [partials](https://github.com/jknack/handlebars.java/blob/master/handlebars/src/test/java/hbs/js/PartialsTest.java)
  
## Dependencies

```text
+- org.apache.commons:commons-lang3:jar:3.1
+- org.antlr:antlr4-runtime:jar:4.5.1-1
+- org.mozilla:rhino:jar:1.7R4
+- org.slf4j:slf4j-api:jar:1.6.4
```

## FAQ

## Want to contribute?
* Fork the project on Github.
* Wondering what to work on? See task/bug list and pick up something you would like to work on.
* Do you want to donate one or more helpers? See [handlebars=helpers](https://github.com/jknack/handlebars.java/tree/master/handlebars-helpers) a repository for community's helpers.
* Create an issue or fix one from [issues list](https://github.com/jknack/handlebars.java/issues).
* If you know the answer to a question posted to our [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply.
* Share your ideas or ask questions on [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply - that helps us improve javadocs/FAQ.
* If you miss a particular feature - browse or ask on the [mailing list](https://groups.google.com/forum/#!forum/handlebarsjava) - don't hesitate to write a reply, show us some sample code and describe the problem.
* Write a blog post about how you use or extend handlebars.java.
* Please suggest changes to javadoc/exception messages when you find something unclear.
* If you have problems with documentation, find it non intuitive or hard to follow - let us know about it, we'll try to make it better according to your suggestions. Any constructive critique is greatly appreciated. Don't forget that this is an open source project developed and documented in spare time.

## Help and Support
 [Help and discussion](https://groups.google.com/forum/#!forum/handlebarsjava)

 [Bugs, Issues and Features](https://github.com/jknack/handlebars.java/issues)

## Related Projects
 * [Handlebars.js](http://handlebarsjs.com/)
 * [Try Handlebars.js](http://tryhandlebarsjs.com/)
 * [Mustache](http://mustache.github.io/)
 * [Humanize](https://github.com/mfornos/humanize)
 * [ANTLRv4](http://www.antlr.org/)

## Author
 [Edgar Espina] (https://twitter.com/edgarespina)

## License
[Apache License 2](http://www.apache.org/licenses/LICENSE-2.0.html)
