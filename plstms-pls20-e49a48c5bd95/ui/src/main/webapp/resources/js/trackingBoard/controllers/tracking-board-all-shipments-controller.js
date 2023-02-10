angular.module('pls.controllers').controller('TrackingBoardAllShipmentsController', [ '$scope', '$rootScope', '$timeout', 'TrackingBoardService',
    'ShipmentDetailsService', 'NgGridPluginFactory', 'DateTimeUtils', 'manualBolModel', '$http',
    function ($scope, $rootScope, $timeout, TrackingBoardService, ShipmentDetailsService, NgGridPluginFactory, DateTimeUtils, manualBolModel, $http) {
        'use strict';

        var MAX_HISTORY_RANGE_IN_MONTHS = 3;

        $scope.searchDateSelectorValues = [
            {code: 'PICKUP', name: 'Pickup Date'},
            {code: 'CREATED', name: 'Created Date'},
            {code: 'DELIVERY', name: 'Delivery Date'}
        ];

        $scope.searchDateSelector = 'PICKUP';

        $scope.dateRangeSelectorValues = [
            {code: 'YESTERDAY', name: 'Yesterday'},
            {code: 'TODAY', name: 'Today'},
            {code: 'THIS_WEEK', name: 'This week'},
            {code: 'THIS_MONTH', name: 'This month'},
            {code: 'CUSTOM', name: 'Custom range'}
        ];

        $scope.dateRangeSelector = 'CUSTOM';
        $rootScope.isAllShipmentsPage = true;
        $scope.manualRangeType = 'DEFAULT';
        $scope.defaultSelectionRangeType = 'MONTH';
        $scope.totalSumm = 0.0;

        $scope.allShipmentsEntries = [];
        $scope.selectedEntries = [];
        $scope.toDateAlreadyExists = false;
        $scope.validData = true;
        $scope.toDateForPeriod = $scope.toDate;
        $scope.fromDateForPeriod = $scope.fromDate;
        $scope.showManualBol = false;

        $scope.sortInfo = {columns: [], fields: [], directions: []};

        $scope.isCustomDateRangeSelected = function () {
            return $scope.dateRangeSelector === 'CUSTOM';
        };

        $scope.$watch('dateRangeSelector', function (newValue) {
            var today = new Date();

            switch (newValue) {
                case 'YESTERDAY':
                    $scope.toDate = DateTimeUtils.addDays(today, -1);
                    $scope.fromDate = DateTimeUtils.addDays(today, -1);
                    break;
                case 'TODAY':
                    $scope.toDate = today;
                    $scope.fromDate = today;
                    break;
                case 'THIS_WEEK':
                    $scope.toDate = today;
                    $scope.fromDate = DateTimeUtils.addDays(today, -(today.getDay()));
                    break;
                case 'THIS_MONTH':
                    $scope.toDate = today;
                    $scope.fromDate = DateTimeUtils.addDays(today, -(today.getDate() - 1));
                    break;
            }
        });

        $scope.$watch('fromDate', function (newValue) {
            if (newValue) {
                $scope.minToDate = DateTimeUtils.parseISODate(newValue);
                $scope.maxToDate = DateTimeUtils.addMonths($scope.minToDate, MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.minToDate = undefined;
                $scope.maxToDate = undefined;
            }

            $scope.updatePickupDateRangeIsEmpty();
        });

        $scope.$watch('toDate', function (newValue) {
            if (newValue) {
                $scope.maxFromDate = DateTimeUtils.parseISODate(newValue);
                $scope.minFromDate = DateTimeUtils.addMonths($scope.maxFromDate, -MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.maxFromDate = undefined;
                $scope.minFromDate = undefined;
            }

            $scope.updatePickupDateRangeIsEmpty();
        });

        $scope.updatePickupDateRangeIsEmpty = function () {
            var isEmpty = ($scope.toDate === null || $scope.toDate === undefined);

            if (isEmpty) {
                // remove all highlighted validation fields
                $scope.toDateAlreadyExists = false;
                $scope.validData = true;
            } else {
                // decide which field we should highlight
                $scope.toDateAlreadyExists = !$scope.toDate;
                // if some field is not filled then disable 'Search' button, otherwise - enabled
                $scope.validData = !$scope.toDateAlreadyExists;
            }
        };

        var carrierFilterSelectedOnly = function () {
            return $scope.carrier && $scope.fromDate === undefined && $scope.toDate === undefined && !($scope.bol || $scope.pro);
        };

        var originFiterSelectedOnly = function () {
            return $scope.origin && $scope.fromDate === undefined && $scope.toDate === undefined && !($scope.bol || $scope.pro);
        };

        var destinationFiterSelectedOnly = function () {
            return $scope.destination && $scope.fromDate === undefined && $scope.toDate === undefined && !($scope.bol || $scope.pro);
        };

        var calculateTotal = function (entries) {
            $scope.totalRevenue = 0.0;
            $scope.totalCost = 0.0;
            $scope.totalMargin = 0.0;

            angular.forEach(entries, function (item) {
                if (item.revenue) {
                    $scope.totalRevenue += item.revenue;
                }

                if (item.total) {
                    $scope.totalCost += item.total;
                }

                if (item.margin) {
                    $scope.totalMargin += item.margin;
                }
            });

            $scope.totals = {
                totalRevenue: $scope.totalRevenue.toFixed(2),
                totalCost: $scope.totalCost.toFixed(2),
                totalMargin: $scope.totalMargin.toFixed(2)
            };
        };

        $scope.searchAllShipmentsEntries = function () {
            if (carrierFilterSelectedOnly() || (originFiterSelectedOnly() && destinationFiterSelectedOnly()) || originFiterSelectedOnly() ||
                    destinationFiterSelectedOnly()) {
                $scope.toDateAlreadyExists = true;
            } else {
                TrackingBoardService.all({
                    customer: $scope.customer ? $scope.customer.id : '',
                    loadId: $scope.loadId,
                    bol: $scope.bol,
                    pro: $scope.pro,
                    job: $scope.job,
                    po: $scope.po,
                    origin: $scope.origin ? $scope.origin.zip : '',
                    destination: $scope.destination ? $scope.destination.zip : '',
                    carrier: $scope.carrier ? $scope.carrier.id : '',
                    fromDate: $scope.fromDate,
                    toDate: $scope.toDate,
                    accountExecutiveId: $scope.accountExecutive ? $scope.accountExecutive.id : null,
                    dateSearchField: $scope.searchDateSelector,
                    withManualBol : $scope.showManualBol
                }
                , function (data) {
                    $scope.allShipmentsEntries = data;
                    calculateTotal(data);
                    $scope.selectedEntries.length = 0;
                });
            }
        };

        //total sum should be updated when used progressive search and data in the grid is updated
        $scope.$watch('allShipmentsGrid.ngGrid.filteredRows', function (newValue) {
            calculateTotal(_.map(newValue, function (item) {
                return item.entity;
            }));
        });

        $scope.clearSearchCriteria = function () {
            $scope.keywords = null;
            $scope.sortConfig = null;
            delete $scope.accountExecutive;
            $scope.allShipmentsEntries.length = 0;
            $scope.selectedEntries.length = 0;
            $scope.totalSumm = 0.0;
            $scope.totalRevenue = 0.0;
            $scope.totalCost = 0.0;
            $scope.totalMargin = 0.0;
            $scope.dateRangeSelector = 'CUSTOM';
            $scope.searchDateSelector = 'PICKUP';
            $scope.showManualBol = false;

            $scope.$broadcast('event:cleaning-input');
        };

        $scope.$on('event:shipmentDataUpdated', function () {
            $scope.searchAllShipmentsEntries();
        });
        
        $scope.isShipment = function(){
            if($scope.selectedEntries.length > 0){
                return !$scope.selectedEntries[0].manualBol;
            }else{
                return true;
            }
        };

        $scope.isManualBol = function(){
            if($scope.selectedEntries.length > 0){
                return $scope.selectedEntries[0].manualBol;
            }else{
                return false;
            }
        };

        /**
         * for manual BOL.
         */
        $scope.uncheckManualBol = function() {
            if ($scope.searchDateSelector === 'DELIVERY') {
                $scope.showManualBol = false;
            }
        };

        $scope.copyManualBol = function (manualBol) {
            manualBolModel.copy(manualBol.customerId, manualBol.shipmentId);
        };

        $scope.editManualBol = function (manualBol) {
            manualBolModel.edit(manualBol.customerId, manualBol.shipmentId);
        };

        $scope.cancelManualBol = function (manualBol) {
            manualBolModel.cancel(manualBol.customerId, manualBol.shipmentId, function () {
                manualBol.status = 'CANCELLED';
            });
        };

        $scope.viewManualBol = function () {
            var details = {
                isManualBol: true,
                shipmentId: $scope.selectedEntries[0].shipmentId,
                customerId: $scope.selectedEntries[0].customerId,
                bol: $scope.selectedEntries[0].bolNumber,
                closeHandler: function () {
                    $scope.progressPanelOptions.showPanel = false;
                }
            };

            manualBolModel.redirectToTrackingBoard = true;
            $scope.$broadcast('event:showShipmentDetails', details);
        };

        /**
         * the end.
         */

        $scope.view = function() {
            $scope.viewShipmentDetails($scope.selectedEntries[0]);
        };

        $scope.closeHandler = function () {
            $scope.searchAllShipmentsEntries();
            $scope.selectedEntries.length = 0;
        };

        $scope.viewShipmentDetails = function (entity) {
            if ($scope.$root.isFieldRequired('VIEW_ALL_SHIPMENTS_COST_DETAILS')) {
                $scope.openEditSalesOrderDialog({
                    shipmentId: entity.shipmentId,
                    formDisabled: true,
                    closeHandler: $scope.closeHandler,
                    isBillingStatusNone: (entity.billingStatus === 'NONE')
                });
            } else {
                var shipmentDetailsOption = {
                    shipmentId: entity.shipmentId,
                    customerId: entity.customerId,
                    bol: entity.bolNumber
                };

                $scope.$broadcast('event:showShipmentDetails', shipmentDetailsOption);
            }
        };

        function onShowTooltip(row) {
            if (row.entity.manualBol) {
                $http.get(
                        '/restful/customer/' + row.entity.customerId + '/manualbol/'
                                + row.entity.shipmentId + '/tooltip').success(function(response) {
                    if (response) {
                        $scope.tooltipData = response;
                        $scope.tooltipData.isManualBol = true;
                    }
                });
            } else {
                ShipmentDetailsService.getTooltipData({
                    customerId : row.entity.customerId,
                    shipmentId : row.entity.shipmentId
                }, function(data) {
                    if (data) {
                        $scope.tooltipData = data;
                        $scope.tooltipData.isManualBol = false;
                    }
                });
            }
        }

        $scope.openTerminalInfoModalDialog = function (shipmentId) {
            $scope.$broadcast('event:openTerminalInfoDialog', shipmentId);
        };

        $scope.openTerminalInfoDialogFoManualBol = function (id) {
            $scope.$broadcast('event:openTerminalInfoDialogFoManualBol', id);
        };

        $scope.keyPressed = function (evt) {
            if (evt.which === 13) {
                $scope.searchAllShipmentsEntries();
            }
        };

        var gridColumns = [{
            field: 'indicators',
            displayName: 'Indicators',
            cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
        }, {
            field: 'shipmentId',
            displayName: 'Load ID'
        }, {
            field: 'bolNumber',
            displayName: 'BOL',
            showTooltip: true
        }, {
            field: 'pieces',
            displayName: 'QTY'
        }, {
            field: 'weight',
            displayName: 'Total Weight'
        }, {
            field: 'soNumber',
            displayName: 'SO#'
        }, {
            field: 'glNumber',
            displayName: 'GL#'
        }, {
            field: 'proNumber',
            displayName: 'Pro#'
        }, {
            field: 'refNumber',
            displayName: 'Shipper Ref#'
        }, {
            field: 'accountExecutive',
            displayName: 'Account Executive'
        }, {
            field: 'poNumber',
            displayName: 'PO#'
        }, {
            field: 'puNumber',
            displayName: 'PU#'
        }, {
            field: 'jobNumber',
            displayName: 'JOB#'
        }, {
            field: 'shipper',
            displayName: 'Shipper',
            headerClass: 'text-center',
            cellClass: 'text-center'
        }, {
            field: 'origin',
            cellFilter: 'zip',
            displayName: 'Origin'
        }, {
            field: 'consignee',
            displayName: 'Consignee',
            headerClass: 'text-center',
            cellClass: 'text-center'
        }, {
            field: 'destination',
            cellFilter: 'zip',
            displayName: 'Destination'
        }, {
            field: 'carrier',
            displayName: 'Carrier',
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()" data-ng-if="!row.entity.manualBol">' +
            '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.shipmentId)">{{row.getProperty(col.field)}}</a>'
                + '</div>'
                + '<div class="ngCellText" data-ng-class="col.colIndex()" data-ng-if="row.entity.manualBol">'
                + '<a href="" data-ng-if="row.entity.scac !==\'1TRU\'" data-ng-click="openTerminalInfoDialogFoManualBol(row.entity.shipmentId)" >'
                + '{{row.getProperty(col.field)}}</a>'
                + '<span data-ng-if="row.entity.scac ===\'1TRU\'">{{row.getProperty(col.field)}}</span>'
                + '</div>'
        }, {
            field: 'integrationType',
            displayName: 'EDI',
            cellFilter: 'integrationTypeFilter'
        }, {
            field: 'pickupDate',
            displayName: 'Pickup Date',
            cellFilter: "date:wideAppDateFormat"
        }, {
            field: 'dispatchedDate',
            displayName: 'Dispatched Date',
            cellFilter: "date:appDateTimeFormat"
        }, {
            field: 'deliveryDate',
            displayName: 'Del. Date',
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<span data-ng-bind="_.isUndefined(row.entity.deliveryDate) ?\'N/A\':row.entity.deliveryDate|date:appDateFormat">' +
                '</span></div>',
            cellFilter: 'date:appDateFormat'
        }, {
            field: 'status',
            displayName: 'Ops. Status',
            cellFilter: 'shipmentStatus'
        }
        ];

        $scope.allShipmentsGrid = {
            enableColumnResize: true,
            data: 'allShipmentsEntries',
            multiSelect: false,
            selectedItems: $scope.selectedEntries,
            columnDefs: gridColumns,
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_VIEW_SALES_ORDER')) {
                    if (entity.manualBol) {
                        $scope.viewManualBol(entity);
                    } else {
                        $scope.viewShipmentDetails(entity);
                    }
                }
            },
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.progressiveSearchPlugin(),
                NgGridPluginFactory.actionPlugin(),
                NgGridPluginFactory.hideColumnPlugin()
            ],
            tooltipOptions: {
                url: 'pages/content/quotes/shipments-grid-tooltip.html',
                onShow: onShowTooltip
            },
            refreshTable: function (columnFilters, pagingConfig, sortConfig) {
                $scope.searchAllShipmentsEntries(sortConfig);
            },
            progressiveSearch: true,
            sortInfo: $scope.sortInfo
        };

        if ($rootScope.isFieldRequired('VIEW_ALL_SHIPMENTS_COST_DETAILS')) {
            gridColumns.unshift({
                field: 'adjustment',
                headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                headerClass: 'cellToolTip',
                cellTemplate: '<div class="ngCellText text-center tooltip-wide" data-ng-show="row.entity.adjustment">'
                        + '<i class="icon-wrench" data-tooltip-placement="right" data-tooltip="Adjustment"></i></div>',
                cellClass: 'cellToolTip',
                width: '35',
                searchable: false,
                sortable: true,
                exportTemplate: 'exportEntity.adjustment ? "Adjustment" : ""',
                exportDisplayName: 'Adjustment'
            });

            gridColumns.push({
                field: 'billingStatus',
                displayName: 'Billing Status'
            }, {
                field: 'revenue',
                displayName: 'Revenue',
                cellFilter: 'plsCurrency'

            }, {
                field: 'total',
                displayName: 'Cost',
                cellFilter: 'plsCurrency',
                hideForReport: $scope.exportWithoutCostMargin()
            }, {
                field: 'margin',
                displayName: 'Margin',
                cellFilter: 'plsCurrency',
                hideForReport: $scope.exportWithoutCostMargin()
            });
        } else if ($rootScope.isFieldRequired('VIEW_ALL_SHIPMENTS_REVENUE_ONLY')) {
            gridColumns.push({
                field: 'revenue',
                displayName: 'Total',
                cellFilter: 'plsCurrency'
            });
        }

        if ($rootScope.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            gridColumns.splice($rootScope.isFieldRequired('VIEW_ALL_SHIPMENTS_COST_DETAILS') ? 10 : 9, 0, {
                field: 'network',
                displayName: 'Business Unit'
            });
        }

        $scope.detailsGrid = {};
    }
]);