angular.module('pls.controllers').controller('AddEditAdjustmentCtrl', ['$scope', 'ShipmentUtils', 'AddressService', 'NgGridPluginFactory',
                                                                       function ($scope, ShipmentUtils, AddressService, NgGridPluginFactory) {
    'use strict';

    $scope.addEditAdjustmentDialogOptions = {
        parentDialog: 'detailsDialogDiv'
    };
    $scope.adjastmentDialogDiv = 'addEditAdjustmentDialog';

    $scope.isEdit = false;
    $scope.invalidIdentifier = {};

    $scope.rateQuoteDictionary = ShipmentUtils.getDictionaryValues();

    /* prepare all accessorial types */
    //TODO move this logic to ShipmentUtils
    var allAccessorialTypes = _.reject($scope.$root.accessorialTypes, function (accessorialType) {
        return accessorialType.id === 'SRA';
    });
    allAccessorialTypes = _.sortBy(allAccessorialTypes, 'id');
    allAccessorialTypes = _.flatten([{id: 'SRA', description: 'Base Rate', status: 'ACTIVE'}, allAccessorialTypes], true);

    AddressService.listUserBillToAddresses({
        customerId: $scope.wizardData.shipment.organizationId,
        currency: $scope.wizardData.shipment.selectedProposition.carrier.currencyCode
    }, function (data) {
        if (data && data.length) {
            var filteredData = _.filter(data, function(item){
                return item.invoicePreferences.invoiceType !== "CBI";
            });
            $scope.billToList = _.sortBy(filteredData, function (billTo) {
                return billTo.address.addressName;
            });
        }
    });

    $scope.cancelEditAdjustment = function () {
        $scope.addEditAdjustmentDialogVisible = false;
    };

    function resetDictionaries() {
        if ($scope.isEdit) {
            $scope.accessorialTypes = _.where(allAccessorialTypes, {id : $scope.adjustmentModel.refType});
        } else {
            var usedAccessorialTypes = _.pluck($scope.wizardData.nonInvoicedAdjustmentAccessorials, 'refType');
            $scope.accessorialTypes = _.filter(allAccessorialTypes, function (item) {
                return item && _.indexOf(usedAccessorialTypes, item.id) === -1 && item.status === 'ACTIVE';
            });
        }

        if (!$scope.finReasons) {
            $scope.finReasons = angular.copy($scope.$root.financialReasons);
            var index = _.findIndex($scope.finReasons, function(reason){return reason.value === ShipmentUtils.OUTDATED_REBILL_REASON;});
            $scope.finReasons.splice(index, 1);
            if ($scope.wizardData.shipment.billTo.invoicePreferences.invoiceType === "CBI") {
                index = _.findIndex($scope.finReasons, function(reason){return reason.value === ShipmentUtils.REBILL_SHIPPER_REASON;});
                $scope.finReasons.splice(index, 1);
            }
        }
    }

    function generateCostDetails() {
        var result = {};

        _.each($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
            if (item.costDetailOwner === 'S' || item.costDetailOwner === 'C') {
                var refType = item.refType;

                if (refType === 'CRA') {
                    refType = 'SRA';
                }

                var field = item.costDetailOwner === 'S' ? 'revenue' : 'cost';

                if (!result[refType]) {
                    result[refType] = {};
                }

                result[refType][field] = item.subTotal;
            }
        });
        if ($scope.wizardData.shipment.adjustments && !_.isEmpty($scope.wizardData.shipment.adjustments.costItems)) {
            _.each($scope.wizardData.shipment.adjustments.costItems, function (adjustment) {
                if (adjustment.invoiceDate) {
                    if (!result[adjustment.refType]) {
                        result[adjustment.refType] = {
                            revenue: 0,
                            cost: 0
                        };
                    }
                    result[adjustment.refType].revenue += adjustment.revenue;
                    result[adjustment.refType].cost += adjustment.cost;
                }
            });
        }

        return _.map(result, function(value, key) {
            value.refType = key;
            value.reason = $scope.adjustmentModel.reason;
            value.billToName = $scope.wizardData.shipment.billTo.address.addressName;
            value.notInvoice = true;
            value.revenue = value.revenue || 0;
            value.cost = value.cost || 0;
            return value;
        });
    }

    function setDataForRebillAdjustment() {
        if (ShipmentUtils.isRebillShipperAdjustment($scope.adjustmentModel)) {
            var costItems;
            if ($scope.isEdit) {
                costItems = _.where($scope.wizardData.nonInvoicedAdjustmentAccessorials, {notInvoice: false});
            } else {
                costItems = generateCostDetails();
            }
            var shipment = $scope.wizardData.shipment;
            $scope.adjustmentLoadInfo = {
                bolNumber: shipment.bolNumber,
                billTo: _.findWhere($scope.billToList, {id: shipment.billTo.id}),
                finishOrder: {
                    poNumber: shipment.finishOrder.poNumber,
                    soNumber: shipment.finishOrder.soNumber,
                    ref: shipment.finishOrder.ref,
                    quoteMaterials: angular.copy(shipment.finishOrder.quoteMaterials)
                },
                costItems: costItems
            };
        }
    }

    function setDataForWrongCarrierAdjustment() {
        if ($scope.isWrongCarrier()) {
            $scope.adjustmentLoadInfo = {
                carrier: {name: $scope.adjustmentModel.carrierName}
            };
        }
    }

    $scope.$on('event:showAddEditAdjustment', function (event, indexOfEditedAdjustment) {
        $scope.isEdit = !_.isUndefined(indexOfEditedAdjustment);
        if ($scope.isEdit) {
            $scope.indexOfEditedAdjustment = indexOfEditedAdjustment;
            $scope.adjustmentModel = angular.copy($scope.wizardData.nonInvoicedAdjustmentAccessorials[indexOfEditedAdjustment]);
            setDataForRebillAdjustment();
            setDataForWrongCarrierAdjustment();
        } else {
            $scope.adjustmentModel = {
                notInvoice: true,
                revenue: 0,
                cost: 0,
                billToName: $scope.wizardData.shipment.billTo.address.addressName
            };
        }
        resetDictionaries();
        $scope.addEditAdjustmentDialogVisible = true;
    });

    function changeSign(adjustments) {
        _.each(adjustments, function(adj) {
            if (adj.cost) {
                adj.cost = -adj.cost;
            }
            if (adj.revenue) {
                adj.revenue = -adj.revenue;
            }
        });
        return adjustments;
    }

    function initLoadInfo() {
        if (!$scope.wizardData.shipment.adjustments) {
            $scope.wizardData.shipment.adjustments = {};
        }
        if (!$scope.wizardData.shipment.adjustments.loadInfo) {
            $scope.wizardData.shipment.adjustments.loadInfo = {};
        }
    }

    function saveBackupDataFromShipment(){
        initLoadInfo();
        var loadInfo = $scope.wizardData.shipment.adjustments.loadInfo;
        loadInfo.bolNumber = $scope.wizardData.shipment.bolNumber;
        loadInfo.poNumber = $scope.wizardData.shipment.finishOrder.poNumber;
        loadInfo.soNumber = $scope.wizardData.shipment.finishOrder.soNumber;
        loadInfo.refNumber = $scope.wizardData.shipment.finishOrder.ref;
        loadInfo.billToId = $scope.wizardData.shipment.billTo.id;
        $scope.wizardData.shipment.adjustments.products = $scope.wizardData.shipment.finishOrder.quoteMaterials;
    }

    function setNewDataToShipment() {
        $scope.wizardData.shipment.bolNumber = $scope.adjustmentLoadInfo.bolNumber;
        $scope.wizardData.shipment.finishOrder.poNumber = $scope.adjustmentLoadInfo.finishOrder.poNumber;
        $scope.wizardData.shipment.finishOrder.soNumber = $scope.adjustmentLoadInfo.finishOrder.soNumber;
        $scope.wizardData.shipment.finishOrder.ref = $scope.adjustmentLoadInfo.finishOrder.ref;
        $scope.wizardData.shipment.billTo = $scope.adjustmentLoadInfo.billTo;
        $scope.wizardData.shipment.finishOrder.quoteMaterials = $scope.adjustmentLoadInfo.finishOrder.quoteMaterials;

        $scope.wizardData.newLoadInfo = {
            billToId: $scope.wizardData.shipment.billTo.id,
            bolNumber: $scope.wizardData.shipment.bolNumber,
            poNumber: $scope.wizardData.shipment.finishOrder.poNumber,
            refNumber: $scope.wizardData.shipment.finishOrder.ref,
            soNumber: $scope.wizardData.shipment.finishOrder.soNumber
        };
        $scope.wizardData.newProducts = $scope.wizardData.shipment.finishOrder.quoteMaterials;
    }

    function setNewCosts(previousPositiveAdj) {
        _.each($scope.adjustmentLoadInfo.costItems, function(costItem){
            costItem.notInvoice = false;
            costItem.cost = 0;
            costItem.reason = $scope.adjustmentModel.reason;
            costItem.billToName = $scope.wizardData.shipment.billTo.address.addressName;
            if (previousPositiveAdj && previousPositiveAdj.financialAccessorialsId && previousPositiveAdj.version) {
                costItem.financialAccessorialsId = previousPositiveAdj.financialAccessorialsId;
                costItem.version = previousPositiveAdj.version;
            }
            $scope.wizardData.nonInvoicedAdjustmentAccessorials.push(costItem);
        });
    }

    $scope.$on('event:pls-added-quote-item', function() {
        var previousPositiveAdj;
        if ($scope.isEdit) {
            // save id and version of positive adjustment
            previousPositiveAdj = _.findWhere($scope.wizardData.nonInvoicedAdjustmentAccessorials, {notInvoice: false});
            // negative adjustments can be left the same
            $scope.wizardData.nonInvoicedAdjustmentAccessorials = _.where($scope.wizardData.nonInvoicedAdjustmentAccessorials, {notInvoice: true});
        } else {
            //delete previous nonInvoiced adjustments
            $scope.$root.$broadcast('event:deleteAllAdjustments');

            //create negative adjustments
            var adjustments = changeSign(generateCostDetails());
            _.each(adjustments, function(adj) {
                adj.financialAccessorialsId = -1;
                adj.cost = 0;
            });
            $scope.wizardData.nonInvoicedAdjustmentAccessorials = adjustments;

            saveBackupDataFromShipment();
        }
        setNewDataToShipment();
        setNewCosts(previousPositiveAdj);
        $scope.cancelEditAdjustment();
        $scope.$root.$broadcast('event:adjustmentSaved');
    });

    function fixCarrierAdjustments(adjustments, carrierName){
        _.each(adjustments, function(adj) {
            adj.carrierName = carrierName;
            adj.revenue = 0;
        });
    }

    function handleRegularAdjustment() {
        if ($scope.isEdit) {
            $scope.wizardData.nonInvoicedAdjustmentAccessorials[$scope.indexOfEditedAdjustment] = $scope.adjustmentModel;
        } else {
            $scope.wizardData.nonInvoicedAdjustmentAccessorials.push($scope.adjustmentModel);
        }
    }

    function handleWrongCarrierAdjustment() {
        if ($scope.isEdit) {
            handleRegularAdjustment();

            // update notInvoice flag for other items
            _.each($scope.wizardData.nonInvoicedAdjustmentAccessorials, function(adj) {
                if (adj.carrierName === $scope.adjustmentModel.carrierName) {
                    adj.notInvoice = $scope.adjustmentModel.notInvoice;
                }
            });
        } else {
            //delete previous nonInvoiced adjustments
            $scope.$root.$broadcast('event:deleteAllAdjustments');

            // save backup data from shipment
            initLoadInfo();
            $scope.wizardData.shipment.adjustments.loadInfo.carrier = $scope.wizardData.shipment.selectedProposition.carrier;

            // set new data to shipment
            $scope.wizardData.shipment.selectedProposition.carrier = $scope.adjustmentLoadInfo.carrier;

            // set new data to update shipment after saving
            $scope.wizardData.newLoadInfo = {carrier: $scope.adjustmentLoadInfo.carrier};

            // generate costs
            var negativeAdjustments = changeSign(generateCostDetails());
            var positiveAdjustments = generateCostDetails();
            fixCarrierAdjustments(negativeAdjustments, $scope.wizardData.shipment.adjustments.loadInfo.carrier.name);
            fixCarrierAdjustments(positiveAdjustments, $scope.adjustmentLoadInfo.carrier.name);
            _.each(negativeAdjustments, function(adj) {
                adj.financialAccessorialsId = -1;
            });
            $scope.wizardData.nonInvoicedAdjustmentAccessorials = _.flatten([negativeAdjustments, positiveAdjustments]);
        }
    }

    function handleCanceledAdjustment() {
        if (!$scope.isEdit) {
            //delete previous nonInvoiced adjustments
            $scope.$root.$broadcast('event:deleteAllAdjustments');

            $scope.wizardData.nonInvoicedAdjustmentAccessorials = changeSign(generateCostDetails());
        }

        // set same selected not invoice flag for all Cancelled adjustments
        _.each($scope.wizardData.nonInvoicedAdjustmentAccessorials, function(adj) {
            adj.notInvoice = $scope.adjustmentModel.notInvoice;
        });
    }

    $scope.saveAdjustment = function () {
        if ($scope.isRebillShipper()) {
            $scope.$broadcast('event:pls-add-quote-item');
            return;
        } else if ($scope.isWrongCarrier()) {
            handleWrongCarrierAdjustment();
        } else if ($scope.isCanceled()) {
            handleCanceledAdjustment();
        } else {
            handleRegularAdjustment();
        }
        $scope.cancelEditAdjustment();
        $scope.$root.$broadcast('event:adjustmentSaved');
    };

    $scope.isRebillShipper = function() {
        return $scope.addEditAdjustmentDialogVisible && ShipmentUtils.isRebillShipperAdjustment($scope.adjustmentModel);
    };

    $scope.isWrongCarrier = function() {
        return ShipmentUtils.isWrongCarrierAdjustment($scope.adjustmentModel);
    };

    $scope.adjustmentReasonChange = function() {
        $scope.adjustmentModel.notInvoice = true;
        $scope.adjustmentModel.revenue = 0;
        $scope.adjustmentModel.cost = 0;
        delete $scope.adjustmentModel.revenueNote;
        delete $scope.adjustmentModel.costNote;
        delete $scope.adjustmentModel.refType;

        if ($scope.isWrongCarrier()) {
            $scope.adjustmentModel.notInvoice = true;
            $scope.adjustmentLoadInfo = {};
        }
        setDataForRebillAdjustment();
    };

    $scope.isCostDisabled = function() {
        return $scope.isCanceled() || (!$scope.isEdit && $scope.isWrongCarrier());
    };

    $scope.isCanceled = function() {
        return ShipmentUtils.isCanceledAdjustment($scope.adjustmentModel);
    };

    $scope.isDonNotInvoiceDisabled = function() {
        return (!$scope.isEdit && $scope.isWrongCarrier())
                || (!$scope.adjustmentModel.revenue && !$scope.isCanceled() && !$scope.isWrongCarrier());
    };

    $scope.validateRevenue = function() {
        if ($scope.adjustmentModel.revenue === undefined) {
            $scope.adjustmentModel.revenue = 0;
        }
    };

    $scope.validateCost = function() {
        if ($scope.adjustmentModel.cost === undefined) {
            $scope.adjustmentModel.cost = 0;
        }
    };

    $scope.isValidRebill = function() {
        return !$scope.isRebillShipper() || $scope.adjustmentLoadInfo.costItems.length;
    };

    $scope.selectedCostItems = [];

    $scope.revenueGrid = {
        enableColumnResize: true,
        data: 'adjustmentLoadInfo.costItems',
        selectedItems: $scope.selectedCostItems,
        tabIndex: -10,
        columnDefs: [{
            field: 'refType',
            displayName: 'Description',
            width: '60%',
            cellFilter: 'refCodeAndDesc'
        }, {
            field: 'revenue',
            displayName: 'Revenue',
            width: '36%',
            cellFilter: 'plsCurrency'
        }],
        plugins: [NgGridPluginFactory.plsGrid()],
        enableSorting: false,
        multiSelect: false
    };

    function unselectItem() {
        if ($scope.selectedCostItems.length) {
            var index = _.lastIndexOf($scope.adjustmentLoadInfo.costItems, $scope.selectedCostItems[0]);
            $scope.revenueGrid.selectRow(index, false);
            $scope.selectedCostItems.length = 0;
        }
    }

    $scope.addCostDetails = function() {
        var usedAccessorialTypes = _.pluck($scope.adjustmentLoadInfo.costItems, 'refType');
        var costItems = _.filter(allAccessorialTypes, function (item) {
            return item && _.indexOf(usedAccessorialTypes, item.id) === -1 && item.status === 'ACTIVE';
        });
        unselectItem();
        $scope.$root.$broadcast('event:showAddEditCostDetails', {
            editedCostDetail: {},
            isEdit: false,
            hideCost: true,
            parentDialog: $scope.adjastmentDialogDiv,
            accessorialTypes: costItems
        });
    };

    $scope.editCostDetails = function() {
        $scope.$root.$broadcast('event:showAddEditCostDetails', {
            editedCostDetail: angular.copy($scope.selectedCostItems[0]),
            isEdit: true,
            hideCost: true,
            parentDialog: $scope.adjastmentDialogDiv,
            accessorialTypes: _.where(allAccessorialTypes, {id : $scope.selectedCostItems[0].refType})
        });
    };

    $scope.$on('event:saveCostDetails', function(event, costDetail) {
        if ($scope.selectedCostItems.length) {
            $scope.selectedCostItems[0].revenue = costDetail.revenue;
            if (costDetail.revenueNote) {
                $scope.selectedCostItems[0].revenueNote = costDetail.revenueNote;
            }
            unselectItem();
        } else {
            $scope.adjustmentLoadInfo.costItems.push(costDetail);
        }
    });

    $scope.deleteSelectedCostDetails = function() {
        var index = _.lastIndexOf($scope.adjustmentLoadInfo.costItems, $scope.selectedCostItems[0]);
        $scope.adjustmentLoadInfo.costItems.splice(index, 1);
        $scope.selectedCostItems.length = 0;
    };

    $scope.getTotalRevenue = function() {
        return _.reduce($scope.adjustmentLoadInfo.costItems, function(memo, item){ return memo + item.revenue; }, 0);
    };
}]);