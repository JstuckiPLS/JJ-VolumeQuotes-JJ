/**
 * Created by vgavriliuk on 4/29/2016.
 */
angular.scenario.dsl('getValue', function () {
    return function (selector) {
        return this.addFutureAction('get text from \'' + selector + '\'', function ($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);

            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }

            done(null, $(selector).val());
        });
    };
});