angular.module('plsApp.directives').directive('plsPrepaidAmount', function () {
    return {
        restrict: 'A',
        scope: {
            prepaidDetails: '=',
            totalRevenue: '=',
            type: '@'
        },
        templateUrl: 'pages/tpl/prepaid/pls-prepaid-amount.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            if (angular.isUndefined($scope.prepaidDetails)) {
                $scope.prepaidDetails = [];
            }

            $scope.$watch('prepaidDetails', function(newValue) {
                if (newValue) {
                    $scope.prepaidAmount = 0.00;

                    _.forEach(newValue, function(value) {
                        $scope.prepaidAmount += value.amount;
                    });

                    $scope.balance = $scope.totalRevenue - $scope.prepaidAmount;
                }
            }, true);
        }]
    };
});