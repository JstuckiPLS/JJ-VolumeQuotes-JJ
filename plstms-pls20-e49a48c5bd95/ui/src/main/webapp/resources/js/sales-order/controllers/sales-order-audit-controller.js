angular.module('pls.controllers').controller('SOAuditCtrl', [
    '$scope', '$filter', 'ShipmentDetailsService', 'VendorBillService','CostDetailsUtils', 'NgGridPluginFactory',
    function ($scope, $filter, ShipmentDetailsService, VendorBillService, CostDetailsUtils, NgGridPluginFactory) {
        'use strict';

        function initAccessorialsTable(pricingDetailsData) {
            $scope.accessorialsExcludingFuel = CostDetailsUtils.getAccessorialsExcludingFuel($scope.wizardData.shipment.selectedProposition, 'C',
              $scope.wizardData.shipment.selectedProposition.guaranteedBy);
            $scope.accessorials = [];
            var total = pricingDetailsData.totalChargeFromSmc3;
            if (pricingDetailsData.deficitChargeFromSmc3) {
                $scope.accessorials.push({refType: 'Deficit Charge From Smc3', subTotal: pricingDetailsData.deficitChargeFromSmc3});
                total = total - pricingDetailsData.deficitChargeFromSmc3;
            }
            $scope.accessorials.push({refType: 'Total', subTotal: total});
            _.each($scope.accessorialsExcludingFuel, function(item){
                var label = $filter('ref')(item.refType) + ' (' + item.refType + ')';
                if (item.guaranteedBy) {
                    label = label + '(by' + $filter('longTime')(item.guaranteedBy) + ')';
                }
                $scope.accessorials.push({refType: label, subTotal: item.subTotal});
            });
            var accessorialTotal = _.reduce ($scope.accessorialsExcludingFuel, function(memo, item){ 
                return memo + item.subTotal; 
            }, 0);
            if (!accessorialTotal) {
                accessorialTotal = 0;
            }
            $scope.accessorials.push({refType: 'Accessorial Total', subTotal: accessorialTotal});
            if (pricingDetailsData.smc3MinimumCharge) {
                $scope.accessorials.push({refType: 'CZAR Minimum Floor', subTotal: pricingDetailsData.smc3MinimumCharge});
            }
            $scope.basePriceFromSmc3 = pricingDetailsData.totalChargeFromSmc3;

            if (pricingDetailsData.deficitChargeFromSmc3) {
                $scope.basePriceFromSmc3 = $scope.basePriceFromSmc3 + pricingDetailsData.deficitChargeFromSmc3;
            }
            if (pricingDetailsData.smc3MinimumCharge && $scope.basePriceFromSmc3 < pricingDetailsData.smc3MinimumCharge) {
                $scope.basePriceFromSmc3 = pricingDetailsData.smc3MinimumCharge;
            }

            $scope.accessorials.push({refType: 'Total', subTotal: $scope.basePriceFromSmc3});
            if (pricingDetailsData.costDiscount) {
                $scope.accessorials.push({refType: pricingDetailsData.costDiscount + '% Discount', 
                    subTotal: $scope.basePriceFromSmc3 - pricingDetailsData.costAfterDiscount});
            }
            $scope.accessorials.push({refType: 'Total After Discount', subTotal: pricingDetailsData.costAfterDiscount});
            if (pricingDetailsData.minimumCost){
                $scope.accessorials.push({refType: 'Minimum PLS Cost', subTotal: pricingDetailsData.minimumCost});
                var baseRate = CostDetailsUtils.getBaseRate($scope.wizardData.shipment.selectedProposition, 'C');
                if (!baseRate) {
                    baseRate = 0;
                }
                $scope.accessorials.push({refType: 'Total After Applying PLS Minimum Cost', subTotal: baseRate});
            }
            var fuelSurcharge = CostDetailsUtils.getFuelSurcharge($scope.wizardData.shipment.selectedProposition, 'C');
            if (!fuelSurcharge) {
              fuelSurcharge = 0;
            }
            if (pricingDetailsData.carrierFuelDiscount) {
                $scope.accessorials.push({refType: pricingDetailsData.carrierFuelDiscount + '% Fuel Surcharge', subTotal: fuelSurcharge});
            }
        }

        function initInfoTable(pricingDetailsData) {
            $scope.infoTableData = [];
            $scope.infoTableData.push({infoLabel: 'Move Type', infoText: pricingDetailsData.movementType});
            $scope.infoTableData.push({infoLabel: 'Move ID', infoText: $scope.wizardData.shipment.selectedProposition.pricingProfileId});
            $scope.infoTableData.push({infoLabel: 'FSC ID', infoText: pricingDetailsData.carrierFSId});
            $scope.infoTableData.push({infoLabel: 'Tariff Type', infoText: pricingDetailsData.pricingType});
        }

        function processVendorBill(data) {
          if (data && data.id) {
              if (data.costItems) {
                  $scope.vendorBillCost = _.reduce(_.pluck(data.costItems, 'subTotal'), function(subTotal, memo) {
                      return subTotal + memo;
                  }, 0);
                  $scope.vendorBillWeight = _.reduce(data.lineItems, function(memo, item) {
                      var weight = 0;
                      if (item.weight) {
                          weight = item.weight;
                      }
                      return memo + parseFloat(weight);
                  }, 0);
              }
          } else {
              $scope.vendorBillCost = 0;
          }
          if (!$scope.vendorBillWeight) {
              $scope.vendorBillWeight = 0;
          }
          if (!$scope.vendorBillCost) {
              $scope.vendorBillCost = 0;
          }
      }

        function initData() {
            ShipmentDetailsService.findShipmentPricingDetails({
                customerId: $scope.wizardData.shipment.organizationId,
                shipmentId: $scope.wizardData.shipment.id
            }, function (pricingDetailsData) {
                $scope.pricingDetails = pricingDetailsData;
                $scope.totalCostDetailsWeight = _.reduce ($scope.pricingDetails.smc3CostDetails, function(memo, item){
                      return memo + parseFloat(item.weight);
                  }, 0);
                $scope.totalCost = $scope.wizardData.shipment.selectedProposition.totalCarrierAmt;
                initAccessorialsTable(pricingDetailsData);
                initInfoTable(pricingDetailsData);
            }, function () {
                $scope.$root.$emit('event:application-error', 'Shipment cost details load failed!',
                             'Can\'t load cost details with shipment ID ' + $scope.wizardData.shipment.id);
            });
            if (!$scope.wizardData.vendorBillModel.vendorBill) {
                VendorBillService.getForShipment({subParamId: $scope.wizardData.shipment.id}, function (data) {
                    $scope.wizardData.vendorBillModel.vendorBill = data;
                    processVendorBill(data);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Vendor Bill load failed!', 'Can\'t get Vendor Bill for Shipment');
                });
            } else {
                processVendorBill($scope.wizardData.vendorBillModel.vendorBill);
            }
        }

        $scope.$on('event:update-total-cost', function() {
            $scope.totalCost = $scope.wizardData.shipment.selectedProposition.totalCarrierAmt;
        });

        $scope.costDetailsGrid = {
            enableColumnResize: true,
            data: 'pricingDetails.smc3CostDetails',
            columnDefs: [{
                field: 'weight',
                displayName: 'Weight',
                width: '10%'
            }, {
                field: 'enteredNmfcClass',
                displayName: 'Entered Class',
                width: '10%'
            }, {
                field: 'nmfcClass',
                displayName: 'Rated Class',
                width: '10%'
            },  {
                field: 'description',
                displayName: 'Description',
                width: '10%'
            },  {
                field: 'nmfc',
                displayName: 'NMFC',
                width: '10%'
            },  {
                field: 'quantity',
                displayName: 'Qty',
                width: '10%'
            },  {
                field: 'rate',
                displayName: 'Base Rate',
                width: '20%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'charge',
                displayName: 'Rated Amount',
                width: '20%',
                cellFilter: 'plsCurrency'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            enableSorting: true,
            multiSelect: false,
            enableRowSelection: false
        };

        $scope.vendorBillGrid = {
            enableColumnResize: true,
            data: 'wizardData.vendorBillModel.vendorBill.lineItems',
            columnDefs: [{
                field: 'self',
                displayName: 'Weight',
                cellClass: 'text-center',
                width: '20%',
                cellFilter: 'materialWeight'
            }, {
                field: 'commodityClass',
                cellClass: 'text-center',
                displayName: 'Class',
                width: '10%',
                cellFilter: 'commodityClass'
            }, {
                field: 'productDescription',
                cellClass: 'text-center',
                displayName: 'Description',
                width: '20%'
            }, {
                field: 'nmfc',
                cellClass: 'text-center',
                displayName: 'NMFC',
                width: '20%'
            }, {
                field: 'quantity',
                cellClass: 'text-center',
                displayName: 'Qty',
                width: '10%'
            }, {
                field: 'cost',
                cellClass: 'text-center',
                cellFilter: 'plsCurrency',
                displayName: 'Cost',
                width: '20%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            enableSorting: true,
            multiSelect: false,
            enableRowSelection: false
        };


        $scope.accessorialsGrid = {
            headerRowHeight:0,
            enableColumnResize: true,
            data: 'accessorials',
            columnDefs: [{
                field: 'refType',
                displayName: '',
                width: '60%',
                cellFilter: 'ref'
            }, {
                field: 'subTotal',
                displayName: '',
                cellClass: 'text-center',
                width: '30%',
                cellFilter: 'plsCurrency'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            enableSorting: false,
            multiSelect: false,
            enableRowSelection: false
        };

        $scope.infoTableGrid = {
            headerRowHeight:0,
            enableColumnResize: true,
            data: 'infoTableData',
            columnDefs: [{
                field: 'infoLabel',
                displayName: '',
                width: '50%'
            }, {
                field: 'infoText',
                displayName: '',
                width: '49%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            enableSorting: false,
            multiSelect: false,
            enableRowSelection: false
        };

        $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
          if (tabId === 'audit' && $scope.wizardData.shipment.id) {
              initData();
          }
        });

        $scope.isDifferenceVisible = function () {
            return $scope.wizardData.vendorBillModel.vendorBill && !_.isEmpty($scope.wizardData.vendorBillModel.vendorBill.lineItems);
        };
    }
]);