// Source: src/test/resources/templates/a.hbs
define('a.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "I'm template a.\nHello "
    + container.escapeExpression(container.lambda(depth0, depth0))
    + "!";
},"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['a'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['a'] = template;
  return template;
});

// Source: src/test/resources/templates/c.hbs
define('c.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    return "I'm template c.\nHello "
    + container.escapeExpression(container.lambda(depth0, depth0))
    + "!";
},"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['c'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['c'] = template;
  return template;
});

