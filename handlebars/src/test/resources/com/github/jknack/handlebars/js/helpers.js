Handlebars.registerHelper('simple', function() {
  return 'Long live to Js!';
});

Handlebars.registerHelper('context', function(ctx) {
  return ctx.name;
});

Handlebars.registerHelper('thisContext', function() {
  return this.name;
});

Handlebars.registerHelper('param1', function(curly, age) {
  return curly.name + ' is ' + age + ' years old';
});

Handlebars.registerHelper('params', function(context, p1, p2, p3) {
  return p1 + ', ' + p2 + ', ' + p3;
});

Handlebars.registerHelper('hash', function(context, options) {
  return options.hash.h1 + ', ' + options.hash.h2 + ', ' + options.hash.h3;
});

Handlebars.registerHelper('fn', function(context, options) {return options.fn(this);});

Handlebars.registerHelper('fnWithNewContext', function(context, options) {return options.fn({name: 'moe'});});

Handlebars.registerHelper('escapeString', function() {return '<a></a>';});

Handlebars.registerHelper('safeString', function() {return new Handlebars.SafeString('<a></a>');});

// From https://github.com/wycats/handlebars.js/blob/master/spec/helpers.js

Handlebars.registerHelper('link', function(prefix) {
  return "<a href='" + prefix + "/" + this.url + "'>" + this.text + "</a>";
});

Handlebars.registerHelper('goodbyes2', function(options) {
  var out = "";
  var byes = ["Goodbye", "goodbye", "GOODBYE"];
  for (var i = 0,j = byes.length; i < j; i++) {
    out += byes[i] + " " + options.fn(this) + "! ";
  }
  return out;
});

Handlebars.registerHelper('link2', function (prefix, options) {
    return "<a href='" + prefix + "/" + this.url + "'>" + options.fn(this) + "</a>";
});

Handlebars.registerHelper('goodbyes3', function(options) { return options.fn({text: "GOODBYE"}); });

Handlebars.registerHelper('form', function(options) {
  return "<form>" + options.fn(this) + "</form>";
});

Handlebars.registerHelper('link3', function(options) {
  return '<a href="/people/' + this.id + '">' + options.fn(this) + '</a>';
});

Handlebars.registerHelper('form2', function(context, options) {
  return "<form>" + options.fn(context) + "</form>";
});

Handlebars.registerHelper('list', function(context, options) {
  if (context.length > 0) {
    var out = "<ul>";
    for(var i = 0,j=context.length; i < j; i++) {
      out += "<li>";
      out += options.fn(context[i]);
      out += "</li>";
    }
    out += "</ul>";
    return out;
  } else {
    return "<p>" + options.inverse(this) + "</p>";
  }
});

