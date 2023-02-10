angular.module('pls.controllers').controller('SOVendorBillsCtrl', ['$scope', 'VendorBillService', function ($scope, VendorBillService) {
    'use strict';

    function processVendorBill(data) {
        $scope.wizardData.vendorBillModel.vendorBill = data;
        if (data && data.id) {
            $scope.wizardData.vendorBillModel.detachAllowed = true;

            if ($scope.wizardData.vendorBillModel.vendorBill.costItems) {
                $scope.wizardData.vendorBillModel.totalCost =
                        _.reduce(_.pluck($scope.wizardData.vendorBillModel.vendorBill.costItems, 'subTotal'), function (subTotal, memo) {
                            return subTotal + memo;
                        }, 0);
                $scope.vendorBillCost = $scope.wizardData.vendorBillModel.totalCost;
            }
        } else {
            $scope.wizardData.vendorBillModel.totalCost = 0;
            $scope.wizardData.vendorBillModel.detachAllowed = false;
        }
    }

    function loadVendorBill() {
        VendorBillService.getForShipment({subParamId: $scope.wizardData.shipment.id}, function (data) {
            processVendorBill(data);
        }, function () {
            $scope.$root.$emit('event:application-error', 'Vendor Bill load failed!', 'Can\'t get Vendor Bill for Shipment');
        });
    }

    function vendorBillHandler(isAddEditVendorBill, isDetachVendorBill) {
        if (isAddEditVendorBill) {
            if (!$scope.wizardData.shipment.guaranteedBy && $scope.wizardData.shipment.guaranteedBy !== 0) {
                $scope.wizardData.shipment.guaranteedBy = undefined;
            }

            loadVendorBill();

            if (!isDetachVendorBill) {
                $scope.$root.$broadcast('event:showReasonMessage', {
                    shipment: $scope.wizardData.shipment,
                    parentDlgId: 'detailsDialogDiv'
                });
            }
        }
        if ($scope.editSalesOrderModel.closeHandler) {
            $scope.editSalesOrderModel.closeHandler();
        }
    }

    $scope.wizardData.vendorBillModel = {
        showControls: true,
        detachAllowed: false,
        allowChangeVednorBill: !_.isUndefined($scope.wizardData.shipment.matchedVendorBillId),
        detachVendorBill: function () {
            VendorBillService.detach({
                loadId: $scope.wizardData.shipment.id,
                loadVersion: $scope.wizardData.shipment.version
            }, function () {
                $scope.wizardData.shipment.version = $scope.wizardData.shipment.version + 1;
                vendorBillHandler(true, true);
                $scope.wizardData.shipment.isVendorBillMatched = false;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Vendor Bill detach failed!', 'Can\'t detach Vendor Bill from Shipment');
            });
        },
        addVendorBill: function () {
            $scope.wizardData.vendorBillModel.vendorBill.loadVersion = $scope.wizardData.shipment.version;

            $scope.$root.$broadcast('event:showAddEditVendorBill', {
                parentDialog: 'detailsDialogDiv', matchedLoad: $scope.wizardData.shipment,
                vendorBill: $scope.wizardData.vendorBillModel.vendorBill, closeHandler: vendorBillHandler
            });
        }
    };

    $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
        if (tabId === 'vendor_bills') {
            if (!$scope.wizardData.vendorBillModel.vendorBill && $scope.wizardData.shipment.id) {
                loadVendorBill();
            } else if (!$scope.wizardData.vendorBillModel.vendorBill && $scope.wizardData.shipment.matchedVendorBillId) {
                VendorBillService.get({vendorBillId: $scope.wizardData.shipment.matchedVendorBillId}, function (data) {
                    processVendorBill(data);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Vendor Bill load failed!',
                            'Can\'t load Vendor Bill with id: ' + $scope.wizardData.shipment.matchedVendorBillId);
                });
            }
        }
    });
}]);