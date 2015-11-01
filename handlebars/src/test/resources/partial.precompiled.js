(function() {
  var template = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['partial/child'],depth0,{"name":"partial/child","data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"usePartial":true,"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['root'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['root'] = template;
})();