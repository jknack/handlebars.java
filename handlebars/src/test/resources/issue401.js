Handlebars.registerHelper('withKey', function(map, options) {
    return options.fn(map[options.hash.key] || []);
});
