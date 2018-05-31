/**
 * The Handlebars global variable.
 */
var Handlebars = {};
/**
 * Helpers
 */
Handlebars.helpers = {};

/**
 * Creates Handlebars.SafeString
 */
Handlebars.SafeString = Java.type('com.github.jknack.handlebars.Handlebars.SafeString');

/**
 * Creates Handlebars.Utils
 */
Handlebars.Utils = Java.type('com.github.jknack.handlebars.Handlebars.Utils');
Handlebars.escapeExpression = Handlebars.Utils.escapeExpression;

/**
 * Register helper function.
 *
 * @param {String} name The helper's name. Required.
 * @param {Function} helper The helper function. Required.
 */
Handlebars.registerHelper = function (name, helper) {
  /**
   * Bridge between a Java and JavaScript helpers.
   */
  var fn = function (context, options) {
    var args = [];
    var self = options.context.model();
    var psize = options.context.data('com.github.jknack.handlebars.Context#paramSize')
    if (psize > 0) {
      args.push(context);
      for(var i = 0; i < options.params.length; i++) {
        args.push(options.params[i]);
      }
    }
    args.push(options);

    // Invoke the JavaScript helper.
    return helper.apply(self, args);
  };

  Handlebars.helpers[name] = helper;
  Handlebars_java.registerHelper(name, fn);
};
