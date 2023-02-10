/**
 * Future action to get text from HTML element.
 * Selector - any jQuery selector.
 */
angular.scenario.dsl('getText', function () {
    return function (selector) {
        return this.addFutureAction('get text from \'' + selector + '\'', function ($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);

            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }

            done(null, $(selector).text().trim());
        });
    };
});