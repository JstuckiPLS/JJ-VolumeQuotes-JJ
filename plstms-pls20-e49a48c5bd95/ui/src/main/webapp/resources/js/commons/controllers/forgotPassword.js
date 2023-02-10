/**
 * Controller for 'Forgot Password' modal window.
 */
angular.module('plsApp').controller('ForgotPasswordCtrl', ['$scope', '$rootScope', 'urlConfig', 'PlsAuthService',
    function ($scope, $rootScope, urlConfig, PlsAuthService) {
        'use strict';

        $scope.forgotPasswordOpen = false;
        $rootScope.$on('event:forgotPasswordOpen', function (event, params) {
            if (params === undefined || params.userId === undefined || params.userId === '') {
                $scope.userIdHide = false;
            } else {
                $scope.login = params.userId;
                $scope.userIdHide = true;
            }
            $scope.forgotPasswordOpen = true;
        });

        $scope.closeForgotPasswordDialog = function () {
            $scope.forgotPasswordOpen = false;
            $scope.login = '';
        };

        $scope.resetPassword = function () {
            PlsAuthService.resetPassword({
                login: $scope.login
            }, function (data) {
                $rootScope.$broadcast('event:showConfirmation', {
                    caption: 'Reset Password Completed',
                    closeButtonHide: true,
                    message: "Password is successfully reset. New Password sent to User's email",
                    showAboveLogin: true
                });
            }, function (response) {
                $rootScope.$broadcast('event:showConfirmation', {
                    caption: 'Reset Password Error',
                    closeButtonHide: true,
                    message: response.data.message,
                    showAboveLogin: true
                });
            });

            $scope.forgotPasswordOpen = false;
            $scope.login = '';
        };
    }
]);