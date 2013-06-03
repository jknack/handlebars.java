/**
 * The Handlebars global variable.
 */
var Handlebars = {};

/**
 * Register helper function.
 *
 * @param {String} name The helper's name. Required.
 * @param {Function} helper The helper function. Required.
 */
Handlebars.registerHelper = function (name, helper) {
  /**
   * Bridge between a Handlebars.java helper and a Handlebars.js helper.
   * 'fn' will be invoked from Java using the regular helper notation.
   */
  var fn = function (context, options) {
    var args = [context];
    for(var i = 0; i < options.params.length; i++) {
      args.push(options.params[i]);
    }
    args.push(options);
    // Invoke the JavaScript helper.
    return helper.apply(context, args);
  };

  Handlebars_java.registerHelper(name, fn);
}

/**
 * Creates Handlebars.SafeString
 */
Handlebars.SafeString = com.github.jknack.handlebars.Handlebars.SafeString;