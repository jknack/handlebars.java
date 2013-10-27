// Source: src/test/resources/templates/a.hbs
define('a.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", functionType="function", escapeExpression=this.escapeExpression;


  buffer += "I'm template a.\nHello "
    + escapeExpression((typeof depth0 === functionType ? depth0.apply(depth0) : depth0))
    + "!";
  return buffer;
  });
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['a.hbs'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['a.hbs'] = template;
  return template;
});

// Source: src/test/resources/templates/c.hbs
define('c.hbs', ['handlebars'], function(Handlebars) {
  var template = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", functionType="function", escapeExpression=this.escapeExpression;


  buffer += "I'm template c.\nHello "
    + escapeExpression((typeof depth0 === functionType ? depth0.apply(depth0) : depth0))
    + "!";
  return buffer;
  });
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['c.hbs'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['c.hbs'] = template;
  return template;
});

