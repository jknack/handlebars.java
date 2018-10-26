Handlebars.registerHelper('and', function () {
    const len = arguments.length - 1;
    const options = arguments[len];
    let val = true;
    for (let i = 0; i < len; i++) {
		if(!val)
			break;
		val = val && arguments[i];
    }
    return val ? options.fn(context) : options.inverse(context);
});