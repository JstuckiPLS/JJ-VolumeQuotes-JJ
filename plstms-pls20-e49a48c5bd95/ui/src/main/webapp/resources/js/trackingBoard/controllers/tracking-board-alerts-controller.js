angular.module('pls.controllers').controller('TrackingBoardAlertsController', [
    '$scope', '$timeout', 'TrackingBoardAlertService', 'NgGridPluginFactory', '$sortService',
    function ($scope, $timeout, TrackingBoardAlertService, NgGridPluginFactory, $sortService) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];

        $scope.init = function () {
            $scope.loadAlerts();
        };

        $scope.loadAlerts = function () {
            TrackingBoardAlertService.alert({}, function (data) {
                $scope.shipments = data.sort(function (a, b) {
                    if (a.newAlert !== b.newAlert) {
                        return -$sortService.sortBool(a.newAlert, b.newAlert);
                    }

                    return -$sortService.sortNumber(a.id, b.id);
                });
            });
        };

        $scope.editShipment = function () {
            if ($scope.selectedShipments[0].newAlert) {
                TrackingBoardAlertService.acknowledgeAlerts({shipmentId: $scope.selectedShipments[0].id}, function () {
                    $scope.selectedShipments[0].newAlert = false;

                    $scope.$root.$broadcast('event:trackingBoardAlertsChanged');
                });
            }

            $scope.editShipmentDetails($scope.selectedShipments[0]);
        };

        $scope.closeHandler = function () {
            $scope.loadAlerts();
            $scope.selectedShipments.length = 0;
        };

        $scope.editShipmentDetails = function (entity) {
            $scope.openEditSalesOrderDialog({shipmentId: entity.id, closeHandler: $scope.closeHandler, selectedTab: 'tracking', formDisabled: true});
        };

        $scope.getRowFontStyle = function (entity) {
            return entity.newAlert ? 'bold' : 'normal';
        };

        $scope.getRowBackgroundColorStyle = function (entity) {
            var redColoredAlertTypes = ['NDL', '30M'];
            var alertTypes = entity.alertTypes.split(",");
            var needToBeColored = false;

            _.each(alertTypes, function (alertType) {
                if (_.contains(redColoredAlertTypes, alertType)) {
                    needToBeColored = true;
                }
            });

            return needToBeColored;
        };

        $scope.openTerminalInfoModalDialog = function (shipmentId) {
            $scope.$broadcast('event:openTerminalInfoDialog', shipmentId);
        };

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            primaryKey: 'id',
            rowTemplate: "<div data-ng-class=\"{'text-error' : getRowBackgroundColorStyle(row.entity)}\">"
            + "<div data-ng-style=\"{ 'cursor': row.cursor, 'font-weight': getRowFontStyle(row.entity) }\""
            + " ng-repeat=\"col in renderedColumns\" ng-class=\"col.colIndex()\" class=\"ngCell {{col.cellClass}}\" ng-cell></div></div>",
            columnDefs: [{
                field: 'indicators',
                displayName: 'Indicators',
                width: '3%',
                cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                width: '4%'
            }, {
                field: 'id',
                displayName: 'Load ID',
                width: '4%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '6%'
            }, {
                field: 'pro',
                displayName: 'Pro#',
                width: '6%'
            }, {
                field: 'pieces',
                displayName: 'QTY',
                width: '2%'
            }, {
                field: 'weight',
                displayName: 'Total Weight',
                width: '3%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '6%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '6%',
                cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>' +
                '</div>'
            }, {
                field: 'integrationType',
                displayName: 'EDI',
                width: '3%',
                cellFilter: "integrationTypeFilter"
            }, {
                field: 'shipmentStatus',
                displayName: 'Status',
                width: '2%',
                cellFilter: "shipmentStatus"
            }, {
                field: 'pickupDate',
                displayName: 'Pickup Date',
                width: '6%',
                cellFilter: "date:appDateTimeFormat"
            }, {
                field: 'dispatchedDate',
                displayName: 'Dispatched Date',
                width: '6%',
                cellFilter: "date:appDateTimeFormat"
            }, {
                field: 'estimatedDelivery',
                displayName: 'Est Delivery Date',
                width: '4%',
                cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                    '<span data-ng-bind="_.isUndefined(row.entity.estimatedDelivery) ?\'N/A\':row.entity.estimatedDelivery|date:appDateFormat">' +
                    '</span></div>',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'shipper',
                displayName: 'Shipper',
                width: '6%'
            }, {
                field: 'originAddress',
                displayName: 'Origin',
                width: '6%',
                cellFilter: 'zip'
            }, {
                field: 'consignee',
                displayName: 'Consignee',
                width: '6%'
            }, {
                field: 'destinationAddress',
                displayName: 'Destination',
                width: '6%',
                cellFilter: 'zip'
            }, {
                field: 'alertTypes',
                displayName: 'Alert',
                width: '3%'
            }, {
                field: 'createdBy',
                displayName: 'Created By',
                width: '5%'
            }, {
                field: 'createdDateTime',
                displayName: 'Created Date',
                width: '6%',
                cellFilter: "date:appDateTimeFormat"
            }, {
                field: 'pickupWindowEnd',
                displayName: 'Late Pickup Date/Time',
                width: '6%',
                cellFilter: "date:appDateTimeFormat"
            }
            ],
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_VIEW_SALES_ORDER')) {
                    $scope.editShipment(entity);
                }
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(),
                      NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.hideColumnPlugin()],
            progressiveSearch: true
        };

        if ($scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            $scope.shipmentsGrid.columnDefs.splice(1, 0, {
                field: 'network',
                displayName: 'Business Unit',
                width: '3%'
            });
            $scope.shipmentsGrid.columnDefs[12].width = '5%';
            $scope.shipmentsGrid.columnDefs[13].width = '5%';
            $scope.shipmentsGrid.columnDefs[20].width = '5%';
            $scope.shipmentsGrid.columnDefs[21].width = '5%';
        }
    }
]);