
  var template = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "Hi "
    + escapeExpression(lambda(depth0, depth0))
    + "!";
},"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['input'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['input'] = template;
