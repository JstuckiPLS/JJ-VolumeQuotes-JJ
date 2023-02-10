/**
 * Created by vgavriliuk on 4/25/2016.
 */
angular.scenario.dsl('isElementVisible', function() {
    return function (selector) {
        return this.addFutureAction('element visibility \'' + selector + '\'', function ($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var isVisible = $(selector + ':visible').length ? true : false;
            done(null, isVisible);
        });
    };
});
