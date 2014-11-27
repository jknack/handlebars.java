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
Handlebars.SafeString = com.github.jknack.handlebars.Handlebars.SafeString;

/**
 * Creates Handlebars.Utils
 */
Handlebars.Utils = com.github.jknack.handlebars.Handlebars.Utils;
Handlebars.escapeExpression = Handlebars.Utils.escapeExpression;

(function () {
  /**
   * Register helper function.
   *
   * @param {String} name The helper's name. Required.
   * @param {Function} helper The helper function. Required.
   */
  Handlebars.registerHelper = function (name, helper) {
    var isUndefined = function (obj) {
      return obj === void 0;
    };
  
    /**
     * Bridge between a Java and JavaScript helpers.
     */
    var fn = function (context, arg0, options) {
      var args = [];
      if (!isUndefined(arg0) && arg0 !== '___NOT_SET_') {
        args.push(arg0);
      }
      for(var i = 0; i < options.params.length; i++) {
        args.push(options.params[i]);
      }
      args.push(options);
  
      // Invoke the JavaScript helper.
      return helper.apply(context, args);
    };
  
    Handlebars.helpers[name] = helper;
    Handlebars_java.registerHelper(name, fn);
  };

})();
