/**
 * Controller for 'Contact Us' modal window.
 */
angular.module('plsApp').controller('ContactUsCtrl', ['$scope', '$rootScope', function ($scope, $rootScope) {
    'use strict';

    $rootScope.$on('event:openContactUs', function (event, params) {
        if (params) {
            $scope.inactiveCustomerLoginAttemptFailure = params.inactiveCustomerLoginAttemptFailure;
            $scope.showAboveLogin = params.showAboveLogin;
        } else {
            $scope.inactiveCustomerLoginAttemptFailure = false;
            $scope.showAboveLogin = false;
        }
        $scope.contactUsOpen = true;
    });

    $scope.closeContactUsDialog = function () {
        $scope.contactUsOpen = false;
        $rootScope.$broadcast('event:contactUsClosed');
    };
}]);