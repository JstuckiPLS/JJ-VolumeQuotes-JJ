angular.module('plsApp').directive('plsBillToDefaultValues', function () {
    return {
        restrict: 'A',
        scope: {
            billTo: '='
        },
        templateUrl: 'pages/content/customer/billTo/templates/billto-default-values-tpl.html',
        controller: ['$scope', 'ShipmentUtils', function ($scope, ShipmentUtils) {
            'use strict';

            if (angular.isUndefined($scope.billTo.billToDefaultValues) || _.isEmpty($scope.billTo.billToDefaultValues)) {
                $scope.billTo.billToDefaultValues = {
                    direction: 'O',
                    ediDirection: 'O',
                    manualBolDirection: 'O',
                    payTerms: 'PREPAID',
                    ediPayTerms: 'PREPAID',
                    manualBolPayTerms: 'COLLECT',
                    payMethods: 'PREPAID'
                };
            }
            $scope.billTo.billToDefaultValues.direction = $scope.billTo.billToDefaultValues.direction || 'O';
            $scope.billTo.billToDefaultValues.ediDirection = $scope.billTo.billToDefaultValues.ediDirection || 'O';
            $scope.billTo.billToDefaultValues.manualBolDirection = $scope.billTo.billToDefaultValues.manualBolDirection || 'O';
            $scope.billTo.billToDefaultValues.payTerms = $scope.billTo.billToDefaultValues.payTerms || 'PREPAID';
            $scope.billTo.billToDefaultValues.ediPayTerms = $scope.billTo.billToDefaultValues.ediPayTerms || 'PREPAID';
            $scope.billTo.billToDefaultValues.manualBolPayTerms = $scope.billTo.billToDefaultValues.manualBolPayTerms || 'COLLECT';
            $scope.billTo.billToDefaultValues.payMethods = $scope.billTo.billToDefaultValues.payMethods || 'PREPAID';
            $scope.paymentTermsValues = angular.copy(ShipmentUtils.getDictionaryValues().paymentTerms);
            if (!$scope.billTo.billToDefaultValues.ediCustomsBrokerPhone) {
                $scope.billTo.billToDefaultValues.ediCustomsBrokerPhone = {countryCode: '1'};
            } else if (!$scope.billTo.billToDefaultValues.ediCustomsBrokerPhone.countryCode) {
                $scope.billTo.billToDefaultValues.ediCustomsBrokerPhone.countryCode = '1';
            }
        }]
    };
});