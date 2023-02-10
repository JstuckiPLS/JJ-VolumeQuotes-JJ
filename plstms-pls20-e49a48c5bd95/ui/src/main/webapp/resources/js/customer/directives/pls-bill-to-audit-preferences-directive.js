angular.module('plsApp').directive('plsBillToAuditPreferences', function () {
    return {
        restrict: 'A',
        scope: {
            billTo: '='
        },
        replace: true,
        templateUrl: 'pages/content/customer/billTo/templates/billto-audit-preferences-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';
            
            if (!$scope.billTo.billToThresholdSettings) {
                $scope.billTo.billToThresholdSettings = {};
            }

            if (!$scope.billTo.billToThresholdSettings.costDifference) {
                $scope.billTo.billToThresholdSettings.costDifference = '4.99';
            }
            
            if (!$scope.billTo.billToThresholdSettings.totalRevenue) {
                $scope.billTo.billToThresholdSettings.totalRevenue = '1500';
            }
            
            if (!$scope.billTo.billToThresholdSettings.margin) {
                $scope.billTo.billToThresholdSettings.margin = '70';
            }
        }]
    };
});