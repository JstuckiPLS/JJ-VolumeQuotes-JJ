/**
 * AngularJS directive which displays quote pricing information.
 *
 * @author: Alexander Kirichenko
 * Date: 5/10/13
 * Time: 3:57 PM
 */
angular.module('plsApp').directive('plsQuotePriceInfo', ['CostDetailsUtils', 'QuotePermissionsService',
    function (CostDetailsUtils, QuotePermissionsService) {
    return {
        restrict: 'A',
        scope: {
            shipment: '=plsQuotePriceInfo',
            showAccessorialsDetails: '=',
            hideDiscount: '@'
        },
        templateUrl: 'pages/tpl/quote-price-info-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.showCostDetails = QuotePermissionsService.showCostDetails;
            $scope.showCarrierCost = QuotePermissionsService.showCarrierCost;
            $scope.showCustomerRevenue = QuotePermissionsService.showCustomerRevenue;

            $scope.authData = $scope.$parent.authData;
            $scope.showAdditionalAccessorials = $scope.showAccessorialsDetails || false;
            $scope.visibility = false;

            function isCostUndefined() {
                return !$scope.shipment || $scope.shipment === 'null'
                        || !$scope.shipment.selectedProposition || $scope.shipment.selectedProposition === 'null'
                        || !$scope.shipment.selectedProposition.costDetailItems || $scope.shipment.selectedProposition.costDetailItems === 'null';
            }

            $scope.getCarrierBaseRate = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                // carrier base rate
                var costDetailItem = _.find($scope.shipment.selectedProposition.costDetailItems, function (item) {
                    return item.refType === 'CRA' && item.costDetailOwner === 'C';
                });

                return costDetailItem ? costDetailItem.subTotal : 0;
            };

            $scope.getBaseRate = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                // shipper base rate
                var costDetailItem = _.find($scope.shipment.selectedProposition.costDetailItems, function (item) {
                    return item.refType === 'SRA' && item.costDetailOwner === 'S';
                });

                return costDetailItem ? costDetailItem.subTotal : 0;
            };

            $scope.getCarrierSelectedGuaranteed = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getSimilarCostDetailItem($scope.shipment.selectedProposition,
                        CostDetailsUtils.getSelectedGuaranteed($scope.shipment.selectedProposition, $scope.shipment.guaranteedBy));
            };

            $scope.getSelectedGuaranteed = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getSelectedGuaranteed($scope.shipment.selectedProposition, $scope.shipment.guaranteedBy);
            };

            $scope.getSelectedGuaranteedBy = function () {
                if (isCostUndefined()) {
                    return $scope.shipment.guaranteedBy;
                }

                var guaranteed = CostDetailsUtils.getSelectedGuaranteed($scope.shipment.selectedProposition, $scope.shipment.guaranteedBy);
                return guaranteed && guaranteed.guaranteedBy >= 0 ? guaranteed.guaranteedBy : $scope.shipment.guaranteedBy;
            };

            $scope.getCarrierFuelSurcharge = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getFuelSurcharge($scope.shipment.selectedProposition, 'C') || 0;
            };

            $scope.getFuelSurcharge = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getFuelSurcharge($scope.shipment.selectedProposition, 'S') || 0;
            };

            $scope.getCarrierAccessorials = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getAccessorialsCost($scope.shipment.selectedProposition, 'C');
            };

            $scope.getAccessorials = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getAccessorialsCost($scope.shipment.selectedProposition, 'S');
            };

            $scope.getCarrierTotalCost = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getCarrierTotalCost($scope.shipment.selectedProposition, $scope.shipment.guaranteedBy);
            };

            $scope.getTotalCost = function () {
                if (isCostUndefined()) {
                    return 0;
                }

                return CostDetailsUtils.getTotalCost($scope.shipment.selectedProposition, $scope.shipment.guaranteedBy);
            };

            $scope.setVisibleAccessorialsDetails = function () {
                $scope.visibility = !$scope.visibility;
                $scope.$root.$broadcast('event:accessorialsDetails');
            };
        }]
    };
}]);