Handlebars.registerHelper('link', function(text, url) {
  text = Handlebars.Utils.escapeExpression(text);
  url  = Handlebars.escapeExpression(url);

  var result = '<a href="' + url + '">' + text + '</a>';

  return new Handlebars.SafeString(result);
});

Handlebars.registerHelper('if-helper', function(context, options) {
  var ifhelper = Handlebars.helpers['if'];
  java.lang.System.out.println(ifhelper);

  return ifhelper.apply(arguments);
});
