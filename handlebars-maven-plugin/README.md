handlebars-maven-plugin
======

It compiles Handlebars/Mustache templates to JavaScript functions.

usage
======

```xml
<plugin>
  <groupId>com.github.jknack</groupId>
  <artifactId>handlebars-maven-plugin</artifactId>
  <version>${handlebars-version}</version>
  <configuration>
    <output>target/helpers.js</output>
    <prefix>${basedir}/src/main/webapp</prefix>
    <suffix>.hbs</suffix>
    <minimize>false</minimize>
    <includeRuntime>false</includeRuntime>
  </configuration>
  <executions>
    <execution>
      <id>precompile</id>
      <phase>generate-resources</phase>
      <goals>
        <goal>precompile</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

configuration options
======

* output: The output file to generated. Required.
* prefix: The template base directory. Default is: ```${basedir}/src/main/webapp```.
* suffix: The file extension. Default is: ```.hbs```.
* minimize: True, to minimize the output file with the google closure compiler. Default is: ```false```.
* includeRuntime: True, if you want to include the ```handlebars.runtime.js``` in the final output. Default is: ```false```.
