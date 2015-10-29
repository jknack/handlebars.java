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

/**
 * Wrap OptionsJs with JSON deserialization of hash and params,
 * so they become accessible as native JS object and array, respectively. 
 */
function JsOptions(javaOptionsJs) {
	this.javaOptionsJs = javaOptionsJs;

	this.params = JSON.parse(javaOptionsJs.paramsJson);
	this.hash = JSON.parse(javaOptionsJs.hashJson);

	this.fn = function(context) {
		return this.javaOptionsJs.fn(context);
	}

	this.inverse = function(context) {
		return this.javaOptionsJs.inverse(context);
	}
}

(function() {
	/**
	 * Register helper function.
	 *
	 * @param {String} name The helper's name. Required.
	 * @param {Function} helper The helper function. Required.
	 */
	Handlebars.registerHelper = function(name, helper) {
		var isUndefined = function(obj) {
			return obj === void 0;
		};

		/**
		 * Bridge between a Java and JavaScript helpers.
		 *  
		 * "Arg0" is passed in either as complexArg0Json for complex objects (Map, Array, Collection),
		 * or as simpleArg0 with the Java object itself (e.g. String, Number, boolean).
		 * 
		 * This is so that complex objects become accessible in JS as native JS objects or arrays,
		 * not Java objects where e.g. a Map requires access via get() method and a collection
		 * doesn't have a length property as usual in JS.
		 */
		var fn = function(contextProperties, arg0, javaOptionsJs) {
			var args = [];
			if (!isUndefined(arg0) && arg0 !== '___NOT_SET_') {
				args.push(arg0);
			}
			for (var i = 0; i < javaOptionsJs.params.length; i++) {
				args.push(javaOptionsJs.params[i]);
			}
			args.push(javaOptionsJs);

			// Invoke the JavaScript helper.
			return helper.apply(contextProperties, args);
		};

		Handlebars.helpers[name] = helper;
		Handlebars_java.registerHelper(name, fn);
	};

})();
