angular.module('myProfile.controllers').controller('ViewProfileCtrl', ['$scope', 'UserDetailsService', function ($scope, UserDetailsService) {
    'use strict';

    UserDetailsService.getUser({
        personId: $scope.authData.personId
    }, function (data) {
        $scope.user = data;
        $scope.user.customers = $scope.user.customers || [];
    });

    $scope.changePassword = function () {
        $scope.$broadcast('event:changeUserPassword', $scope.authData.personId);
    };
}]);