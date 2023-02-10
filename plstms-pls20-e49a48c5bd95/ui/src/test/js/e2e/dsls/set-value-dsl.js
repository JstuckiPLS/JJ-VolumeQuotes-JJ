/**
 * Future action to emulate setting value to HTML input.
 * Selector - any jQuery selector.
 * Value - value to be set.
 */
angular.scenario.dsl('setValue', function() {
    return function(selector, value) {
        return this.addFutureAction('set value for \'' + selector + '\'', function($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);

            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }

            elem.val(value);

            elem.trigger('input');
            elem.trigger('select');
            elem.trigger('change');

            done();
        });
    };
});