angular.module('pls.controllers').controller('SOGeneralAdjustmentInfoCtrl', ['$scope', '$filter', 'DateTimeUtils', 'ShipmentUtils',
    'ShipmentDictionaryTypeService', 'AddressService', 'StringUtils', 'NgGridPluginFactory',
function ($scope, $filter, DateTimeUtils, ShipmentUtils, ShipmentDictionaryTypeService, AddressService, StringUtils, NgGridPluginFactory) {
    'use strict';

    if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map) {
        var stepObject = $scope.wizardData.breadCrumbs.map.general_information;

        stepObject.validNext = function () {
            return true;
        };
    }

    $scope.costDetailsForGrid = [];
    $scope.invoicedAdjustments = [];
    $scope.selectedCostDetails = [];
    $scope.wizardData.nonInvoicedAdjustmentAccessorials = [];
    $scope.wizardData.removedAdjustments = [];
    $scope.wizardData.selectedCustomer.name = $scope.wizardData.shipment.customerName;

    if (!$scope.$root.financialReasons || !$scope.$root.financialReasons.length) {
        ShipmentDictionaryTypeService.getFinancialReasons({}, function (data) {
            $scope.$root.financialReasons = _.sortBy(data, function (financialReasons) {
                return financialReasons.label;
            });
        });
    }

    /**
     * Each Rebill or Rebill Shipper adjustment can change bill to name.
     * Generally only two cases available if at least one such adjustment exists:
     * - When final bill to name (the one which is currently saved in shipment)
     *   equals the original bill to name (the one which was used for first invoice).
     *      In this case each bill to name will appear even number of times in adjustments.
     *      e.g. load was created with billto1.
     *          1st rebill adjustment (id1 - billto1, id2 - billto2)
     *          2nd rebill adjustment (id3 - billto2, id4 - billto1)
     *          billto1 appears in two adjustments and billto2 also appears in two adjustments.
     *          We should simply ignore bill to changes made in adjustments.
     * - When final bill to name is different from the original bill to name.
     *      In this case only original bill to name and final bill to name will appear odd number of times in adjustments.
     *      e.g. load was created with billto1.
     *          1st rebill adjustment (id1 - billto1, id2 - billto2)
     *          2nd rebill adjustment (id3 - billto2, id4 - billto3)
     *          billto2 appears in two adjustments - we can ignore it.
     *          billto1 appears in one adjustment and billto3 also appears in one adjustment.
     *          We should use bill to which is different from the one which is currently used in shipment.
     *          In our case billto3 is currently used in shipment.
     *          So billto1 is original bill to. 
     */
    function getOriginalBillToName() {
        if ($scope.wizardData.shipment.adjustments && !_.isEmpty($scope.wizardData.shipment.adjustments.costItems)) {
            var idNameMap = {};
            _.each($scope.wizardData.shipment.adjustments.costItems, function (adjustment) {
                if (ShipmentUtils.isOutdatedRebillAdjustment(adjustment) || ShipmentUtils.isRebillShipperAdjustment(adjustment)) {
                    idNameMap[adjustment.financialAccessorialsId] = adjustment.billToName;
                }
            });
            if (!_.isEmpty(idNameMap)) {
                var values = _.values(idNameMap); // array of bill to names
                if (values.length > 2) {
                    var counts = _.countBy(values, function (v){return v;});
                    // counts is an object with key - BillToName and value - count of it's occurrences
                    counts = _.pick(counts, function(value, key, object) {
                        return value % 2 === 1; // leave only those where count is odd
                    });
                    values = _.keys(counts);
                }
                if (values.length === 2) {
                    return values[0] === $scope.wizardData.shipment.billTo.address.addressName ? values[1] : values[0];
                }
            }
        }
        return $scope.wizardData.shipment.billTo.address.addressName;
    }

    function generateCostDetailsFromProposal() {
        var result = {};
        var keys = [];

        _.each($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
            if (item.costDetailOwner === 'S' || item.costDetailOwner === 'C') {
                var refType = item.refType;

                if (refType === 'CRA') {
                    refType = 'SRA';
                }

                var field = item.costDetailOwner === 'S' ? 'revenue' : 'cost';
                var fieldNote = field === 'revenue' ? 'revenueNote' : 'costNote';

                if (!result[refType]) {
                    result[refType] = {};
                    keys.push(refType);
                }

                result[refType][field] = item.subTotal;
                result[refType][fieldNote] = item.note;
            }
        });

        var firstBillToName = getOriginalBillToName();

        $scope.invoicedAdjustments = _.map(keys, function (key) {
            return {
                refType: key,
                revenue: result[key].revenue || 0,
                cost: result[key].cost || 0,
                revenueNote: result[key].revenueNote,
                costNote: result[key].costNote,
                reason: '',
                billToName: firstBillToName,
                invoiceNumber: $scope.wizardData.shipment.invoiceNumber,
                invoiceDate: DateTimeUtils.parseISODate($scope.wizardData.shipment.invoiceDate),
                notInvoice: false,
                financialAccessorialsId: -2
            };
        });
    }
    generateCostDetailsFromProposal();

    function generateCostDetailsFromAdjustments() {
        if ($scope.wizardData.shipment.adjustments && !_.isEmpty($scope.wizardData.shipment.adjustments.costItems)) {
            _.each($scope.wizardData.shipment.adjustments.costItems, function (adjustment) {
                if (adjustment.invoiceDate) {
                    $scope.invoicedAdjustments.push(adjustment);
                } else {
                    $scope.wizardData.nonInvoicedAdjustmentAccessorials.push(adjustment);
                }
            });
        }
    }
    generateCostDetailsFromAdjustments();

    function calculateTotals() {
        $scope.totalCost = 0;
        $scope.totalRevenue = 0;

        _.each($scope.costDetailsForGrid, function (item) {
            $scope.totalCost += item.cost;
            $scope.totalRevenue += item.revenue;
        });

        $scope.totalRevenue = $scope.totalRevenue.toFixed(2);
    }

    function rebuildGrid() {
        $scope.costDetailsForGrid = angular.copy($scope.invoicedAdjustments);

        if (!_.isEmpty($scope.wizardData.nonInvoicedAdjustmentAccessorials)) {
            angular.forEach($scope.wizardData.nonInvoicedAdjustmentAccessorials, function (accessorial) {
                $scope.costDetailsForGrid.push(accessorial);
            });
        }

        calculateTotals();

        // calculate grouping column
        _.each($scope.costDetailsForGrid, function (item) {
            item.grouping = item.financialAccessorialsId + '_' + item.notInvoice;
        });
        $scope.selectedCostDetails.length = 0;
    }
    rebuildGrid();

    $scope.$on('event:adjustmentSaved', rebuildGrid);

    $scope.addAdjustment = function() {
        $scope.$root.$broadcast('event:showAddEditAdjustment');
    };

    $scope.editAdjustment = function() {
        var index = _.lastIndexOf($scope.wizardData.nonInvoicedAdjustmentAccessorials, $scope.selectedCostDetails[0]);
        $scope.$root.$broadcast('event:showAddEditAdjustment', index);
    };

    function deleteSelectedAdjustment(adj, forceDelete) {
        if (adj && adj.financialAccessorialsId !== undefined && adj.version !== undefined) {
            // we should save removed adjustment if all cost items have been removed
            var removed = {
                financialAccessorialsId: adj.financialAccessorialsId,
                version: adj.version
            };

            if ((forceDelete || _.findWhere($scope.wizardData.nonInvoicedAdjustmentAccessorials, removed) === undefined)
                    && _.findWhere($scope.wizardData.removedAdjustments, removed) === undefined) {
                $scope.wizardData.removedAdjustments.push(removed);
            }
        }
    }

    function revertLoadInPreviousState() {
        if ($scope.selectedCostDetails.length && ShipmentUtils.isWrongCarrierAdjustment($scope.selectedCostDetails[0])) {
            $scope.wizardData.shipment.selectedProposition.carrier = $scope.wizardData.shipment.adjustments.loadInfo.carrier;
        } else if ($scope.selectedCostDetails.length && ShipmentUtils.isRebillShipperAdjustment($scope.selectedCostDetails[0])) {
            var loadInfo = $scope.wizardData.shipment.adjustments.loadInfo;
            $scope.wizardData.shipment.bolNumber = loadInfo.bolNumber;
            $scope.wizardData.shipment.finishOrder.poNumber = loadInfo.poNumber;
            $scope.wizardData.shipment.finishOrder.soNumber = loadInfo.soNumber;
            $scope.wizardData.shipment.finishOrder.ref = loadInfo.refNumber;

            $scope.wizardData.shipment.finishOrder.quoteMaterials = $scope.wizardData.shipment.adjustments.products;

            if (loadInfo.billToId !== $scope.wizardData.shipment.billTo.id) {
                // search bill to by ID
                AddressService.listUserBillToAddresses({
                    customerId: $scope.wizardData.shipment.organizationId,
                    currency: $scope.wizardData.shipment.selectedProposition.carrier.currencyCode
                }, function (data) {
                    if (data && data.length) {
                        $scope.wizardData.shipment.billTo = _.find(data, function (billto) {
                            return billto.id === loadInfo.billToId;
                        });
                    }
                });
            }
        }
        delete $scope.wizardData.newLoadInfo;
        delete $scope.wizardData.newProducts;
        if ($scope.wizardData.shipment.adjustments) {
            delete $scope.wizardData.shipment.adjustments.loadInfo;
            delete $scope.wizardData.shipment.adjustments.products;
        }
        _.each($scope.wizardData.nonInvoicedAdjustmentAccessorials, function(adj) {
            deleteSelectedAdjustment(adj, true);
        });
        $scope.wizardData.nonInvoicedAdjustmentAccessorials = [];
        rebuildGrid();
    }

    $scope.$on('event:deleteAllAdjustments', revertLoadInPreviousState);

    $scope.deleteAdjustment = function() {
        if (ShipmentUtils.isRebillShipperAdjustment($scope.selectedCostDetails[0])) {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Warning',
                message: 'Both Rebill Shipper adjustments will be deleted. Would you like to proceed?',
                okFunction: revertLoadInPreviousState,
                parentDlgId: 'detailsDialogDiv'
            });
        } else if (ShipmentUtils.isWrongCarrierAdjustment($scope.selectedCostDetails[0])
                || ShipmentUtils.isCanceledAdjustment($scope.selectedCostDetails[0])) {
            revertLoadInPreviousState();
        } else {
            var index = _.lastIndexOf($scope.wizardData.nonInvoicedAdjustmentAccessorials, $scope.selectedCostDetails[0]);
            $scope.wizardData.nonInvoicedAdjustmentAccessorials.splice(index, 1);
            deleteSelectedAdjustment($scope.selectedCostDetails[0]);
            rebuildGrid();
        }
    };

    $scope.adjustmentsGrid = {
        enableColumnResize: true,
        data: 'costDetailsForGrid',
        selectedItems: $scope.selectedCostDetails,
        columnDefs: [{
            field: 'grouping',
            visible: false
        }, {
            field: 'refType',
            displayName: 'Acc. Description',
            cellFilter: 'refCodeAndDesc',
            width: '20%'
        }, {
            field: 'reason',
            displayName: 'Adj. Reason',
            width: '15%',
            cellFilter: 'financialReason'
        }, {
            field: 'billToName',
            displayName: 'Bill To',
            width: '14%'
        }, {
            field: 'invoiceNumber',
            displayName: 'Inv Number',
            width: '15%'
        }, {
            field: 'invoiceDate',
            displayName: 'Inv. Date',
            width: '7%',
            cellFilter: 'date : appDateFormat'
        }, {
            field: 'revenue',
            displayName: 'Revenue',
            width: '7%',
            cellFilter: 'plsCurrency'
        }, {
            field: 'cost',
            displayName: 'Cost',
            width: '7%',
            cellFilter: 'plsCurrency'
        }, {
            field: 'notInvoice',
            displayName: 'Do Not Invoice',
            cellTemplate: 'pages/cellTemplate/checked-cell.html',
            width: '11%'
        }],
        plugins: [NgGridPluginFactory.plsGrid()],
        groups: ['grouping'],
        aggregateTemplate: "<div data-ng-click=\"row.toggleExpand()\" data-ng-style=\"rowStyle(row)\" class=\"ngAggregate\">" +
                           "    <p style=\"white-space:pre-wrap;\" class=\"ngAggregateText\">{{getAggregateRowText(row)}}</p>" +
                           "    <div class=\"{{row.aggClass()}}\"></div>" +
                           "</div>",
        enableSorting: false,
        groupsCollapsedByDefault: false,
        multiSelect: false
    };

    $scope.getAggregateRowText = function(row) {
        if (!row.children.length) {
            return '';
        }

        var textInvoiced = 'Invoice {0}    ({1} {2}, {3}{4})    {5} cost item{6} of Total: {7} / {8}{9} ';
        var textNotInvoiced = 'Invoice {0}    ({1} {2}, not invoiced)    {5} cost item{6} of Total: {7} / {8}{9}';

        var totalChildren = row.totalChildren();

        var sum = _.reduce(row.children, function (memo, item) {
            return {
                cost: memo.cost + item.entity.cost,
                revenue: memo.revenue + item.entity.revenue
            };
        }, {cost: 0, revenue: 0});

        var firstItem = row.children[0].entity;
        var isInvoiced = !_.isUndefined(firstItem.invoiceDate);

        var args = [];

        args[0] = row.aggIndex + 1;
        args[1] = ShipmentUtils.isWrongCarrierAdjustment(firstItem) ? firstItem.carrierName : '';
        args[2] = firstItem.billToName;
        args[3] = isInvoiced && firstItem.invoiceNumber ? firstItem.invoiceNumber + ', ' : '';
        args[4] = isInvoiced ? $filter('date')(firstItem.invoiceDate, $scope.$root.appDateFormat) : '';
        args[5] = totalChildren;
        args[6] = totalChildren > 1 ? 's' : '';
        args[7] = $filter('plsCurrency')(sum.cost);
        args[8] = $filter('plsCurrency')(sum.revenue);
        args[9] = firstItem.notInvoice ? ', Do Not Invoice' : '';

        return StringUtils.format(isInvoiced ? textInvoiced : textNotInvoiced, args);
    };

    $scope.isNotInvoicedAdjustmentSelected = function() {
        return !$scope.selectedCostDetails.length || $scope.selectedCostDetails[0].invoiceDate;
    };

    $scope.isAddAdjustmentProhibited = function() {
        return $scope.wizardData.nonInvoicedAdjustmentAccessorials.length
                && _.some($scope.wizardData.nonInvoicedAdjustmentAccessorials, function(adj) {
                    return ShipmentUtils.isWrongCarrierAdjustment(adj) || ShipmentUtils.isRebillShipperAdjustment(adj)
                        || ShipmentUtils.isCanceledAdjustment(adj);
                });
    };
}]);
