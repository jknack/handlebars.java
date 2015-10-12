Handlebars.registerHelper('compare', function(lvalue, rvalue, options) {
  if( lvalue === rvalue ) {
      return options.fn(this);
  } else {
      return options.inverse(this);
  }
});

Handlebars.registerHelper('uppercase', function(str, options) {
  if (arguments.length < 2 || str === null){
      return '';
  }
  if(str.toUpperCase){
      return str.toUpperCase();
  }
  return str;
});