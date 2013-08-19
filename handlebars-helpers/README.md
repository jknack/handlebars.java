handlebars-helpers
======

A collection of Handlebars.java helpers, donated by the community.

[assign](https://github.com/jknack/handlebars.java/blob/master/handlebars-helpers/src/main/java/com/github/jknack/handlebars/helper/AssignHelper.java)
======
create auxiliary/temprary variables. Example:

```
{{#assign "benefitsTitle"}} benefits.{{type}}.title {{/assign}}
<span class="benefit-title"> {{i18n benefitsTitle}} </span>
```

[isEven, isOdd & stripes](https://github.com/jknack/handlebars.java/blob/master/handlebars-helpers/src/main/java/com/github/jknack/handlebars/helper/NumberHelper.java)
======
commons functions for numbers

```
{{isEven number}} // output: even

{{isEven number "row-even"}} // output: row-even
```

include
=====
This is a port of [https://github.com/wycats/handlebars.js/pull/368](https://github.com/wycats/handlebars.js/pull/368).

**NOTE**: The helper is required if you want to use handlebars.js, bc handlebars.js doesn't implement ```partials``` according to the Mustache Spec.
All the feature of the ```include``` helper are supported with plain ```partials``` in Handlebars.java.
