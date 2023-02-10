angular.module('pls.controllers').controller('TrackingBoardUndeliveredController', [
    '$scope', '$timeout', 'TrackingBoardService', 'NgGridPluginFactory', '$rootScope',
    function ($scope, $timeout, TrackingBoardService, NgGridPluginFactory, $rootScope) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];
        $scope.personId = $scope.$root.authData.personId;

        var calculateTotal = function (entries) {
            $scope.totalRevenue = 0.0;
            $scope.totalCost = 0.0;
            $scope.totalMargin = 0.0;

            angular.forEach(entries, function (item) {
                if (item.revenue) {
                    $scope.totalRevenue += item.revenue;
                }

                if (item.cost) {
                    $scope.totalCost += item.cost;
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

        $scope.getShipments = function () {
            TrackingBoardService.undelivered({}, function (data) {
                $scope.shipments = data;
                calculateTotal(data);
            });
        };

        $scope.getShipments();

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        function deleteSelectedRow(selected) {
            var index = $scope.shipments.indexOf(selected);
            $scope.shipments.splice(index, 1);
            $scope.shipments = angular.copy($scope.shipments);
            $scope.selectedShipments.length = 0;
        }

        $scope.$watch('selectedShipments[0].status', function (newValue, oldValue) {
            if (newValue === 'CANCELLED') {
                deleteSelectedRow($scope.selectedShipments[0]);
            }
        });

        $scope.viewShipment = function () {
            $scope.viewShipmentDetails($scope.selectedShipments[0]);
        };

        $scope.closeHandler = function () {
            $scope.getShipments();
            $scope.selectedShipments.length = 0;
        };

        $scope.viewShipmentDetails = function (entity) {
            if ($scope.$root.isFieldRequired('VIEW_ACTIVE_SHIPMENTS_COST_DETAILS')) {
                $scope.openEditSalesOrderDialog({
                    shipmentId: entity.id, closeHandler: $scope.closeHandler,
                    selectedTab: 'tracking', formDisabled: true
                });
            } else {
                var shipmentDetailsOption = {
                    shipmentId: entity.id,
                    bol: entity.bol
                };

                $scope.$broadcast('event:showShipmentDetails', shipmentDetailsOption);
            }
        };

        $scope.openTerminalInfoModalDialog = function (shipmentId) {
            $scope.$broadcast('event:openTerminalInfoDialog', shipmentId);
        };

        //total sum should be updated when used progressive search and data in the grid is updated
        $scope.$watch('shipmentsGrid.ngGrid.filteredRows', function (newValue) {
            calculateTotal(_.map(newValue, function (item) {
                return item.entity;
            }));
        });

        var gridColumns = [{
            field: 'indicators',
            displayName: 'Indicators',
            cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
        }, {
            field: 'id',
            displayName: 'Load ID'
        }, {
            field: 'bol',
            displayName: 'BOL'
        }, {
            field: 'pieces',
            displayName: 'QTY'
        }, {
            field: 'weight',
            displayName: 'Total Weight'
        }, {
            field: 'pro',
            displayName: 'PRO #'
        }, {
            field: 'accountExecutive',
            displayName: 'Account Executive'
        }, {
            field: 'customerName',
            displayName: 'Customer'
        }, {
            field: 'carrierName',
            displayName: 'Carrier',
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
            '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>' +
            '</div>'
        }, {
            field : 'integrationType',
            displayName : 'EDI',
            cellFilter : 'integrationTypeFilter'
        }, {
            field: 'status',
            displayName: 'Status',
            cellFilter: 'shipmentStatus'
        }, {
            field: 'estimatedDelivery',
            displayName: 'Est Delivery',
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<span data-ng-bind="_.isUndefined(row.entity.estimatedDelivery) ?\'N/A\':row.entity.estimatedDelivery|date:appDateFormat">' +
                '</span></div>',
            cellFilter: 'date:appDateFormat'
        }, {
            field: 'shipper',
            displayName: 'Shipper'
        }, {
            field: 'origin',
            displayName: 'Origin'
        }, {
            field: 'consignee',
            displayName: 'Consignee'
        }, {
            field: 'destination',
            displayName: 'Destination'
        }, {
            field: 'createdBy',
            displayName: 'Created By'
        }, {
            field: 'createdDate',
            displayName: 'Created Date',
            cellFilter: "date:wideAppDateFormat"
        }, {
            field: 'pickupWindowEnd',
            displayName: 'Late Pickup Date/Time',
            cellFilter: "date:appDateTimeFormat"
        }, {
            field: 'dispatchedDate',
            displayName: 'Dispatched Date',
            cellFilter: "date:appDateTimeFormat"
        }, {
            field: 'glNumber',
            displayName: 'GL #'
        }, {
            field: 'srNumber',
            displayName: 'Shipper Reference #'
        }, {
            field: 'poNumber',
            displayName: 'PO #'
        }, {
            field: 'puNumber',
            displayName: 'PU #'
        }];

        if ($rootScope.isFieldRequired('VIEW_ACTIVE_SHIPMENTS_COST_DETAILS')) {
            gridColumns.push({
                field: 'revenue',
                displayName: 'Revenue',
                cellFilter: 'plsCurrency'
            }, {
                field: 'cost',
                displayName: 'Cost',
                cellFilter: 'plsCurrency',
                hideForReport: $scope.exportWithoutCostMargin()
            }, {
                field: 'margin',
                displayName: 'Margin',
                cellFilter: 'plsCurrency',
                hideForReport: $scope.exportWithoutCostMargin()
            });
        } else if ($rootScope.isFieldRequired('VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY')) {
            gridColumns.push({
                field: 'revenue',
                displayName: 'Total',
                cellFilter: 'plsCurrency'
            });
        }

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            columnDefs: gridColumns,
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_VIEW_SALES_ORDER')) {
                    $scope.viewShipment(entity);
                }
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), 
                      NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.hideColumnPlugin()],
            sortInfo: {
                fields: ['estimatedDelivery'],
                directions: ['asc']
            },
            progressiveSearch: true
        };

        if ($rootScope.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            gridColumns.splice(6, 0, {
                field: 'network',
                displayName: 'Business Unit'
            });
        }
    }
]);