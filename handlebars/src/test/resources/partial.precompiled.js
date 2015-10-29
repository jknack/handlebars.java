(function() {
  var template = Handlebars.template({"compiler":[7,">= 4.0.0"],"main":function(container,depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = container.invokePartial(partials["partial/child"],depth0,{"name":"partial/child","data":data,"helpers":helpers,"partials":partials,"decorators":container.decorators})) != null ? stack1 : "");
},"usePartial":true,"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['root'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['root'] = template;
})();