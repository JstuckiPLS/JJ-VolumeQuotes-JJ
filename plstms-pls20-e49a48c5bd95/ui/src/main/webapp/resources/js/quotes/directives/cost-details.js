angular.module('plsApp').directive('plsCostDetails', ['CostDetailsUtils', function (CostDetailsUtils) {
    return {
        restrict: 'A',
        scope: {
            shipment: '=',
            selectedProposition: '='
        },
        replace: true,
        templateUrl: 'pages/tpl/cost-details-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';
            $scope.getBaseRate = CostDetailsUtils.getBaseRate;
            $scope.getAccessorialsRefType = CostDetailsUtils.getAccessorialsRefType;
            $scope.getGuranteedBy = CostDetailsUtils.getGuranteedBy;
            $scope.getNote = CostDetailsUtils.getNote;
            $scope.getItemCost = CostDetailsUtils.getItemCost;
            $scope.getTotalCost = CostDetailsUtils.getTotalCost;
            $scope.getCarrierTotalCost = CostDetailsUtils.getCarrierTotalCost;
            $scope.calculateCost = CostDetailsUtils.calculateCost;
            
            $scope.authData = $scope.$parent.authData;

            $scope.model = {showCostDetails: false};

            $scope.getMostSuitableGuaranteed = function(owner) {
                var guaranteed = CostDetailsUtils.getMostSuitableGuaranteed($scope.selectedProposition.costDetailItems, owner,
                        $scope.shipment.guaranteedBy);
                return guaranteed ? guaranteed.subTotal : undefined;
            };

            function showItemByPermissions() {
                return $scope.$root.isPlsPermissions('EDIT_PLS_REVENUE || EDIT_CARRIER_COST || VIEW_PLS_REVENUE_AND_CARRIER_COST');
            }

            $scope.isBenchmarkCost = function () {
                return $scope.benchmarkPresent && showItemByPermissions();
            };

            $scope.isRevenueAndCost = function () {
                return showItemByPermissions();
            };

            $scope.isRevenueOnly = function () {
                return !showItemByPermissions() && $scope.$root.isPlsPermissions('VIEW_PLS_CUSTOMER_COST || VIEW_PLS_CUSTOMER_COST_DETAILS');
            };

            $scope.isCostDetailsVisible = function () {
                return showItemByPermissions() || $scope.$root.isPlsPermissions('VIEW_PLS_CUSTOMER_COST_DETAILS');
            };

            $scope.$watch('selectedProposition', function (newVal) {
                $scope.benchmarkPresent = newVal && CostDetailsUtils.getBaseRate(newVal, 'B') !== undefined;
            });
        }]
    };
}]);
