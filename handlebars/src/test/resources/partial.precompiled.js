(function() {
  var template = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); partials = this.merge(partials, Handlebars.partials); data = data || {};
  var stack1, self=this;


  stack1 = self.invokePartial(partials['partial/child'], 'partial/child', depth0, helpers, partials, data);
  if(stack1 || stack1 === 0) { return stack1; }
  else { return ''; }
  });
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['root'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['root'] = template;
})();