angular.module('pls.controllers').controller('ManualBolController', ['$scope', '$rootScope', '$http', 'TrackingBoardService', 'NgGridPluginFactory',
    'DateTimeUtils', 'ExportDataBuilder', 'ExportService', 'manualBolModel',
    function ($scope, $rootScope, $http, TrackingBoardService, NgGridPluginFactory, DateTimeUtils, ExportDataBuilder, ExportService, manualBolModel) {
        'use strict';

        var MAX_HISTORY_RANGE_IN_MONTHS = 3;

        $scope.searchDateSelectorValues = [{
            code: 'PICKUP',
            name: 'Pickup Date'
        }, {
            code: 'CREATED',
            name: 'Created Date'
        }];

        $scope.searchDateSelector = 'PICKUP';

        $scope.dateRangeSelectorValues = [{
            code: 'YESTERDAY',
            name: 'Yesterday'
        }, {
            code: 'TODAY',
            name: 'Today'
        }, {
            code: 'THIS_WEEK',
            name: 'This week'
        }, {
            code: 'THIS_MONTH',
            name: 'This month'
        }, {
            code: 'CUSTOM',
            name: 'Custom range'
        }];

        $scope.dateRangeSelector = 'CUSTOM';
        $rootScope.isAllShipmentsPage = true;
        $scope.manualRangeType = 'DEFAULT';
        $scope.defaultSelectionRangeType = 'MONTH';

        $scope.manualBolList = [];
        $scope.selectedEntries = [];
        $scope.toDateAlreadyExists = false;
        $scope.validData = true;
        $scope.toDateForPeriod = $scope.toDate;
        $scope.fromDateForPeriod = $scope.fromDate;

        $scope.sortInfo = {
            columns: [],
            fields: [],
            directions: []
        };

        $scope.isCustomDateRangeSelected = function () {
            return $scope.dateRangeSelector === 'CUSTOM';
        };

        $scope.copyManualBol = function (manualBol) {
            manualBolModel.copy(manualBol.customerId, manualBol.id);
        };

        $scope.editManualBol = function (manualBol) {
            manualBolModel.edit(manualBol.customerId, manualBol.id);
        };

        $scope.cancelManualBol = function (manualBol) {
            manualBolModel.cancel(manualBol.customerId, manualBol.id, function () {
                manualBol.status = 'CANCELLED';
            });
        };

        $scope.view = function () {
            var details = {
                isManualBol: true,
                shipmentId: $scope.selectedEntries[0].id,
                customerId: $scope.selectedEntries[0].customerId,
                bol: $scope.selectedEntries[0].bolNumber,
                closeHandler: function () {
                    $scope.progressPanelOptions.showPanel = false;
                }
            };

            manualBolModel.redirectToTrackingBoard = true;
            $scope.$broadcast('event:showShipmentDetails', details);
        };

        function onShowTooltip(row) {
            $http.get('/restful/customer/' + row.entity.customerId + '/manualbol/' + row.entity.id + '/tooltip').success(function (response) {
                if (response) {
                    $scope.tooltipData = response;
                }
            });
        }

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
                case 'CUSTOM':
                    break;
                default:
                    console.log('error: ' + newValue);
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
                // if some field is not filled then disable
                // 'Search' button, otherwise - enabled
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

        $scope.searchManualBolList = function () {
            if (carrierFilterSelectedOnly() || (originFiterSelectedOnly() && destinationFiterSelectedOnly()) || originFiterSelectedOnly()
                    || destinationFiterSelectedOnly()) {
                $scope.toDateAlreadyExists = true;
            } else {
                TrackingBoardService.allManualBol({
                    customer: $scope.customer ? $scope.customer.id : '',
                    loadId: $scope.loadId,
                    bol: $scope.bol,
                    pro: $scope.pro,
                    job: $scope.job,
                    origin: $scope.origin ? $scope.origin.zip : '',
                    destination: $scope.destination ? $scope.destination.zip : '',
                    carrier: $scope.carrier ? $scope.carrier.id : '',
                    fromDate: $scope.fromDate,
                    toDate: $scope.toDate,
                    dateSearchField: $scope.searchDateSelector
                }, function (data) {
                    $scope.manualBolList = data;
                    $scope.selectedEntries.length = 0;
                });
            }
        };

        $scope.clearSearchCriteria = function () {
            $scope.keywords = null;
            $scope.sortConfig = null;
            $scope.accountExecutiveId = null;
            $scope.manualBolList.length = 0;
            $scope.selectedEntries.length = 0;
            $scope.totalRevenue = 0.0;
            $scope.totalCost = 0.0;
            $scope.totalMargin = 0.0;
            $scope.dateRangeSelector = 'CUSTOM';
            $scope.searchDateSelector = 'PICKUP';

            $scope.$broadcast('event:cleaning-input');
        };

        $scope.$on('event:shipmentDataUpdated', function () {
            $scope.searchManualBolList();
        });

        $scope.exportAllShipments = function () {
            var fileFormat = 'ManualBOL_Exportfile_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Manual BOL";

            var shipmentEntities = _.map($scope.allShipmentsGrid.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var exportData = ExportDataBuilder.buildExportData($scope.allShipmentsGrid, shipmentEntities, fileFormat, sheetName);

            ExportService.exportData(exportData);
        };

        $scope.openTerminalInfoModalDialog = function (id) {
            $scope.$broadcast('event:openTerminalInfoDialogFoManualBol', id);
        };

        $scope.keyPressed = function (evt) {
            if (evt.which === 13) {
                $scope.searchManualBolList();
            }
        };

        var gridColumns = [{
            field: 'indicators',
            displayName: 'Indicators',
            cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
        }, {
            field: 'id',
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
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">'
            + '<a href="" data-ng-if="row.entity.scac != \'1TRU\'" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">'
            + '{{row.getProperty(col.field)}}</a>'
            + '<span data-ng-if="row.entity.scac === \'1TRU\'" >{{row.getProperty(col.field)}}</span>'
            + '</div>'
        }, {
            field: 'pickupDate',
            displayName: 'Pickup Date',
            cellFilter: "date:wideAppDateFormat"
        }, {
            field: 'status',
            displayName: 'Ops. Status',
            cellFilter: 'shipmentStatus'
        }];

        $scope.allShipmentsGrid = {
            enableColumnResize: true,
            data: 'manualBolList',
            multiSelect: false,
            selectedItems: $scope.selectedEntries,
            columnDefs: gridColumns,
            action: function () {
                if ($scope.$root.isFieldRequired('BOARD_CAN_VIEW_SALES_ORDER')) {
                    $scope.view();
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
                url: 'pages/content/manual-bol/manual-bol-grid-tooltip.html',
                onShow: onShowTooltip
            },
            refreshTable: function (columnFilters, pagingConfig, sortConfig) {
                $scope.searchManualBolList(sortConfig);
            },
            progressiveSearch: true,
            sortInfo: $scope.sortInfo
        };

        if ($scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            gridColumns.splice(9, 0, {
                field: 'network',
                displayName: 'Business Unit'
            });
        }
    }
]);