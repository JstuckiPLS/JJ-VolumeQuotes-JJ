/**
 * Future action to disable location change check.
 */
angular.scenario.dsl('disableLocationChangeCheck', function() {
    return function() {
        return this.addFutureAction('disable location change check', function($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            if (!$('[data-ng-model=ignoreLocationChangeFlag]').is(':checked')) {
                $('[data-ng-model=ignoreLocationChangeFlag]').click();
            } else {
                $('[data-ng-model=ignoreLocationChangeFlag]').click();
                $('[data-ng-model=ignoreLocationChangeFlag]').click();
            }

            done();
        });
    };
});