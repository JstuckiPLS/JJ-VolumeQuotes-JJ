angular.module('plsApp.directives').directive('plsNotification', ['$timeout', 'AddressService', function ($timeout, AddressService) {
    return {
        restrict: 'A',
        scope: {
            parentDialog: '=',
            dialogOpen: '=',
            userId: '=',
            customerId: '=',
            title: '=',
            message: '=',
            onCancel: '=',
            hideContacts: '='
        },
        replace: true,
        templateUrl: 'pages/content/quotes/pickup-message.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.pickupMessageModel = {
                pickupMessageModalOptions: {
                    parentDialog: $scope.parentDialog
                },
                contactInfo: undefined
            };

            $scope.closePickupMessageDialog = function () {
                $scope.dialogOpen = false;

                //on cancel is optional and must be checked before calling
                if ($scope.onCancel && angular.isFunction($scope.onCancel)) {
                    $timeout(function () {
                        $scope.onCancel();
                    });
                }
            };

            $scope.$watch('dialogOpen', function (newValue) {
                if (newValue && !$scope.hideContacts) {
                    AddressService.findContactSetInfo({customerId: $scope.customerId}, function (data) {
                        $scope.pickupMessageModel.contactInfo = data;
                    });
                }
            });
        }]
    };
}]);
