angular.module('plsApp.directives').directive('plsConfirmationDialog', [function () {
    return {
        restrict: 'A',
        replace: true,
        templateUrl: 'pages/content/commons/confirmation-dialog.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.shouldBeOpen = false;
            $scope.confirmDialogOptions = {};
            $scope.closeButtonHide = false;
            $scope.confirmButtonHide = false;

            $scope.$on('event:showConfirmation', function (event, params) {
                $scope.caption = params.caption;
                $scope.message = params.message;
                $scope.okFunction = params.okFunction;
                $scope.closeFunction = params.closeFunction;
                $scope.closeButtonHide = params.closeButtonHide;
                $scope.confirmButtonHide = params.confirmButtonHide;
                $scope.confirmButtonLabel = params.confirmButtonLabel || 'OK';
                $scope.closeButtonLabel = params.closeButtonLabel || 'Cancel';
                $scope.shouldBeOpen = true;
                $scope.confirmDialogOptions.parentDialog = params.parentDlgId;
                $scope.confirmDialogOptions.showAboveLogin = params.showAboveLogin;
            });

            $scope.close = function () {
                if ($scope.closeFunction && angular.isFunction($scope.closeFunction)) {
                    $scope.closeFunction();
                }

                $scope.shouldBeOpen = false;
            };

            $scope.ok = function () {
                if ($scope.okFunction && angular.isFunction($scope.okFunction)) {
                    $scope.okFunction();
                }

                $scope.shouldBeOpen = false;
            };
        }]
    };
}]);