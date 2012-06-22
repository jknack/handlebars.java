Handlebars.java - Logic-less and semantic templates with Java
===============
Handlebars.java is a Java port of [handlebars](http://handlebarsjs.com/).

Handlebars provides the power necessary to let you build semantic templates effectively with no frustration.

[Mustache](http://mustache.github.com/mustache.5.html) templates are compatible with Handlebars, so you can take a [Mustache](http://mustache.github.com/mustache.5.html) template, import it into Handlebars, and start taking advantage of the extra Handlebars features.

# Getting Started

## Basic Usage
```java
Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("Hello {{.}}!");

System.out.println(template.apply("Handlebars.java"));
```
Output:
```
Hello Handlebars.java!
```

## JavaBean Usage
```java
Blog blog = new Blog("My First Post", "edgar");
blog.setBody("...");

Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("{{title}} by {{author}}\n{{body}}");

System.out.println(template.apply(blog));
```
Output:
```
My First Post by edgar
...
```

## Map Usage
```java
Map blog = new HashMap();
blog.put("title", "My First Post");
blog.put("author", "edgar");
blog.put("body", "...");

Handlebars handlebars = new Handlebars();

Template template = handlebars.compile("{{title}} by {{author}}\n{{body}}");

System.out.println(template.apply(blog));
```
Output:
```
My First Post by edgar
...
```

## Helper Usage
```java

Map<String, String> nav1 = new HashMap<String, String>();
nav1.put("url", "http://www.yehudakatz.com");
nav1.put("title", "Katz Got Your Tongue");

Map<String, String> nav2 = new HashMap<String, String>();
nav2.put("url", "http://www.sproutcore.com/block");
nav2.put("title", "SproutCore Blog");

Map<String, Object> navs = new HashMap<String, Object>();
navs.put("nav", Arrays.asList(nav1, nav2));

Handlebars handlebars = new Handlebars();
handlebars.registerHelper("list", new Helper<List<Map<String, String>>>() {
  public CharSequence apply(List<Map<String, String>> context, Options options) {
    String ret = "<ul>";
    
    for(Map<String, String> nav: context) {
      ret += "<li>" + options.fn(nav) + "</li>";
    }
    
    return new Handlebars.SafeString(ret + "</ul>");
  }
});

Template template = handlebars.compile("{{#list nav}}<a href="{{url}}">{{title}}</a>{{/list}}");

System.out.println(template.apply(navs));
```
Output:
```
<ul>
<li><a href="http://www.yehudakatz.com">Katz Got Your Tongue</a></li>
<li><a href="http://www.sproutcore.com/block">SproutCore Blog</a></li>
</ul>
```

## Status
### Mustache Spec
 * Passes 123 of 127 tests from the [Mustache Spec](https://github.com/mustache/spec).
 * The 4 missing tests are: "Standalone Line Endings", "Standalone Without Previous Line", "Standalone Without Newline", "Standalone Indentation" all them from partials.yml.
 * In short, partials works 100% if you ignore white spaces indentation.

### Maven Central
 * There isn't a public release yet.

## Design
 * Handlebars.java do the best to follows the JavaScript API with some minors exceptions due to the nature of the Java Language.
 * The parser is built on top of [Parboiled] (https://github.com/sirthias/parboiled).
 * Data is provided as primitive types (int, boolean, double, etc.), strings, maps, list or JavaBeans objects.
 * Handlebars.java is thread-safe.

## Helpers
 * Handlebars.java includes the built-in helpers: 'if', 'unless', 'with', 'each', 'noop' and 'log'.
 * Handlebars.java also includes two built-in helpers: 'block' and 'partial' for doing [Template Inheritance](http://thejohnfreeman.com/blog/2012/03/23/template-inheritance-for-handlebars.html)

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

### Optional dependencies:
#### Jackson

 ```text 
  org.codehaus.jackson:jackson-mapper-asl:1.9.7
 ```

## FAQ

## Help and Support
 [Help and discussion](https://groups.google.com/forum/#!forum/handlebarsjava)

 [Bugs, Issues and Features](https://github.com/edgarespina/handlebars.java/issues)

## Credits
 * [Mathias](https://github.com/sirthias): For the [parboiled](https://github.com/sirthias/parboiled) PEG library

## License
[Apache License 2](http://www.apache.org/licenses/LICENSE-2.0.html)