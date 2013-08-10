handlebars-maven-plugin
======

A collection of maven plugin for [handlebars.js](http://handlebarsjs.com/) and [i18n.js](https://github.com/fnando/i18n-js)

precompile
======
Convert Handlebars/Mustache template to JavaScript using the handlebars.js runtime.

usage
======

```xml
<plugin>
  <groupId>com.github.jknack</groupId>
  <artifactId>handlebars-maven-plugin</artifactId>
  <version>${handlebars-version}</version>
  <configuration>
    <output>${project.build.directory}/${project.build.finalName}/js/helpers.js</output>
    <prefix>${basedir}/src/main/webapp</prefix>
    <suffix>.hbs</suffix>
    <minimize>false</minimize>
    <includeRuntime>false</includeRuntime>
  </configuration>
  <executions>
    <execution>
      <id>precompile</id>
      <phase>prepare-package</phase>
      <goals>
        <goal>precompile</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

or:

```
mvn handlebars:precompile
```

configuration options
======

* output: The output file to generated. Default: ```${project.build.directory}/${project.build.finalName}/js/helpers.js```.
* prefix: The template base directory. Default is: ```${basedir}/src/main/webapp```.
* suffix: The file extension. Default is: ```.hbs```.
* minimize: True, to minimize the output file with the google closure compiler. Default is: ```false```.
* includeRuntime: True, if you want to include the ```handlebars.runtime.js``` in the final output. Default is: ```false```.

i18n.js
======
Convert [Java Resource Bundles](docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html) to JavaScript using the i18n.js API.

usage
======

```xml
<plugin>
  <groupId>com.github.jknack</groupId>
  <artifactId>handlebars-maven-plugin</artifactId>
  <version>${handlebars-version}</version>
  <configuration>
    <output>${project.build.directory}/${project.build.finalName}/js</output>
    <bundle>messages</bundle>
    <merge>false</merge>
    <amd>false</amd>
  </configuration>
  <executions>
    <execution>
      <id>i18n.js</id>
      <phase>prepare-package</phase>
      <goals>
        <goal>i18n.js</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

or:

```
mvn handlebars:i18n.js
```

configuration options
======

* output: The output directory where the generated JavaScript files should be saved. Optional. Default: ```${project.build.directory}/${project.build.finalName}/js```
* bundle: The bundle's name. For example: ```messages```, ```com.my.app.messages```, etc. Optional. Default: ```messages```.
* merge: True, if you want to merge all the bundles in one single file. Default: false
* amd: True for generating AMD modules.

