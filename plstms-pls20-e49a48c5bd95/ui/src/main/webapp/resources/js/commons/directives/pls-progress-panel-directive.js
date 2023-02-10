/**
 * AngularJS directive which displays progress panel that hide page controls during AJAX actions.
 */
angular.module('plsApp.directives').directive('plsProgressPanel', function () {
    return {
        restrict: 'A',
        scope: {
            progressOptions: '=plsProgressPanel'
        },
        replace: true,
        templateUrl: 'pages/tpl/progress-panel-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.$on('event:auth-loginRequired', function () {
                //hide progress panel if login form is shown
                if ($scope.progressOptions && $scope.progressOptions.showPanel) {
                    $scope.progressOptions.showPanel = false;
                }
            });
        }]
    };
});