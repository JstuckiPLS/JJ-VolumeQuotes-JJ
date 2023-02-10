/**
 * Created by vgavriliuk on 4/26/2016.
 */

angular.scenario.dsl('getOptionValue', function() {
    return function (selector) {
        return this.addFutureAction('get selected option value from \'' + selector + '\'', function ($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(selector);
            if (!elem.length) {
                return done('No element matched \'' + selector + '\'.');
            }
            done(null, $(selector + ' option:selected').val());
        });
    };
});
