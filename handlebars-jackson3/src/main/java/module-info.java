/** handlebars jackson3. */
module com.github.jknack.handlebars.jackson {
  exports com.github.jknack.handlebars.jackson;

  requires org.slf4j;
  requires com.github.jknack.handlebars;
  requires tools.jackson.core;
  requires tools.jackson.databind;
}
