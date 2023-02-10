/**
 * Freight Bill Pay To - directive
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives').directive('plsFreightBillPayTo', [function () {
    return {
        restrict: 'A',
        templateUrl: 'pages/tpl/freight-bill-pay-to/pls-freight-bill-pay-to-tpl.html',
        scope: {
            isManualBol: '@',
            selectedCustomer: '=',
            shipment: '='
        },
        controller: ['$scope', '$filter', 'ShipmentsProposalService', 'FreightBillService',
            function ($scope, $filter, ShipmentsProposalService, FreightBillService) {
                'use strict';

                var customerDefaultFreightBill;
                $scope.defaultFreightBill = undefined;

                function isCorrect(freightBillPayTo) {
                    return freightBillPayTo && freightBillPayTo.company;
                }

                if (!$scope.isManualBol) {
                    ShipmentsProposalService.getFreightBillPayTo({}, function (data) {
                        if (isCorrect(data)) {
                            $scope.defaultFreightBill = data;
                            if (!customerDefaultFreightBill) {
                                customerDefaultFreightBill = angular.copy($scope.defaultFreightBill);
                            }
                        }
                    });
                }
                function setCustomerDefaultFreightBill(data) {
                    customerDefaultFreightBill = isCorrect(data) ? data : undefined;
                    if ($scope.isManualBol) {
                        $scope.defaultFreightBill = angular.copy(customerDefaultFreightBill);
                    } else if (!isCorrect(customerDefaultFreightBill)) {
                        customerDefaultFreightBill = angular.copy($scope.defaultFreightBill);
                    }
                }
                if ($scope.selectedCustomer && $scope.selectedCustomer.id) {
                    FreightBillService.getDefault({customerId: $scope.selectedCustomer.id}, function (data) {
                        setCustomerDefaultFreightBill(data);
                        if (!isCorrect($scope.shipment.freightBillPayTo) && $scope.selectedCustomer && $scope.selectedCustomer.id) {
                            $scope.shipment.freightBillPayTo = angular.copy(customerDefaultFreightBill);
                        }
                    });
                }

                function getFreightBillFromPricing(pricingProfileId) {
                    ShipmentsProposalService.getFreightBillPayTo({pricingProfileId: pricingProfileId}).$promise.then(function (data) {
                        if (isCorrect(data)) {
                            $scope.shipment.freightBillPayTo = angular.copy(data);
                        } else {
                            $scope.shipment.freightBillPayTo = angular.copy(customerDefaultFreightBill);
                        }
                    });
                }

                $scope.$watch('shipment.selectedProposition.pricingProfileId', function (newValue, oldValue) {
                    if (newValue && (newValue !== oldValue || !isCorrect($scope.shipment.freightBillPayTo))) {
                        getFreightBillFromPricing(newValue);
                    }
                });

                $scope.previousCustomer = undefined;
                $scope.$watch('selectedCustomer.id', function (newValue, oldValue) {
                    if (newValue && (newValue !== $scope.previousCustomer || !isCorrect($scope.shipment.freightBillPayTo))) {
                        var forceUpdateShipment = $scope.previousCustomer && newValue !== $scope.previousCustomer;
                        FreightBillService.getDefault({customerId: newValue}, function (data) {
                            setCustomerDefaultFreightBill(data);
                            if (forceUpdateShipment || !isCorrect($scope.shipment.freightBillPayTo)) {
                                $scope.shipment.freightBillPayTo = customerDefaultFreightBill ? angular.copy(customerDefaultFreightBill) : undefined;
                            }
                        });
                        $scope.previousCustomer = newValue;
                    }
                });

                if ($scope.$root.authData.assignedOrganization && $scope.$root.authData.assignedOrganization.orgId) {
                    $scope.$on('event:pls-clear-form-data', function() {
                        setTimeout(function() {
                            $scope.shipment.freightBillPayTo = angular.copy(customerDefaultFreightBill);
                            if ($scope.$root.$$phase !== '$apply' && $scope.$root.$$phase !== '$digest') {
                                $scope.$apply();
                            }
                        });
                    });
                }
            }
        ]
    };
}]);