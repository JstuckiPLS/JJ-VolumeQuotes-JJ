/**
 * AngularJS directive which displays type-ahead input for user search.
 */
angular.module('plsApp.directives').directive('plsUserSearch', ['UserSearchService', function (UserSearchService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsUserSearch: '=',
            count: '@'
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-user-search-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.findUser = function (criteria) {
                return UserSearchService.progressiveSearch({filter: criteria, count: $scope.count}).$promise;
            };
        }]
    };
}]);