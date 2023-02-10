angular.module('pls.controllers').controller('TrackingBoardUnbilledController', [
    '$scope', '$timeout', 'ShipmentStatus', 'TrackingBoardService', 'NgGridPluginFactory',
    'ShipmentDetailsService',
    function ($scope, $timeout, ShipmentStatus, TrackingBoardService, NgGridPluginFactory, ShipmentDetailsService) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];
        $scope.detailsGrid = {};

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
        };

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        $scope.init = function () {
            $scope.loadUnbilledShipments();
            
        };

        //total sum should be updated when used progressive search and data in the grid is updated
        $scope.$watch('shipmentsGrid.ngGrid.filteredRows', function (newValue) {
            calculateTotal(_.map(newValue, function (item) {
                return item.entity;
            }));
        });

        $scope.loadUnbilledShipments = function () {
            TrackingBoardService.unbilled({}, function (data) {
                $scope.shipmentsData = data;
                calculateTotal(data);
            });
        };

        $scope.closeHandler = function () {
            $scope.loadUnbilledShipments();
            $scope.selectedShipments.length = 0;
        };

        $scope.editSalesOrder = function () {
            $scope.openEditSalesOrderDialog({
                shipmentId: $scope.selectedShipments[0].shipmentId, closeHandler: $scope.closeHandler,
                selectedTab: 'vendor_bills'
            });
        };

        function onShowTooltip(row) {
            ShipmentDetailsService.getTooltipData({
                customerId: $scope.$root.authData.organization.orgId,
                shipmentId: row.entity.shipmentId
            }, function (data) {
                if (data) {
                    $scope.tooltipData = data;
                }
            });
        }

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipmentsData',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            columnDefs: [{
                field: 'indicators',
                displayName: 'Indicators',
                cellClass: 'text-center',
                cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
            }, {
                field: 'shipmentId',
                displayName: 'Load ID',
                cellClass: 'text-center'
            }, {
                field: 'bolNumber',
                displayName: 'BOL',
                headerClass: 'text-center',
                cellClass: 'text-center',
                showTooltip: true
            }, {
                field: 'pieces',
                displayName: 'QTY'
            }, {
                field: 'weight',
                displayName: 'Total Weight'
            }, {
                field: 'proNumber',
                displayName: 'PRO#',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                 field: 'accountExecutive',
                 displayName: 'Account Executive'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'carrier',
                displayName: 'Carrier',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'shipper',
                displayName: 'Shipper',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'origin',
                cellFilter: 'zip',
                displayName: 'Origin',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'consignee',
                displayName: 'Consignee',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'destination',
                cellFilter: 'zip',
                displayName: 'Destination',
                headerClass: 'text-center',
                cellClass: 'text-center'
            }, {
                field: 'deliveryDate',
                displayName: 'Del. Date',
                headerClass: 'text-center',
                cellClass: 'text-center',
                cellFilter: "date:wideAppDateFormat"
            }, {
                field: 'billToRecieved',
                displayName: 'Reason',
                cellFilter: 'booleanFilter:"Date Hold":"Waiting for VB"'
            }, {
                field: 'createdBy',
                displayName: 'Created By'
            }, {
                field: 'createdDate',
                displayName: 'Created Date',
                cellFilter: "date:wideAppDateFormat"
            }, {
                field: 'revenue',
                displayName: 'Revenue',
                cellFilter: 'plsCurrency'

            }, {
                field: 'total',
                displayName: 'Cost',
                cellFilter: 'plsCurrency'
            }, {
                field: 'margin',
                displayName: 'Margin',
                cellFilter: 'plsCurrency'
            }],
            action: function () {
                if ($scope.$root.isFieldRequired('BOARD_CAN_EDIT_SALES_ORDER')) {
                    $scope.editSalesOrder();
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
                NgGridPluginFactory.hideColumnPlugin()],
            sortInfo: {
                fields: ['deliveryDate'],
                directions: ['asc']
            },
            tooltipOptions: {
                url: 'pages/content/quotes/shipments-grid-tooltip.html',
                onShow: onShowTooltip
            },
            progressiveSearch: true
        };

        if ($scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            $scope.shipmentsGrid.columnDefs.splice(6, 0, {
                field: 'network',
                displayName: 'Business Unit',
                headerClass: 'text-center',
                cellClass: 'text-center'
            });
        }

        $scope.OverrideDateHold = function () {
            if ($scope.selectedShipments[0] && $scope.selectedShipments[0].billToRecieved) {
                ShipmentStatus.overridedate({shipmentId: $scope.selectedShipments[0].shipmentId}, function () {
                    $scope.closeHandler();
                });
            }
        };
    }
]);