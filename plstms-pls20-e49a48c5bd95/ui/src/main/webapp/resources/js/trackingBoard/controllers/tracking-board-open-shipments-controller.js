angular.module('pls.controllers').controller('TrackingBoardOpenController', [
    '$scope', '$timeout', 'TrackingBoardService', 'NgGridPluginFactory', 'ShipmentDetailsService', 'DateTimeUtils',
    function ($scope, $timeout, TrackingBoardService, NgGridPluginFactory, ShipmentDetailsService, DateTimeUtils) {
        'use strict';

        $scope.shipments = [];
        $scope.detailsGrid = {};
        var MAX_HISTORY_RANGE_IN_MONTHS = 3;
        $scope.fromDate = null;
        $scope.toDate = null;
        $scope.selectedShipments = [];

        $scope.deleteOrderDialog = {
            open: function () {
                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Delete Order', okFunction: $scope.deleteShipment,
                    message: 'Are you sure you want to delete Order with BOL ' + $scope.selectedShipments[0].bol + '?'
                });
            }
        };

        $scope.searchShipments = function () {
            $scope.selectedShipments.length = 0;
            $scope.getShipments();
        };

        $scope.clearFilters = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.selectedShipments.length = 0;
            $scope.shipments.length = 0;
        };

        $scope.getShipments = function () {
            TrackingBoardService.open({fromDate: $scope.fromDate, toDate: $scope.toDate}, function (data) {
                $scope.shipments = data;
            });
        };

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        $scope.viewShipment = function () {
            $scope.viewShipmentDetails($scope.selectedShipments[0]);
        };

        $scope.closeHandler = function () {
            $scope.getShipments();
            $scope.selectedShipments.length = 0;
        };

        $scope.viewShipmentDetails = function (entity) {
            $scope.openEditSalesOrderDialog({
                shipmentId: entity.id,
                closeHandler: $scope.closeHandler,
                formDisabled: true
            });
        };

        $scope.deleteShipment = function () {
            if (!_.isUndefined($scope.selectedShipments[0])) {
                $scope.cancelShipment($scope.selectedShipments[0], true).then(function (status) {
                    if (!_.isEmpty($scope.selectedShipments) && status === 'CANCELLED') {
                        var index = _.indexOf($scope.shipments, $scope.selectedShipments[0]);
                        if (index !== -1) {
                            $scope.shipments.splice(index, 1);
                        }
                    }
                });
            }
        };

        $scope.getRowFontStyle = function (entity) {
            return entity.apiCapable ? 'normal' : 'bold';
        };

        $scope.onShowTooltip = function (row) {
            ShipmentDetailsService.getTooltipData({
                customerId: $scope.$root.authData.organization.orgId,
                shipmentId: row.entity.id
            }, function (data) {
                if (data) {
                    $scope.tooltipData = data;
                }
            });
        };

        $scope.$watch('fromDate', function (newValue) {
            if (newValue) {
                $scope.minToDate = DateTimeUtils.parseISODate(newValue);
                $scope.maxToDate = DateTimeUtils.addMonths($scope.minToDate, MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.minToDate = undefined;
                $scope.maxToDate = undefined;
            }
        });

        $scope.$watch('toDate', function (newValue) {
            if (newValue) {
                $scope.maxFromDate = DateTimeUtils.parseISODate(newValue);
                $scope.minFromDate = DateTimeUtils.addMonths($scope.maxFromDate, -MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.maxFromDate = undefined;
                $scope.minFromDate = undefined;
            }
        });

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            columnDefs: [{
                field: 'indicators',
                displayName: 'Indicators',
                cellClass: 'text-center',
                width: '4%',
                cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
                    //$scope.shipments[0].indicators = "HG";
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                width: '5%'
            }, {
                field: 'id',
                displayName: 'Load ID',
                width: '3%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '7%',
                headerClass: 'text-center',
                cellClass: 'text-center',
                showTooltip: true
            }, {
                field: 'pieces',
                displayName: 'QTY',
                width: '3%'
            }, {
                field: 'weight',
                displayName: 'Total Weight',
                width: '4%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '8%',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'pickupDate',
                displayName: 'Estimated Pickup Date',
                width: '6%',
                cellFilter: "date:wideAppDateFormat",
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'pickupWindowEnd',
                displayName: 'Late Pickup Date/Time',
                width: '7%',
                cellFilter: "date:appDateTimeFormat"
            }, {
                field: 'shipper',
                displayName: 'Shipper',
                width: '7%',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'origin',
                displayName: 'Origin',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '11%'
            }, {
                field: 'consignee',
                displayName: 'Consignee',
                width: '7%',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'destination',
                displayName: 'Destination',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '11%'
            }, {
                field: 'createdBy',
                displayName: 'Created By',
                width: '6%'
            }, {
                field: 'createdDate',
                displayName: 'Created Date',
                width: '6%',
                cellFilter: "date:wideAppDateFormat"
            }
            ],
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_EDIT_SALES_ORDER')) {
                    $scope.viewShipment(entity);
                }
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
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
                onShow: $scope.onShowTooltip
            },
            progressiveSearch: true
        };
        if ($scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            $scope.shipmentsGrid.columnDefs.splice(1, 0, {
                field: 'network',
                displayName: 'Business Unit',
                width: '4%'
            });
            $scope.shipmentsGrid.columnDefs[10].width = '10%';
            $scope.shipmentsGrid.columnDefs[12].width = '10%';
        }
        $scope.getShipments();
    }
]);