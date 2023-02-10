angular.module('plsApp.directives').directive('plsAuditUpdateCost', [ 
    function () {
        return {
            restrict: 'A',
            scope: {
                shipment: '=',
                vendorBillCost: '='
            },
            require: 'ngModel', 
            templateUrl: 'pages/tpl/audit-update-cost.html',
            controller: ['$scope', 'ShipmentAuditService', 'ShipmentUtils', 'CostDetailsUtils',
                function ($scope, ShipmentAuditService, ShipmentUtils, CostDetailsUtils) {
                    'use strict';

                    $scope.disputeCosts = angular.copy(ShipmentUtils.getDisputeCostOptions());
                    $scope.updateRevenues = angular.copy(ShipmentUtils.getUpdateRevenueOptions());

                    $scope.auditCostDetails = {
                        updateRevenue: {}
                    };
                    var initialInvoiceAdditionalDetails = {};
                    var newSelectedProposition;

                    if($scope.shipment.selectedProposition && $scope.shipment.selectedProposition.costDetailItems) {
                        var baseRateShiper = _.findWhere($scope.shipment.selectedProposition.costDetailItems, {refType: "SRA"});
                        if(baseRateShiper) {
                            $scope.auditCostDetails.invoiceNote = baseRateShiper.note;
                        }
                        $scope.auditCostDetails.initialInvoiceNote = $scope.auditCostDetails.invoiceNote;
                    }

                    if ($scope.shipment.id) {
                        ShipmentAuditService.getInvoiceAdditionalDetails({shipmentId: $scope.shipment.id}, function(data){
                            initialInvoiceAdditionalDetails = data;
                            // following ternary operator is required for correct validation
                            initialInvoiceAdditionalDetails.requestPaperwork = initialInvoiceAdditionalDetails.requestPaperwork ? true : false;
                            $scope.auditCostDetails.requestPaperWork = initialInvoiceAdditionalDetails.requestPaperwork;
                            $scope.auditCostDetails.disputeCost = initialInvoiceAdditionalDetails.disputeCost;
                        });
                    }

                    function resetSelectedProposition() {
                        newSelectedProposition = angular.copy($scope.shipment.selectedProposition);
                        if(!_.findWhere(newSelectedProposition.costDetailItems, {refType: "SRA"})) {
                            newSelectedProposition.costDetailItems.push({
                               refType: "SRA", 
                               costDetailOwner: "S",
                               subTotal: 0
                            });
                        }
                        if(!_.findWhere(newSelectedProposition.costDetailItems, {refType: "CRA"})) {
                            newSelectedProposition.costDetailItems.push({
                                refType: "CRA", 
                                costDetailOwner: "C",
                                subTotal: 0
                            });
                        }
                    }

                    function round(val) {
                        return val ? parseFloat(val.toFixed(2)) : 0;
                    }

                    function updateCarrierBaseRateWithCostDiff() {
                        var costDetailItem = _.findWhere(newSelectedProposition.costDetailItems, {refType: "CRA"});
                        costDetailItem.subTotal += $scope.vendorBillCost - $scope.shipment.selectedProposition.totalCarrierAmt;
                        costDetailItem.subTotal = round(costDetailItem.subTotal);
                    }

                    function calculateUpdateRevenueValue() {
                        var revenue = $scope.shipment.selectedProposition.totalShipperAmt;
                        var cost = $scope.shipment.selectedProposition.totalCarrierAmt;
                        switch ($scope.auditCostDetails.updateRevenue.id) {
                        case 'MARGIN_PERCENT':
                            var margin = revenue === 0 ? 0 : (1 - cost/revenue) * 100;
                            $scope.auditCostDetails.updateRevenue.value = round(margin);
                            break;
                        case 'MARGIN_VALUE':
                            $scope.auditCostDetails.updateRevenue.value = round(revenue - cost);
                            break;
                        case 'TOTAL_REVENUE_AMOUNT':
                            $scope.auditCostDetails.updateRevenue.value = revenue;
                            break;
                        case 'UPDATE_USING_COST_DIFF':
                            $scope.auditCostDetails.updateRevenue.value = round(revenue + $scope.vendorBillCost - cost);
                            break;
                        case 'INVOICE_WITHOUT_MARKUP':
                            $scope.auditCostDetails.updateRevenue.value = revenue;
                            break;
                        }
                    }

                    function updateShipmentCosts() {
                        var originalBaseRate = _.findWhere($scope.shipment.selectedProposition.costDetailItems, {refType: "SRA"}) || {subTotal: 0};
                        originalBaseRate = originalBaseRate.subTotal;
                        var newBaseRate = _.findWhere(newSelectedProposition.costDetailItems, {refType: "SRA"});
                        switch ($scope.auditCostDetails.updateRevenue.id) {
                        case 'MARGIN_PERCENT':
                            var margin = round($scope.auditCostDetails.updateRevenue.value);
                            var revenue;
                            if (margin === 100) {
                                revenue = 0;
                            } else {
                                revenue = $scope.vendorBillCost / (1 - margin/100);
                            }
                            newBaseRate.subTotal = originalBaseRate + round(revenue) - $scope.shipment.selectedProposition.totalShipperAmt;
                            break;
                        case 'MARGIN_VALUE':
                            var newTotalRevenue = $scope.vendorBillCost + round($scope.auditCostDetails.updateRevenue.value);
                            newBaseRate.subTotal = originalBaseRate + newTotalRevenue - $scope.shipment.selectedProposition.totalShipperAmt;
                            break;
                        case 'TOTAL_REVENUE_AMOUNT':
                            newBaseRate.subTotal = round($scope.auditCostDetails.updateRevenue.value);
                            newSelectedProposition.costDetailItems = _.filter(newSelectedProposition.costDetailItems, function (item) {
                                return item.costDetailOwner === 'C' || item.refType === "SRA";
                            });
                            break;
                        case 'UPDATE_USING_COST_DIFF':
                            newBaseRate.subTotal = originalBaseRate + $scope.vendorBillCost- $scope.shipment.selectedProposition.totalCarrierAmt;
                            break;
                        case 'INVOICE_WITHOUT_MARKUP':
                            break;
                        }
                        newBaseRate.subTotal = round(newBaseRate.subTotal);
                    }

                    function calculateTotals() {
                        $scope.totalRevenue = CostDetailsUtils.calculateCost(newSelectedProposition, $scope.shipment.guaranteedBy, 'S');
                        $scope.totalCost = CostDetailsUtils.calculateCost(newSelectedProposition, $scope.shipment.guaranteedBy, 'C');
                        $scope.absoluteMargin = round($scope.totalRevenue - $scope.totalCost);
                        $scope.relativeMargin = round($scope.totalRevenue === 0 ? 0 : (1 - $scope.totalCost/$scope.totalRevenue) * 100);
                    }

                    //init
                    resetSelectedProposition();
                    calculateTotals();

                    $scope.disputeCostChanged = function () {
                        delete $scope.auditCostDetails.updateRevenue.id;
                        delete $scope.auditCostDetails.updateRevenue.value;
                        resetSelectedProposition();
                        calculateTotals();
                        if (!$scope.auditCostDetails.disputeCost) {
                            delete $scope.auditCostDetails.disputeCost;
                        }
                    };

                    $scope.updateRevenueChangedId = function () {
                        delete $scope.auditCostDetails.disputeCost;
                        resetSelectedProposition();
                        if ($scope.auditCostDetails.updateRevenue.id) {
                            updateCarrierBaseRateWithCostDiff();
                        }
                        calculateUpdateRevenueValue();
                        updateShipmentCosts();
                        calculateTotals();
                    };

                    $scope.updateRevenueChangedValue = function () {
                        updateShipmentCosts();
                        calculateTotals();
                    };

                    $scope.sendAuditCostDetails = function(){
                        _.each(newSelectedProposition.costDetailItems, function(costDetailItem){
                            if(costDetailItem.refType === "SRA") {
                                costDetailItem.note = $scope.auditCostDetails.invoiceNote;
                            }
                        });
                        newSelectedProposition.totalShipperAmt = $scope.totalRevenue;
                        newSelectedProposition.totalCarrierAmt = $scope.totalCost;

                        ShipmentAuditService.updateCost({shipmentId: $scope.shipment.id}, {
                            proposal: newSelectedProposition,
                            costDifference: round($scope.vendorBillCost - $scope.totalCost),
                            auditShipmentCosts: {
                                updateRevenue: $scope.auditCostDetails.updateRevenue.id,
                                updateRevenueValue: $scope.auditCostDetails.updateRevenue.value,
                                disputeCost: $scope.auditCostDetails.disputeCost,
                                requestPaperwork: $scope.auditCostDetails.requestPaperWork
                            },
                            note: $scope.auditCostDetails.plsNote,
                            requestPaperWork: $scope.auditCostDetails.requestPaperWork
                        }, function (data) {
                            $scope.shipment.version = data.loadVersion;
                            $scope.shipment.selectedProposition = newSelectedProposition;
                            $scope.shipment.selectedProposition.id = data.activeCostDetailId;
                            $scope.$root.$broadcast('event:clear-tracking-shipmentTrackingGridData');
                            $scope.$root.$broadcast('event:note-tab-fetchNotesList');
                            $scope.$root.$broadcast('event:update-total-cost');
                            $scope.auditCostDetails.plsNote = '';
                            initialInvoiceAdditionalDetails.requestPaperwork = $scope.auditCostDetails.requestPaperWork;
                            initialInvoiceAdditionalDetails.disputeCost = $scope.auditCostDetails.disputeCost;
                            delete $scope.auditCostDetails.updateRevenue.id;
                            delete $scope.auditCostDetails.updateRevenue.value;
                        });
                    };

                    function isRequestPaperWorkChanged(){
                        return $scope.auditCostDetails.requestPaperWork !== initialInvoiceAdditionalDetails.requestPaperwork;
                    }

                    function isDisputeCostChanged(){
                        return $scope.auditCostDetails.disputeCost !== initialInvoiceAdditionalDetails.disputeCost;
                    }

                    $scope.isSubmitDisabled = function(){
                        return !$scope.auditCostDetails.updateRevenue.id && !isDisputeCostChanged() && !isRequestPaperWorkChanged();
                    };

                    $scope.isFormDisabled = function() {
                        return _.indexOf(['PRICING_AUDIT_HOLD', 'ACCOUNTING_BILLING_HOLD'], $scope.shipment.finalizationStatus) === -1 
                                || !$scope.shipment.isVendorBillMatched
                                || !$scope.shipment.selectedProposition.costDetailItems.length
                                || !$scope.$root.isPlsPermissions('CAN_EDIT_AUDIT_TAB_FOR_DISPUTE_AND_REVENUE_UPDATE');
                    };
                }
        ]
        };
    }
]);