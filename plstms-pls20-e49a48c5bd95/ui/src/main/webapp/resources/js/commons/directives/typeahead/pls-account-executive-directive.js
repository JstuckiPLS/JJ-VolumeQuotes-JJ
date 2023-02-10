/**
 * AngularJS directive which displays type-ahead input for account executive search.
 */
angular.module('plsApp.directives').directive('plsAccountExecutive', ['CustomerService', function (CustomerService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsAccountExecutive: '='
        },
        replace: true,
        template: '<input type="text" data-ng-model="plsAccountExecutive" '
                + 'data-pls-typeahead="user as user.name for user in findAccountExecByKeyword($viewValue)" autocomplete="off">',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.findAccountExecByKeyword = function (criteria) {
                return CustomerService.getAccountExecutivesByFilter(criteria).then(function (response) {
                    return response.data;
                });
            };
        }]
    };
}]);