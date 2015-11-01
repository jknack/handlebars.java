// Source: src/test/resources/templates/a.hbs
define('a.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "I'm template a.\nHello "
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "!";
},"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['a.hbs'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['a.hbs'] = template;
  return template;
});

// Source: src/test/resources/templates/c.hbs
define('c.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "I'm template c.\nHello "
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "!";
},"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['c.hbs'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['c.hbs'] = template;
  return template;
});

