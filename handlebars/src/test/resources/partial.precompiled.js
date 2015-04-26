(function() {
  var template = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1;
  stack1 = this.invokePartial(partials['partial/child'], '', 'partial/child', depth0, undefined, helpers, partials, data);
  if (stack1 != null) { return stack1; }
  else { return ''; }
  },"usePartial":true,"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['root'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['root'] = template;
})();