angular.module('plsApp.directives').directive('plsBreadcrumb', function () {
    return {
        restrict: 'EA',
        scope: {
            allPages: '=',
            currentPage: '@currentPage'
        },
        replace: true,
        template: '<ul class="breadcrumb">' +
        '<li data-ng-repeat="page in allPages">' +
        '<span data-ng-if="isActive(page)" style="text-decoration: none;"><strong>{{page.title}}</strong></span>' +
        '<span data-ng-if="!isActive(page)" style="text-decoration: none;">{{page.title}}</span>' +
        '<span data-ng-if="!$last" class="divider">/</span>' +
        '</li>' +
        '</ul>',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.isActive = function (page) {
                return $scope.currentPage === page.label;
            };
        }]
    };
});