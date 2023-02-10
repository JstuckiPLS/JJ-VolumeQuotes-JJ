/**
 * AngularJS directive which displays type-ahead input for scac search.
 */
angular.module('plsApp.directives').directive('plsScacSearch', ['ScacService', function (ScacService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsScacSearch: '=',
            count: '=',
            scacDisabled: '='
        },
        replace: true,
        template: '<input type="text" data-ng-model="plsScacSearch" autocomplete="off" data-pls-scac-required data-typeahead-auto-select="true" ' +
        'data-ng-disabled="scacDisabled" data-pls-typeahead="scac as scac.name for scac in findScac($viewValue, count)">',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.findScac = function (criteria, count) {
                return ScacService.findScac(criteria, count);
            };
        }]
    };
}]);

angular.module('plsApp.directives').directive('plsScacRequired', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            if (attrs.plsScacSearch === 'true') {
                var validator = function (viewValue) {
                    ctrl.$setValidity('required', viewValue && viewValue.id);
                    ctrl.$setValidity('mistyped', viewValue && viewValue.id);
                    return viewValue;
                };
                ctrl.$parsers.push(validator);
                ctrl.$formatters.push(validator);
            }
        }
    };
});