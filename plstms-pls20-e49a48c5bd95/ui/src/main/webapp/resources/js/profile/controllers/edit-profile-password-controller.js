angular.module('myProfile.controllers').controller('EditProfilePasswordCtrl', ['$scope', 'UserDetailsService', 'UserMgmtAlterPasswordService',
    function ($scope, UserDetailsService, UserMgmtAlterPasswordService) {
        'use strict';

        $scope.userModel = {
            credentials: {},
            editUserPasswordDialogModalOptions: {
                show: false,
                backdrop: 'static'
            },
            currentPassword: '',
            repeatPasswordValidationFailed: true
        };

        $scope.$on('event:changeUserPassword', function () {
            $scope.userModel.credentials = {};
            $scope.userModel.editUserPasswordDialogModalOptions.show = true;
        });

        $scope.closeEditUserPasswordDialog = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.userModel.editUserPasswordDialogModalOptions.show = false;
        };

        $scope.savePasswordDialog = function () {
            UserMgmtAlterPasswordService.changePassword({}, $scope.userModel.credentials, function () {
                $scope.errorMessage = undefined;
                $scope.$root.$emit('event:operation-success', "Success",
                        "Password has been changed successfully. You should login with a new password.");

                UserDetailsService.getUser({personId: $scope.authData.personId}, function () {
                    //should not be successful, but if yes move to login
                    $scope.$root.$broadcast('event:auth-loginRequired');
                });

                $scope.closeEditUserPasswordDialog();
            }, function (error) {
                $scope.errorMessage = '';

                if (error.data.exceptionClassName === 'PasswordsDoNotMatchException') {
                    $scope.errorMessage = 'Failed to change password. Current password doesn\'t match.';
                } else {
                    $scope.errorMessage = 'Failed to change password. New password doesn\'t match pattern.';
                }

                $scope.$root.$emit('event:application-error', "Failure", $scope.errorMessage);
                $scope.closeEditUserPasswordDialog();
            });
        };
    }
]);