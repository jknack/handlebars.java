/** handlebars springmvc. */
module com.github.jknack.handlebars.springmvc {
  exports com.github.jknack.handlebars.springmvc;

  requires com.github.jknack.handlebars;
  requires jakarta.servlet;
  requires spring.core;
  requires spring.beans;
  requires spring.context;
  requires spring.jcl;
  requires spring.webmvc;
}
