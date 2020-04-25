(function() {
  var template = Handlebars.template({"compiler":[8,">= 4.3.0"],"main":function(container,depth0,helpers,partials,data) {
      var stack1, lookupProperty = container.lookupProperty || function(parent, propertyName) {
        if (Object.prototype.hasOwnProperty.call(parent, propertyName)) {
          return parent[propertyName];
        }
        return undefined
      };

      return ((stack1 = container.invokePartial(lookupProperty(partials,"partial/child"),depth0,{"name":"partial/child","data":data,"helpers":helpers,"partials":partials,"decorators":container.decorators})) != null ? stack1 : "");
    },"usePartial":true,"useData":true});
  var templates = Handlebars.templates = Handlebars.templates || {};
  templates['root'] = template;
  var partials = Handlebars.partials = Handlebars.partials || {};
  partials['root'] = template;
})();
