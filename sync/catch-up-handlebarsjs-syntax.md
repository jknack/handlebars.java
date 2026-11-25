# Catch up with handlebars.js syntax

## Behavior change

A helper that calls `options.data(k, v)` is now visible only to its downstream contexts. Before, it was visible everywhere.
