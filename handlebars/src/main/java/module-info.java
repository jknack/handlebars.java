module com.github.jknack.handlebars {
  exports com.github.jknack.handlebars;
  exports com.github.jknack.handlebars.io;
  exports com.github.jknack.handlebars.cache;
  exports com.github.jknack.handlebars.context;
  exports com.github.jknack.handlebars.helper;

  requires java.scripting;
  requires org.slf4j;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires static jakarta.servlet;
  requires static java.compiler;

  // SHADED: All content after this line will be removed at build time
  requires org.antlr.antlr4.runtime;
}
