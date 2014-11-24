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
  <executions>
    <execution>
      <id>precompile</id>
      <phase>prepare-package</phase>
      <goals>
        <goal>precompile</goal>
      </goals>
      <configuration>
        <output>${project.build.directory}/${project.build.finalName}/js/helpers.js</output>
        <prefix>${basedir}/src/main/webapp</prefix>
        <suffix>.hbs</suffix>
        <handlebarsJsFile>/handlebars-v1.3.0.js</handlebarsJsFile>
        <minimize>false</minimize>
        <runtime></runtime>
        <amd>false</amd>
        <encoding>UTF-8</encoding>
        <templates>
          <template>mytemplateA</template>
          <template>mytemplateB</template>
        </templates>
      </configuration>
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
* runtime: Location of the ```handlebars.runtime.js``` file. Optional. Required if you want to include the ```handlebars.runtime.js``` in the final output.
* templates: The specific list of templates to process. Optional. By default all the templates will be processed.
* handlebarsJsFile: Classpath location of the handlebars.js file. Optional. Default is: ```handlebars-v1.3.0.js```. Set to: ```handlebars-v2.0.0.js``` for handlebars.js 2.x templates.

i18njs
======
Convert [Java Resource Bundles](docs.oracle.com/javase/6/docs/api/java/util/ResourceBundle.html) to JavaScript using the i18n.js API.

usage
======

```xml
<plugin>
  <groupId>com.github.jknack</groupId>
  <artifactId>handlebars-maven-plugin</artifactId>
  <version>${handlebars-version}</version>
  <executions>
    <execution>
      <id>i18njs</id>
      <phase>prepare-package</phase>
      <goals>
        <goal>i18njs</goal>
      </goals>
      <configuration>
        <output>${project.build.directory}/${project.build.finalName}/js</output>
        <bundle>messages</bundle>
        <merge>false</merge>
        <amd>false</amd>
        <encoding>UTF-8</encoding>
      </configuration>
    </execution>
  </executions>
</plugin>
```

or:

```
mvn handlebars:i18njs
```

configuration options
======

* output: The output directory where the generated JavaScript files should be saved. Optional. Default: ```${project.build.directory}/${project.build.finalName}/js```
* bundle: The bundle's name. For example: ```messages```, ```com.my.app.messages```, etc. Optional. Default: ```messages```.
* merge: True, if you want to merge all the bundles in one single file. Default: false
* amd: True for generating AMD modules.

