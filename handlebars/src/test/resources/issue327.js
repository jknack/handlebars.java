Handlebars.registerHelper('link', function(text, url) {
  text = Handlebars.Utils.escapeExpression(text);
  url  = Handlebars.escapeExpression(url);

  var result = '<a href="' + url + '">' + text + '</a>';

  return new Handlebars.SafeString(result);
});

Handlebars.registerHelper('call-link', function(text, url) {
  var link = Handlebars.helpers['link'];
  return link.apply(this, [text, url]);
});
