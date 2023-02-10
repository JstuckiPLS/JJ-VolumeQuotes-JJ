/**
 * Directive that highlights the 'Effective' date field depending on data in
 * fields: 'Effective' and 'Expired' date.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsHighlightingDate', function () {
    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            effective: '=',
            expired: '='
        },
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            scope.$watch('effective', function (date) {
                if (scope.expired) {
                    scope.effDateAlreadyExists = date ? true : false;
                    ctrl.$setValidity('effective-date', scope.effDateAlreadyExists);
                }
            });

            scope.$watch('expired', function (date) {
                if (scope.effective) {
                    scope.effDateAlreadyExists = false;
                } else {
                    scope.effDateAlreadyExists = date ? true : false;
                }

                ctrl.$setValidity('effective-date', !scope.effDateAlreadyExists);
            });
        }
    };
});